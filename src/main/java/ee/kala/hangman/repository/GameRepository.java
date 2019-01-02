package ee.kala.hangman.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.kala.hangman.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}
