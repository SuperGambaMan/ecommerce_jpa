package org.iesvdm.ecommerce_jpa.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "products")
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 120, nullable = false, unique = true)
    private String name;

    @Size(max = 120)
    @Column(name = "descrip", length = 120)
    private String descrip;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id DESC")
    @JsonManagedReference
    private Set<Product> products = new java.util.LinkedHashSet<>();
}
