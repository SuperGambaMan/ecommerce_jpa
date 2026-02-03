package org.iesvdm.ecommerce_jpa.repository;

import org.iesvdm.ecommerce_jpa.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

