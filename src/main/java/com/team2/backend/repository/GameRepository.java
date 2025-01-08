package com.team2.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.models.Game;
import com.team2.backend.models.User;

import java.util.List;


public interface GameRepository extends JpaRepository<Game,Long> {
  List<Game> findByUser(User user);

  List<Game> findByAppid(Integer appid);

  List<Game> findByUserAndAppid(User user, Integer appid);

  void deleteByUserAndAppid(User user, Integer appid);
}
