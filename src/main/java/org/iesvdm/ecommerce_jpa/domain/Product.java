package org.iesvdm.ecommerce_jpa.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "category")
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(length = 120, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    private String descrip;

    @Column(name = "image_url", length = 120)
    private String imageUrl;

    @NotBlank
    @Size(max = 120)
    @Column(length = 120, unique = true, nullable = false)
    private String sku;

    @PositiveOrZero
    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Long quantity;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private Set<CartItem> cartItems = new java.util.LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;
}
