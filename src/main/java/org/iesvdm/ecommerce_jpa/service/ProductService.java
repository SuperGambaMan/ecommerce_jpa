package org.iesvdm.ecommerce_jpa.service;

import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.iesvdm.ecommerce_jpa.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> searchProducts(String name, Long categoryId, String sku, int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, _query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (categoryId != null) {
            spec = spec.and((root, _query, cb) -> {
                Join<Object, Object> join = root.join("category");
                return cb.equal(join.get("id"), categoryId);
            });
        }
        if (sku != null && !sku.isEmpty()) {
            spec = spec.and((root, _query, cb) -> cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%"));
        }
        return productRepository.findAll(spec, pageable);
    }

    public Product saveProduct(Product product) {
        if (product.getCategory() == null || !categoryRepository.existsById(product.getCategory().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe");
        }
        return productRepository.save(product);
    }
}
