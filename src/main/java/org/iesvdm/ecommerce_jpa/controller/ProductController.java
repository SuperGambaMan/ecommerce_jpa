package org.iesvdm.ecommerce_jpa.controller;

import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.iesvdm.ecommerce_jpa.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 404);
                        put("error", "Not Found");
                        put("message", "Producto no encontrado");
                        put("path", "/api/productos/" + id);
                    }});
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        if (!productService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 404);
                        put("error", "Not Found");
                        put("message", "Producto no encontrado");
                        put("path", "/api/productos/" + id);
                    }});
        }
        if (product.getCategory() == null || product.getCategory().getId() == null || !categoryRepository.existsById(product.getCategory().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 400);
                        put("error", "Bad Request");
                        put("message", "La categoría no existe");
                        put("path", "/api/productos/" + id);
                    }});
        }
        try {
            Product updated = productService.update(id, product);
            return ResponseEntity.ok(updated);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 400);
                        put("error", "Bad Request");
                        put("message", "El SKU ya existe");
                        put("path", "/api/productos/" + id);
                    }});
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 500);
                        put("error", "Internal Server Error");
                        put("message", "Error al actualizar el producto");
                        put("path", "/api/productos/" + id);
                    }});
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product saved = productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", ex.getStatusCode().value());
                        put("error", ex.getReason());
                        put("message", ex.getReason());
                        put("path", "/api/productos");
                    }});
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 400);
                        put("error", "Bad Request");
                        put("message", "El SKU ya existe");
                        put("path", "/api/productos");
                    }});
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 500);
                        put("error", "Internal Server Error");
                        put("message", "Error al crear el producto");
                        put("path", "/api/productos");
                    }});
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!productService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 404);
                        put("error", "Not Found");
                        put("message", "Producto no encontrado");
                        put("path", "/api/productos/" + id);
                    }});
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    // Se puede usar ResponseEntity<?> para devolver diferentes tipos de respuestas (éxito, error, etc.)
    // Ó tambien se podria haber usado Page<Product> directamente y lanzar excepciones para los casos de error,
    // pero con ResponseEntity se tiene más control sobre el código de estado y el cuerpo de la respuesta.
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sku,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Validación de parámetros
        if (page < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'page' no puede ser negativo");
        if (size <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'size' debe ser mayor que 0");
        if (!sortBy.equals("id") && !sortBy.equals("name") && !sortBy.equals("price"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'sortBy' solo puede ser 'id', 'name' o 'price'");
        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'direction' solo puede ser 'asc' o 'desc'");
        Page<Product> result = productService.searchProducts(name, categoryId, sku, page, size, sortBy, direction);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("status", 404);
                        put("error", "Not Found");
                        put("message", "No se encontró ningún producto");
                        put("path", "/api/productos/search");
                    }});
        }
        return ResponseEntity.ok(result);
    }
}
