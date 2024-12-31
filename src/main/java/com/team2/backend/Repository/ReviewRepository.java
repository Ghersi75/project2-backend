package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.Models.Game;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long>{
    List<Review> findByUser(User user);

    List<Review> findByGame(Game game);
}
