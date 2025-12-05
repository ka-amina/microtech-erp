package org.example.demo.repository;

import org.example.demo.model.Order;
import org.example.demo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderOrderByPaymentNumberAsc(Order order);
    long countByOrder(Order order);
}
