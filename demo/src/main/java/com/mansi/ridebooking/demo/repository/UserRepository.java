package com.mansi.ridebooking.demo.repository;
import com.mansi.ridebooking.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndStatus(String role, String status);
}