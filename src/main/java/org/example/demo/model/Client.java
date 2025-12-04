package org.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.demo.enums.CustomerTier;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerTier fidelityLevel = CustomerTier.BASIC;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalSpent = 0.0;

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime firstOrderDate;

    private LocalDateTime lastOrderDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private java.util.List<Order> orders;
}
