package ee.kala.hangman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import ee.kala.hangman.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	Player findByName(@Param("name") String name);
}
