package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.domain.Category;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.domain.User;
import org.iesvdm.ecommerce_jpa.repository.ProductRepository;
import org.iesvdm.ecommerce_jpa.repository.CategoryRepository;
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
public class ProductCategoryCartItemUserTests {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeAll
    public static void cleanAll(@Autowired ProductRepository productRepository,
                                @Autowired CategoryRepository categoryRepository,
                                @Autowired CartItemRepository cartItemRepository,
                                @Autowired UserRepository userRepository) {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Order(1)
    @Test
    void crearProductCategoryCartItemUserTest() {
        transactionTemplate.executeWithoutResult(status -> {
            Category category = Category.builder()
                    .name("Electrónica")
                    .descrip("Dispositivos electrónicos")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            Product product = Product.builder()
                    .name("Smartphone")
                    .descrip("Teléfono móvil")
                    .imageUrl("http://img.com/smart.jpg")
                    .sku("SKU001")
                    .price(BigDecimal.valueOf(299.99))
                    .quantity(50L)
                    .category(category)
                    .cartItems(new HashSet<>())
                    .build();
            product = productRepository.save(product);

            User user = User.builder()
                    .email("user@email.com")
                    .username("usuario1")
                    .password("pass123")
                    .cartItems(new HashSet<>())
                    .build();
            user = userRepository.save(user);

            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(2L)
                    .build();
            cartItem = cartItemRepository.save(cartItem);

            product.getCartItems().add(cartItem);
            category.getProducts().add(product);
            user.getCartItems().add(cartItem);
            productRepository.save(product);
            categoryRepository.save(category);
            userRepository.save(user);

            assertNotNull(cartItem.getId());
        });
    }

    @Order(2)
    @Test
    void leerProductCategoryCartItemUserTest() {
        transactionTemplate.executeWithoutResult(status -> {
            Category category = Category.builder()
                    .name("Electrónica2")
                    .descrip("Dispositivos electrónicos 2")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            Product product = Product.builder()
                    .name("Smartphone2")
                    .descrip("Teléfono móvil 2")
                    .imageUrl("http://img.com/smart2.jpg")
                    .sku("SKU002")
                    .price(BigDecimal.valueOf(399.99))
                    .quantity(20L)
                    .category(category)
                    .cartItems(new HashSet<>())
                    .build();
            product = productRepository.save(product);

            User user = User.builder()
                    .email("user2@email.com")
                    .username("usuario2")
                    .password("pass456")
                    .cartItems(new HashSet<>())
                    .build();
            user = userRepository.save(user);

            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(1L)
                    .build();
            cartItem = cartItemRepository.save(cartItem);

            product.getCartItems().add(cartItem);
            category.getProducts().add(product);
            user.getCartItems().add(cartItem);
            productRepository.save(product);
            categoryRepository.save(category);
            userRepository.save(user);

            // Validar relaciones
            Category catReloaded = categoryRepository.findById(category.getId()).orElse(null);
            assertNotNull(catReloaded);
            assertFalse(catReloaded.getProducts().isEmpty());
            Product prodReloaded = productRepository.findById(product.getId()).orElse(null);
            assertNotNull(prodReloaded);
            assertFalse(prodReloaded.getCartItems().isEmpty());
            CartItem cartReloaded = cartItemRepository.findById(cartItem.getId()).orElse(null);
            assertNotNull(cartReloaded);
            assertNotNull(cartReloaded.getUser());
        });
    }

    @Order(3)
    @Test
    void desasociarCartItemTest() {
        transactionTemplate.executeWithoutResult(status -> {
            // Crear toda la cadena
            Category category = Category.builder()
                    .name("Electrónica3")
                    .descrip("Dispositivos electrónicos 3")
                    .products(new HashSet<>())
                    .build();
            category = categoryRepository.save(category);

            Product product = Product.builder()
                    .name("Smartphone3")
                    .descrip("Teléfono móvil 3")
                    .imageUrl("http://img.com/smart3.jpg")
                    .sku("SKU003")
                    .price(BigDecimal.valueOf(499.99))
                    .quantity(10L)
                    .category(category)
                    .cartItems(new HashSet<>())
                    .build();
            product = productRepository.save(product);

            User user = User.builder()
                    .email("user3@email.com")
                    .username("usuario3")
                    .password("pass789")
                    .cartItems(new HashSet<>())
                    .build();
            user = userRepository.save(user);

            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(1L)
                    .build();
            cartItem = cartItemRepository.save(cartItem);

            product.getCartItems().add(cartItem);
            category.getProducts().add(product);
            user.getCartItems().add(cartItem);
            productRepository.save(product);
            categoryRepository.save(category);
            userRepository.save(user);

            // Eliminar el cart item
            cartItemRepository.delete(cartItem);
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el cart item ha sido eliminado
            assertFalse(cartItemRepository.findById(cartItem.getId()).isPresent());
        });
    }
}
