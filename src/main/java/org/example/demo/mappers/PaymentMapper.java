package org.example.demo.mappers;

import org.example.demo.dto.response.PaymentResponseDTO;
import org.example.demo.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDTO toResponse(Payment payment) {
        PaymentResponseDTO res = new PaymentResponseDTO();
        res.setId(payment.getId());
        res.setOrderId(payment.getOrder().getId());
        res.setPaymentNumber(payment.getPaymentNumber());
        res.setAmount(payment.getAmount());
        res.setPaymentType(payment.getPaymentType());
        res.setPaymentDate(payment.getPaymentDate());
        res.setCashDate(payment.getCashDate());
        res.setStatus(payment.getStatus());
        res.setReference(payment.getReference());
        res.setBankName(payment.getBankName());
        res.setDueDate(payment.getDueDate());
        return res;
    }
}
