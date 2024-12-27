package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.Models.Game;

@Repository
public interface GameRepository extends JpaRepository<Game,Long>{
    
}
