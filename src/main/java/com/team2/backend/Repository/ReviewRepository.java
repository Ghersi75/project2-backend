package com.team2.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.models.Review;
import com.team2.backend.models.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long>{
    List<Review> findByUser(User user);

    List<Review> findByAppid(Integer appid);
}
