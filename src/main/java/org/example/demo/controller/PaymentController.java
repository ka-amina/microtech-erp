package org.example.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.aop.RequiresAdmin;
import org.example.demo.dto.request.PaymentRequestDTO;
import org.example.demo.dto.request.PaymentStatusUpdateDTO;
import org.example.demo.dto.response.PaymentResponseDTO;
import org.example.demo.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/payments")
    @RequiresAdmin
    public ResponseEntity<PaymentResponseDTO> addPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequestDTO req) {
        PaymentResponseDTO payment = paymentService.addPayment(orderId, req);
        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/payments/{paymentId}/status")
    @RequiresAdmin
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentStatusUpdateDTO req) {
        PaymentResponseDTO payment = paymentService.updatePaymentStatus(paymentId, req);
        return ResponseEntity.ok(payment);
    }
}
