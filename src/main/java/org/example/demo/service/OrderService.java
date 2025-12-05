package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.OrderRequestDTO;
import org.example.demo.dto.response.OrderResponseDTO;
import org.example.demo.enums.OrderStatus;
import org.example.demo.exception.InsufficientStockException;
import org.example.demo.exception.InvalidOrderException;
import org.example.demo.exception.OrderStatusException;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.mappers.OrderMapper;
import org.example.demo.model.*;
import org.example.demo.repository.ClientRepository;
import org.example.demo.repository.OrderRepository;
import org.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Value("${app.vat.rate:0.20}")
    private BigDecimal vatRate;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO req) {
        // Validate request
        if (req.getClientId() == null) {
            throw new InvalidOrderException("Client ID is required");
        }
        if (req.getOrderItems() == null || req.getOrderItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }

        // Find client
        Client client = clientRepository.findById(req.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + req.getClientId() + " not found"));

        // Create order
        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());
        order.setPromoCode(req.getPromoCode());

        // Create order items and validate stock
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (var itemReq : req.getOrderItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + itemReq.getProductId() + " not found"));

            // Check if product is deleted
            if (product.getIsDeleted()) {
                throw new InvalidOrderException("Product " + product.getName() + " is no longer available");
            }

            // Validate stock availability
            if (product.getStockQuantity() < itemReq.getQuantity()) {
                order.setStatus(OrderStatus.REJECTED);
                order.setSubtotal(BigDecimal.ZERO);
                order.setDiscount(BigDecimal.ZERO);
                order.setVat(BigDecimal.ZERO);
                order.setTotal(BigDecimal.ZERO);
                order.setRemainingAmount(BigDecimal.ZERO);
                orderRepository.save(order);
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() 
                    + ". Available: " + product.getStockQuantity() + ", Requested: " + itemReq.getQuantity());
            }

            // Create order item
            BigDecimal itemTotal = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .totalPrice(itemTotal)
                    .build();
            
            orderItems.add(orderItem);
            subtotal = subtotal.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));

        // Calculate loyalty discount based on client's current tier
        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client, subtotal);
        
        // Calculate promo code discount (5% if valid)
        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (req.getPromoCode() != null && !req.getPromoCode().isEmpty()) {
            if (isValidPromoCode(req.getPromoCode())) {
                promoDiscount = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            } else {
                throw new InvalidOrderException("Invalid promo code format. Must be PROMO-XXXX");
            }
        }
        
        // Cumulative discounts (loyalty + promo)
        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount).setScale(2, RoundingMode.HALF_UP);
        order.setDiscount(totalDiscount);

        // Calculate amounts
        BigDecimal subtotalAfterDiscount = subtotal.subtract(totalDiscount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal vat = subtotalAfterDiscount.multiply(vatRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalAfterDiscount.add(vat).setScale(2, RoundingMode.HALF_UP);

        order.setVat(vat);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order.setRemainingAmount(total);

        // Decrement product stock
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }



    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        // Validate order status - only PENDING orders can be canceled
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStatusException("Cannot cancel order. Only PENDING orders can be canceled. Current status: " + order.getStatus());
        }

        // Restore product stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // Update order status
        order.setStatus(OrderStatus.CANCELED);
        Order savedOrder = orderRepository.save(order);
        
        return orderMapper.toResponse(savedOrder);
    }


    @Transactional
    public OrderResponseDTO rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        // Validate order status - only PENDING orders can be rejected
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStatusException("Cannot reject order. Only PENDING orders can be rejected. Current status: " + order.getStatus());
        }

        // Restore product stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // Update order status
        order.setStatus(OrderStatus.REJECTED);
        Order savedOrder = orderRepository.save(order);
        
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponseDTO confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        // Validate order status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStatusException("Cannot confirm order. Only PENDING orders can be confirmed. Current status: " + order.getStatus());
        }

        // Update order status
        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        // Update client statistics
        Client client = order.getClient();
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent() + order.getTotal().doubleValue());
        
        // Update first order date if this is the first order
        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(order.getOrderDate());
        }
        
        // Update last order date
        client.setLastOrderDate(order.getOrderDate());
        
        // Recalculate and update client tier based on new statistics
        updateClientTier(client);
        
        clientRepository.save(client);
        
        return orderMapper.toResponse(savedOrder);
    }

    // Get all orders for a specific client
    public List<OrderResponseDTO> getOrdersByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + clientId + " not found"));
        
        List<Order> orders = orderRepository.findByClientOrderByOrderDateDesc(client);
        
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }


    private BigDecimal calculateLoyaltyDiscount(Client client, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (client.getFidelityLevel()) {
            case SILVER:
                if (subtotal.compareTo(new BigDecimal("500")) >= 0) {
                    discount = subtotal.multiply(new BigDecimal("0.05"));
                }
                break;
            case GOLD:
                if (subtotal.compareTo(new BigDecimal("800")) >= 0) {
                    discount = subtotal.multiply(new BigDecimal("0.10"));
                }
                break;
            case PLATINUM:
                if (subtotal.compareTo(new BigDecimal("1200")) >= 0) {
                    discount = subtotal.multiply(new BigDecimal("0.15"));
                }
                break;
            case BASIC:
            default:
                discount = BigDecimal.ZERO;
                break;
        }
        
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private void updateClientTier(Client client) {
        int totalOrders = client.getTotalOrders();
        double totalSpent = client.getTotalSpent();
        
        if (totalOrders >= 20 || totalSpent >= 15000) {
            client.setFidelityLevel(org.example.demo.enums.CustomerTier.PLATINUM);
        } else if (totalOrders >= 10 || totalSpent >= 5000) {
            client.setFidelityLevel(org.example.demo.enums.CustomerTier.GOLD);
        } else if (totalOrders >= 3 || totalSpent >= 1000) {
            client.setFidelityLevel(org.example.demo.enums.CustomerTier.SILVER);
        } else {
            client.setFidelityLevel(org.example.demo.enums.CustomerTier.BASIC);
        }
    }

//    validate promo code
    private boolean isValidPromoCode(String promoCode) {
        if (promoCode == null || promoCode.isEmpty()) {
            return false;
        }
        return promoCode.matches("PROMO-[A-Z0-9]{4}");
    }

}
