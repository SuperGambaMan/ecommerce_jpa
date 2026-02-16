package org.iesvdm.ecommerce_jpa.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 120, nullable = false)
    private String email;

    @Column(length = 120, nullable = false)
    private String username;

    @Column(length = 120, nullable = false)
    private String password;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<CartItem> cartItems = new java.util.LinkedHashSet<>();
}
