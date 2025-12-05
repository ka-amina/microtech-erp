package org.example.demo.service;

import org.example.demo.dto.request.OrderItemRequestDTO;
import org.example.demo.dto.request.OrderRequestDTO;
import org.example.demo.dto.response.OrderResponseDTO;
import org.example.demo.enums.CustomerTier;
import org.example.demo.enums.OrderStatus;
import org.example.demo.exception.InsufficientStockException;
import org.example.demo.exception.InvalidOrderException;
import org.example.demo.exception.OrderStatusException;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.mappers.OrderMapper;
import org.example.demo.model.Client;
import org.example.demo.model.Order;
import org.example.demo.model.OrderItem;
import org.example.demo.model.Product;
import org.example.demo.repository.ClientRepository;
import org.example.demo.repository.OrderRepository;
import org.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Client client;
    private Product product;
    private Order order;
    private OrderRequestDTO orderRequestDTO;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        // Set VAT rate
        ReflectionTestUtils.setField(orderService, "vatRate", new BigDecimal("0.20"));

        // Initialize client
        client = Client.builder()
                .id(1L)
                .fullName("Test Client")
                .email("test@example.com")
                .phone("0612345678")
                .address("Test Address")
                .build();

        // Initialize product
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .unitPrice(new BigDecimal("100.00"))
                .stockQuantity(50)
                .isDeleted(false)
                .build();

        // Initialize order item for request
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(1L);
        orderItemRequestDTO.setQuantity(5);

        // Initialize order request
        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setClientId(1L);
        orderRequestDTO.setOrderItems(List.of(orderItemRequestDTO));

        // Initialize order
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(5)
                .unitPrice(new BigDecimal("100.00"))
                .totalPrice(new BigDecimal("500.00"))
                .build();

        order = Order.builder()
                .id(1L)
                .client(client)
                .orderItems(List.of(orderItem))
                .orderDate(LocalDateTime.now())
                .subtotal(new BigDecimal("500.00"))
                .discount(new BigDecimal("0.00"))
                .vat(new BigDecimal("100.00"))
                .total(new BigDecimal("600.00"))
                .status(OrderStatus.PENDING)
                .remainingAmount(new BigDecimal("600.00"))
                .build();

        orderItem.setOrder(order);

        // Initialize order response
        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setStatus(OrderStatus.PENDING);
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(result);
        verify(clientRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createOrder_WhenClientNotFound_ShouldThrowException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.createOrder(orderRequestDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.createOrder(orderRequestDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WhenInsufficientStock_ShouldThrowException() {
        // Arrange
        product.setStockQuantity(2); // Less than requested 5
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () ->
                orderService.createOrder(orderRequestDTO));
    }

    @Test
    void createOrder_WhenProductIsDeleted_ShouldThrowException() {
        // Arrange
        product.setIsDeleted(true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(InvalidOrderException.class, () ->
                orderService.createOrder(orderRequestDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WhenClientIdIsNull_ShouldThrowException() {
        // Arrange
        orderRequestDTO.setClientId(null);

        // Act & Assert
        assertThrows(InvalidOrderException.class, () ->
                orderService.createOrder(orderRequestDTO));
    }

    @Test
    void createOrder_WhenOrderItemsIsEmpty_ShouldThrowException() {
        // Arrange
        orderRequestDTO.setOrderItems(new ArrayList<>());

        // Act & Assert
        assertThrows(InvalidOrderException.class, () ->
                orderService.createOrder(orderRequestDTO));
    }

    @Test
    void createOrder_WhenOrderItemsIsNull_ShouldThrowException() {
        // Arrange
        orderRequestDTO.setOrderItems(null);

        // Act & Assert
        assertThrows(InvalidOrderException.class, () ->
                orderService.createOrder(orderRequestDTO));
    }

    @Test
    void createOrder_WithValidPromoCode_ShouldApplyDiscount() {
        // Arrange
        orderRequestDTO.setPromoCode("PROMO-AB12");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidPromoCode_ShouldThrowException() {
        // Arrange
        orderRequestDTO.setPromoCode("INVALID");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(InvalidOrderException.class, () ->
                orderService.createOrder(orderRequestDTO));
    }

    @Test
    void createOrder_WithSilverTierAndEligibleSubtotal_ShouldApplyLoyaltyDiscount() {
        // Arrange
        client.setFidelityLevel(CustomerTier.SILVER);
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(1L);
        orderItemRequestDTO.setQuantity(6); // 600 DH subtotal
        orderRequestDTO.setOrderItems(List.of(orderItemRequestDTO));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void confirmOrder_ShouldUpdateOrderStatusToConfirmed() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.confirmOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void confirmOrder_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.confirmOrder(1L));
    }

    @Test
    void confirmOrder_WhenOrderNotPending_ShouldThrowException() {
        // Arrange
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderStatusException.class, () ->
                orderService.confirmOrder(1L));
    }

    @Test
    void confirmOrder_ShouldUpdateClientStatistics() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        orderService.confirmOrder(1L);

        // Assert
        assertEquals(1, client.getTotalOrders());
        assertTrue(client.getTotalSpent() > 0);
        assertNotNull(client.getFirstOrderDate());
        assertNotNull(client.getLastOrderDate());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void confirmOrder_ShouldUpdateClientTierToSilver_WhenCriteriaIsMet() {
        // Arrange
        client.setTotalOrders(2); // After confirmation, will be 3
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        orderService.confirmOrder(1L);

        // Assert
        assertEquals(CustomerTier.SILVER, client.getFidelityLevel());
    }

    @Test
    void cancelOrder_ShouldUpdateOrderStatusToCanceled() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void cancelOrder_ShouldRestoreProductStock() {
        // Arrange
        int initialStock = product.getStockQuantity();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(initialStock + 5, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void cancelOrder_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.cancelOrder(1L));
    }

    @Test
    void cancelOrder_WhenOrderNotPending_ShouldThrowException() {
        // Arrange
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderStatusException.class, () ->
                orderService.cancelOrder(1L));
    }

    @Test
    void rejectOrder_ShouldUpdateOrderStatusToRejected() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.rejectOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, order.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void rejectOrder_ShouldRestoreProductStock() {
        // Arrange
        int initialStock = product.getStockQuantity();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        orderService.rejectOrder(1L);

        // Assert
        assertEquals(initialStock + 5, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void rejectOrder_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.rejectOrder(1L));
    }

    @Test
    void rejectOrder_WhenOrderNotPending_ShouldThrowException() {
        // Arrange
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderStatusException.class, () ->
                orderService.rejectOrder(1L));
    }

    @Test
    void getOrdersByClient_ShouldReturnListOfOrders() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(orderRepository.findByClientOrderByOrderDateDesc(client)).thenReturn(List.of(order));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        List<OrderResponseDTO> result = orderService.getOrdersByClient(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByClientOrderByOrderDateDesc(client);
    }

    @Test
    void getOrdersByClient_WhenClientNotFound_ShouldThrowException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.getOrdersByClient(1L));
    }

    @Test
    void createOrder_ShouldDecrementProductStock() {
        // Arrange
        int initialStock = product.getStockQuantity();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        orderService.createOrder(orderRequestDTO);

        // Assert
        assertEquals(initialStock - 5, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void createOrder_WithMultipleProducts_ShouldCalculateCorrectSubtotal() {
        // Arrange
        Product product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
                .unitPrice(new BigDecimal("200.00"))
                .stockQuantity(20)
                .isDeleted(false)
                .build();

        OrderItemRequestDTO item1 = new OrderItemRequestDTO();
        item1.setProductId(1L);
        item1.setQuantity(5);

        OrderItemRequestDTO item2 = new OrderItemRequestDTO();
        item2.setProductId(2L);
        item2.setQuantity(2);

        orderRequestDTO.setOrderItems(List.of(item1, item2));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(2)).save(any(Product.class));
    }
}
