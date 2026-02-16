package org.iesvdm.ecommerce_jpa.repository;

import org.iesvdm.ecommerce_jpa.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, Long> {
}