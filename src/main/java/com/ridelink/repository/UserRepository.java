package com.ridelink.repository;

import com.ridelink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    //METHOD for admin functionality
    List<User> findByAccountStatus(String accountStatus);

    List<User> findByRole(String role);

}