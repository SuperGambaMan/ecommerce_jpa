package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.Category;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.repository.CategoryRepository;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.iesvdm.ecommerce_jpa.repository.CartItemRepository;
import org.iesvdm.ecommerce_jpa.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ProductCartItemRelationTests {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;
    @Autowired
    UserRepository userRepository;

    @BeforeAll
    public static void cleanAll(@Autowired ProductRepository productRepository,
                                @Autowired CartItemRepository cartItemRepository,
                                @Autowired CategoryRepository categoryRepository) {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(1)
    void crearProductConCartItems() {
        transactionTemplate.executeWithoutResult(status -> {
            // Crear y guardar categoría
            var category = Category.builder()
                    .name("Electrónica")
                    .descrip("Dispositivos electrónicos")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            // Crear y guardar usuario
            var user = org.iesvdm.ecommerce_jpa.domain.User.builder()
                    .email("user1@correo.com")
                    .username("user1")
                    .password("pass1")
                    .cartItems(new HashSet<>())
                    .build();
            user = userRepository.save(user);

            Product product = Product.builder()
                    .name("Tablet")
                    .descrip("Tablet Android")
                    .sku("SKU002")
                    .price(BigDecimal.valueOf(199.99))
                    .quantity(30L)
                    .category(category)
                    .cartItems(new HashSet<>())
                    .build();
            product = productRepository.save(product);

            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .user(user)
                    .quantity(1L)
                    .build();
            user.getCartItems().add(cartItem);
            userRepository.save(user);
            cartItem = cartItemRepository.save(cartItem);

            product.getCartItems().add(cartItem);
            productRepository.save(product);

            assertNotNull(cartItem.getId());
        });
    }

    @Test
    @Order(2)
    void borrarCartItemDeProduct() {
        transactionTemplate.executeWithoutResult(status -> {
            // Crear y guardar categoría
            var category = Category.builder()
                    .name("Electrónica2")
                    .descrip("Dispositivos electrónicos 2")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            // Crear y guardar usuario
            var user = org.iesvdm.ecommerce_jpa.domain.User.builder()
                    .email("user2@correo.com")
                    .username("user2")
                    .password("pass2")
                    .cartItems(new HashSet<>())
                    .build();
            user = userRepository.save(user);

            // Crear producto y cart item
            Product product = Product.builder()
                    .name("Tablet2")
                    .descrip("Tablet Android 2")
                    .sku("SKU003")
                    .price(BigDecimal.valueOf(299.99))
                    .quantity(10L)
                    .category(category)
                    .cartItems(new HashSet<>())
                    .build();
            product = productRepository.save(product);

            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .user(user)
                    .quantity(2L)
                    .build();
            user.getCartItems().add(cartItem);
            userRepository.save(user);
            cartItem = cartItemRepository.save(cartItem);
            product.getCartItems().add(cartItem);
            productRepository.save(product);

            // Eliminar el cart item
            product.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
            productRepository.save(product);
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el cart item ha sido eliminado
            assertFalse(cartItemRepository.findById(cartItem.getId()).isPresent());
        });
    }

    @Test
    @Order(3)
    void borrarProductoYCartItems() {
        transactionTemplate.executeWithoutResult(status -> {
            // Crear y guardar categoría
            var category = Category.builder()
                    .name("Electrónica3")
                    .descrip("Dispositivos electrónicos 3")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            // Crear y guardar usuario
            var user = org.iesvdm.ecommerce_jpa.domain.User.builder()
                    .email("user3@correo.com")
                    .username("user3")
                    .password("pass3")
                    .cartItems(new HashSet<>())
                    .build();
            user = userRepository.save(user);

            // Crear producto y cart item
            Product product = Product.builder()
                    .name("Tablet3")
                    .descrip("Tablet Android 3")
                    .sku("SKU004")
                    .price(BigDecimal.valueOf(399.99))
                    .quantity(5L)
                    .category(category)
                    .cartItems(new HashSet<>())
                    .build();
            product = productRepository.save(product);

            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .user(user)
                    .quantity(3L)
                    .build();
            user.getCartItems().add(cartItem);
            userRepository.save(user);
            cartItem = cartItemRepository.save(cartItem);
            product.getCartItems().add(cartItem);
            productRepository.save(product);

            Long cartItemId = cartItem.getId();
            productRepository.delete(product);
            //Para asegurar que se ejecuta la eliminación en cascada y se refleja en la base de datos
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el cart item ha sido eliminado
            assertFalse(cartItemRepository.findById(cartItemId).isPresent());
        });
    }
}
