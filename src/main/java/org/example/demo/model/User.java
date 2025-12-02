package org.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.demo.enums.UserRole;

import java.time.LocalDateTime;

@Entity
@Table(name= "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String  userName;

    @Column(nullable=false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private UserRole role;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
