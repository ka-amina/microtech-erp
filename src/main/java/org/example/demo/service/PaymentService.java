package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.PaymentRequestDTO;
import org.example.demo.dto.response.PaymentResponseDTO;
import org.example.demo.enums.OrderStatus;
import org.example.demo.enums.PaymentStatus;
import org.example.demo.enums.PaymentType;
import org.example.demo.exception.InvalidOrderException;
import org.example.demo.exception.OrderStatusException;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.mappers.PaymentMapper;
import org.example.demo.model.Order;
import org.example.demo.model.Payment;
import org.example.demo.repository.OrderRepository;
import org.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    private static final BigDecimal CASH_LIMIT = new BigDecimal("20000"); 

    @Transactional
    public PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO req) {
        // Find order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        // Validate order status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStatusException("Cannot add payment. Only PENDING orders can receive payments. Current status: " + order.getStatus());
        }

        // Validate payment amount doesn't exceed remaining amount
        if (req.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new InvalidOrderException("Payment amount (" + req.getAmount() + " DH) exceeds remaining amount (" + order.getRemainingAmount() + " DH)");
        }

        // Validate ESPECES limit
        if (req.getPaymentType() == PaymentType.ESPECES && req.getAmount().compareTo(CASH_LIMIT) > 0) {
            throw new InvalidOrderException("Cash payment exceeds legal limit of 20,000 DH (Article 193 CGI)");
        }

        // Validate CHEQUE requirements
        if (req.getPaymentType() == PaymentType.CHEQUE) {
            if (req.getBankName() == null || req.getBankName().isEmpty()) {
                throw new InvalidOrderException("Bank name is required for CHEQUE payments");
            }
            if (req.getDueDate() == null) {
                throw new InvalidOrderException("Due date is required for CHEQUE payments");
            }
        }

        // Validate VIREMENT requirements
        if (req.getPaymentType() == PaymentType.VIREMENT) {
            if (req.getBankName() == null || req.getBankName().isEmpty()) {
                throw new InvalidOrderException("Bank name is required for VIREMENT payments");
            }
        }

        // Calculate next payment number
        long paymentCount = paymentRepository.countByOrder(order);
        Integer nextPaymentNumber = (int) (paymentCount + 1);

        // Determine payment status
        PaymentStatus status;
        if (req.getPaymentType() == PaymentType.ESPECES) {
            status = PaymentStatus.ENCAISSE; // Cash is immediately cashed
        } else {
            status = PaymentStatus.EN_ATTENTE; // CHEQUE and VIREMENT start as pending
        }

        // Create payment
        Payment payment = Payment.builder()
                .order(order)
                .paymentNumber(nextPaymentNumber)
                .amount(req.getAmount().setScale(2, RoundingMode.HALF_UP))
                .paymentType(req.getPaymentType())
                .paymentDate(LocalDateTime.now())
                .status(status)
                .reference(req.getReference())
                .bankName(req.getBankName())
                .dueDate(req.getDueDate())
                .cashDate(status == PaymentStatus.ENCAISSE ? java.time.LocalDate.now() : null)
                .build();

        // Update order remaining amount
        BigDecimal newRemainingAmount = order.getRemainingAmount().subtract(req.getAmount()).setScale(2, RoundingMode.HALF_UP);
        order.setRemainingAmount(newRemainingAmount);

        // Save payment and order
        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.save(order);

        // Map to response
        PaymentResponseDTO response = paymentMapper.toResponse(savedPayment);
        response.setRemainingAmount(newRemainingAmount);

        return response;
    }
}
