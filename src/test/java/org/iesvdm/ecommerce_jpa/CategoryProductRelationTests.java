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
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());

        Category category = new Category();
        category.setName("Electrónica_" + uniqueSuffix);
        category.setDescription("Dispositivos electrónicos");
        category.setProducts(new HashSet<>());

        Product product1 = new Product();
        product1.setName("Smartphone_" + uniqueSuffix);
        product1.setDescrip("Teléfono móvil");
        product1.setSku("SKU_" + uniqueSuffix);
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
        transactionTemplate.execute(status -> {
            String uniqueSuffix = String.valueOf(System.currentTimeMillis());

            // Crear categoría con producto
            Category category = new Category();
            category.setName("TestOrphan_" + uniqueSuffix);
            category.setDescription("Test orphan removal");
            category.setProducts(new HashSet<>());

            Product product = new Product();
            product.setName("ProductoOrphan_" + uniqueSuffix);
            product.setDescrip("Descripción test");
            product.setSku("SKU_ORPHAN_" + uniqueSuffix);
            product.setPrice(java.math.BigDecimal.valueOf(100.00));
            product.setUnits(10);
            product.setCategory(category);
            category.getProducts().add(product);

            categoryRepository.save(category);
            Long productId = product.getId();
            Long categoryId = category.getId();
            assertNotNull(productId);

            entityManager.flush();
            entityManager.clear();

            // Buscar la categoría y eliminar el producto
            Category savedCategory = categoryRepository.findById(categoryId).orElse(null);
            assertNotNull(savedCategory);
            Set<Product> products = savedCategory.getProducts();
            assertFalse(products.isEmpty());

            products.clear();
            categoryRepository.save(savedCategory);
            entityManager.flush();
            entityManager.clear();

            // Verificar que el producto fue eliminado por orphanRemoval
            assertFalse(productRepository.findById(productId).isPresent());
            return null;
        });
    }
}

