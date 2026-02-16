package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.Category;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.repository.CategoryRepository;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class CategoryProductRelationTests {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeAll
    public static void cleanAll(@Autowired ProductRepository productRepository,
                                @Autowired CategoryRepository categoryRepository) {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Order(1)
    @Test
    void crearCategoryProductTest() {
        Category category = Category.builder()
                .name("Electrónica")
                .descrip("Dispositivos electrónicos")
                .products(new HashSet<>())
                .build();
        category = categoryRepository.save(category);

        Product product = Product.builder()
                .name("Smartphone")
                .descrip("Teléfono móvil")
                .sku("SKU001")
                .price(BigDecimal.valueOf(299.99))
                .quantity(50L)
                .category(category)
                .build();
        category.getProducts().add(product);
        productRepository.save(product);
    }

    @Order(2)
    @Test
    void leerCategoryYProductsPorCategory() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            List<Category> listCategory = this.categoryRepository.findAll();
            listCategory.forEach(category -> {
                Set<Product> setProducts = category.getProducts();
                setProducts.forEach(product -> {
                    System.out.println(product);
                });
            });
        });
    }

    @Order(3)
    @Test
    void desasociarProduct() {
        transactionTemplate.executeWithoutResult(status -> {
            // Crear categoría y producto
            Category category = Category.builder()
                    .name("PruebaDesasociar")
                    .descrip("Categoría para test de desasociación")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            Product product = Product.builder()
                    .name("ProductoDesasociar")
                    .descrip("Producto para test de desasociación")
                    .sku("SKU-TEST-DESASOC")
                    .price(BigDecimal.valueOf(10.0))
                    .quantity(1L)
                    .category(category)
                    .build();
            category.getProducts().add(product);
            product = productRepository.save(product);

            // Buscar el producto por id y la categoría por id (simulando la relación intermedia)
            Product productToDelete = productRepository.findById(product.getId()).orElse(null);
            assertNotNull(productToDelete);
            productRepository.delete(productToDelete);
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el producto ha sido eliminado
            assertFalse(productRepository.findById(product.getId()).isPresent());
        });
    }
}
