package org.example.demo.repository;

import org.example.demo.model.Client;
import org.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientOrderByOrderDateDesc(Client client);
}
