package org.example.demo.mappers;

import org.example.demo.dto.request.ProductRequestDTO;
import org.example.demo.dto.request.ProductRequestUpdateDTO;
import org.example.demo.dto.response.ProductResponseDTO;
import org.example.demo.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO req) {
        Product product = new Product();
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setUnitPrice(req.getUnitPrice());
        product.setStockQuantity(req.getStockQuantity());
        return product;
    }

    public ProductResponseDTO toResponse(Product product) {
        ProductResponseDTO res = new ProductResponseDTO();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setDescription(product.getDescription());
        res.setUnitPrice(product.getUnitPrice());
        res.setStockQuantity(product.getStockQuantity());
        return res;
    }

    public void updateEntity(Product product, ProductRequestUpdateDTO req) {
        if (req.getName() != null) {
            product.setName(req.getName());
        }
        if (req.getDescription() != null) {
            product.setDescription(req.getDescription());
        }
        if (req.getUnitPrice() != null) {
            product.setUnitPrice(req.getUnitPrice());
        }
        if (req.getStockQuantity() != null) {
            product.setStockQuantity(req.getStockQuantity());
        }
    }
}
