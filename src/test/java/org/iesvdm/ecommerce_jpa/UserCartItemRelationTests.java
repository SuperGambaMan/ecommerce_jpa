package org.iesvdm.ecommerce_jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.ecommerce_jpa.domain.User;
import org.iesvdm.ecommerce_jpa.domain.CartItem;
import org.iesvdm.ecommerce_jpa.domain.Category;
import org.iesvdm.ecommerce_jpa.domain.Product;
import org.iesvdm.ecommerce_jpa.repository.UserRepository;
import org.iesvdm.ecommerce_jpa.repository.CartItemRepository;
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
public class UserCartItemRelationTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartItemRepository cartItemRepository;
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
    public static void cleanAll(@Autowired UserRepository userRepository,
                                @Autowired CartItemRepository cartItemRepository,
                                @Autowired CategoryRepository categoryRepository) {
        cartItemRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(1)
    void crearUserConCartItems() {
        transactionTemplate.executeWithoutResult(status -> {
            // Crear y guardar categoría
            Category category = Category.builder()
                    .name("Electrónica")
                    .descrip("Dispositivos electrónicos")
                    .build();
            category = categoryRepository.save(category);
            // Crear y guardar producto
            Product product = Product.builder()
                    .name("Tablet")
                    .descrip("Tablet Android")
                    .sku("SKU002")
                    .price(java.math.BigDecimal.valueOf(199.99))
                    .quantity(30L)
                    .category(category)
                    .build();
            product = productRepository.save(product);
            // Crear y guardar usuario
            User user = User.builder()
                    .email("test@correo.com")
                    .username("testuser")
                    .password("pass")
                    .build();
            user = userRepository.save(user);
            // Crear y guardar cartItem
            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(3L)
                    .build();
            cartItemRepository.save(cartItem);
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el usuario y el cartItem existen y están relacionados
            User userDB = userRepository.findById(user.getId()).orElse(null);
            assertNotNull(userDB);
            assertEquals(1, userDB.getCartItems().size());
            CartItem cartItemDB = userDB.getCartItems().iterator().next();
            assertEquals(product.getId(), cartItemDB.getProduct().getId());
        });
    }

    @Test
    @Order(2)
    void orphanRemovalCartItem() {
        transactionTemplate.executeWithoutResult(status -> {
            // Recuperar usuario y cartItem
            User user = userRepository.findAll().stream().findFirst().orElse(null);
            assertNotNull(user);
            assertFalse(user.getCartItems().isEmpty());
            CartItem cartItem = user.getCartItems().iterator().next();
            // Eliminar el cartItem de la colección y guardar usuario
            user.getCartItems().remove(cartItem);
            userRepository.save(user);
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el cart item ha sido eliminado
            assertFalse(cartItemRepository.findById(cartItem.getId()).isPresent());
        });
    }

    @Test
    @Order(3)
    void borrarUsuarioYCartItems() {
        transactionTemplate.executeWithoutResult(status -> {
            // Recuperar usuario
            User user = userRepository.findAll().stream().findFirst().orElse(null);
            assertNotNull(user);
            Long userId = user.getId();
            // Borrar usuario
            userRepository.delete(user);
            entityManager.flush();
            entityManager.clear();
            // Comprobar que el usuario y sus cartItems han sido eliminados
            assertFalse(userRepository.findById(userId).isPresent());
            assertEquals(0, cartItemRepository.findAll().size());
        });
    }
}
