package org.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    private BigDecimal amount;

    private PaymentType paymentType;

    private String reference;

    private String bankName;

    private LocalDate dueDate;
}
