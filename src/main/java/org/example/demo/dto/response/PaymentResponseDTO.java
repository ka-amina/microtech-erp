package org.example.demo.dto.response;

import lombok.Data;
import org.example.demo.enums.PaymentStatus;
import org.example.demo.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long id;
    private Long orderId;
    private Integer paymentNumber;
    private BigDecimal amount;
    private PaymentType paymentType;
    private LocalDateTime paymentDate;
    private LocalDate cashDate;
    private PaymentStatus status;
    private String reference;
    private String bankName;
    private LocalDate dueDate;
    private BigDecimal remainingAmount; 
}
