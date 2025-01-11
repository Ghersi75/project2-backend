package com.team2.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.models.Game;
import com.team2.backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByUsername(String username);

    Optional<User> findById(User user);


    List<User> findByFavoriteGamesContaining(Game game);
}
