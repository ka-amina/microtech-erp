package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.ProductRequestDTO;
import org.example.demo.dto.request.ProductRequestUpdateDTO;
import org.example.demo.dto.response.ProductResponseDTO;
import org.example.demo.exception.DuplicateResourceException;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.mappers.ProductMapper;
import org.example.demo.model.Product;
import org.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDTO createProduct(ProductRequestDTO req) {
        if (productRepository.existsByName(req.getName())) {
            throw new DuplicateResourceException("Product with name '" + req.getName() + "' already exists");
        }
        Product product = productMapper.toEntity(req);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestUpdateDTO req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        if (req.getName() != null && !req.getName().equals(product.getName())) {
            if (productRepository.existsByName(req.getName())) {
                throw new DuplicateResourceException("Product with name '" + req.getName() + "' already exists");
            }
        }
        productMapper.updateEntity(product, req);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    public ProductResponseDTO deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        product.setIsDeleted(true);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

}
