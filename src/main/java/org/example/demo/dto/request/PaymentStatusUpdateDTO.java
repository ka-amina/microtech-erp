package org.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.demo.enums.PaymentStatus;

import java.time.LocalDate;

@Data
public class PaymentStatusUpdateDTO {

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

    private LocalDate cashDate;
}
