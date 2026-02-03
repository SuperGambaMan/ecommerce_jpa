package org.iesvdm.ecommerce_jpa.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @Size(max = 4000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    @JsonManagedReference
    private java.util.Set<Product> products = new java.util.LinkedHashSet<>();
}
