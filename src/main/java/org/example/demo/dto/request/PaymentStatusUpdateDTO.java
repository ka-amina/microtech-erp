package org.example.demo.dto.request;

import lombok.Data;
import org.example.demo.enums.PaymentStatus;

import java.time.LocalDate;

@Data
public class PaymentStatusUpdateDTO {

    private PaymentStatus status;

    private LocalDate cashDate;
}
