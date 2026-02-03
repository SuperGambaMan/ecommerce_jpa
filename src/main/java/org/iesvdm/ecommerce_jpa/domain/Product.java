package org.iesvdm.ecommerce_jpa.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String name;

    private String descrip;
    private String image_url;

    @NotBlank
    @Size(max = 120)
    @Column(unique = true, nullable = false)
    private String sku;

    @PositiveOrZero
    @Column(nullable = false)
    private java.math.BigDecimal price;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer units;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private Set<CartItem> cartItems = new java.util.LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;
}
