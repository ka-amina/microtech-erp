package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.OrderRequestDTO;
import org.example.demo.dto.response.OrderResponseDTO;
import org.example.demo.enums.OrderStatus;
import org.example.demo.exception.InsufficientStockException;
import org.example.demo.exception.InvalidOrderException;
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

        // For now, discount is 0 (will be implemented later with fidelity system)
        BigDecimal discount = BigDecimal.ZERO;
        order.setDiscount(discount);

        // Calculate amounts
        BigDecimal subtotalAfterDiscount = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
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

}
