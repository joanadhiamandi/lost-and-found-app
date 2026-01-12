package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {

    AppUser findByUsername(String username);

    AppUser findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
