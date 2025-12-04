package org.example.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestUpdateDTO {

    private String name;
    private String description;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
}
