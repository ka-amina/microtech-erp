package org.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @Size(min = 3, max = 100, message = "Reference must be between 3 and 100 characters")
    private String reference;

    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bankName;

    private LocalDate dueDate;
}
