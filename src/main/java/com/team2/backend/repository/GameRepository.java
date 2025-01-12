package com.team2.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.models.Game;
import com.team2.backend.models.User;

public interface GameRepository extends JpaRepository<Game, Long> {
  List<Game> findByUser(User user);

  List<Game> findByAppId(Integer appid);

  List<Game> findByUserAndAppId(User user, Integer appid);

  void deleteByUserAndAppId(User user, Integer appid);

  Optional<Game> findByAppId(Long appid);


  



  
}
