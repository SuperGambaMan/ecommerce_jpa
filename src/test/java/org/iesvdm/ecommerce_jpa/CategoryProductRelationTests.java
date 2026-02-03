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

import java.util.HashSet;
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

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(1)
    void crearCategoryConProducts() {
        Category category = new Category();
        category.setName("Electrónica");
        category.setDescription("Dispositivos electrónicos");
        category.setProducts(new HashSet<>());

        Product product1 = new Product();
        product1.setName("Smartphone");
        product1.setDescrip("Teléfono móvil");
        product1.setSku("SKU001");
        product1.setPrice(java.math.BigDecimal.valueOf(299.99));
        product1.setUnits(50);
        product1.setCategory(category);
        category.getProducts().add(product1);

        categoryRepository.save(category);
        assertNotNull(product1.getId());
    }

    @Test
    @Order(2)
    void orphanRemovalProduct() {
        Category category = categoryRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(category);
        Set<Product> products = category.getProducts();
        Product product = products.stream().findFirst().orElse(null);
        assertNotNull(product);
        products.remove(product);
        categoryRepository.save(category);
        assertFalse(productRepository.findById(product.getId()).isPresent());
    }
}

