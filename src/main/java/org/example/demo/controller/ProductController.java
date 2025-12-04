package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ProductRequestDTO;
import org.example.demo.dto.request.ProductRequestUpdateDTO;
import org.example.demo.dto.response.ClientResponseDTO;
import org.example.demo.dto.response.ProductResponseDTO;
import org.example.demo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct( @RequestBody ProductRequestDTO req) {
        ProductResponseDTO productResponseDTO = productService.createProduct(req);
        return ResponseEntity.ok(productResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,@RequestBody ProductRequestUpdateDTO req) {
        ProductResponseDTO product = productService.updateProduct(id, req);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> deleteProduct(@PathVariable Long id) {
        ProductResponseDTO product = productService.deleteProduct(id);
        return ResponseEntity.ok(product);
    }
}
