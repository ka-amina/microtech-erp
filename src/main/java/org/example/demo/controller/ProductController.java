package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ProductRequestDTO;
import org.example.demo.dto.request.ProductRequestUpdateDTO;
import org.example.demo.dto.response.ProductResponseDTO;
import org.example.demo.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductResponseDTO> products = productService.getAllProducts(pageable);
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
