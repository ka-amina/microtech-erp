package org.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponseDTO {

    private long id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
    private boolean isDeleted;
}
