package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.Models.Game;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game,Long>{
    Optional<Game> findByAppid(String appid);
}
