package repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.entity.User;



public interface UserRepository extends JpaRepository<User, Integer>{

    // Check if a user with the given username exists
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    
}
