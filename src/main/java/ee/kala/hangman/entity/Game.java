package ee.kala.hangman.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ee.kala.hangman.model.GameStatus;

import lombok.Data;

@Data
@Entity
@Table(name="GAME")
public class Game {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER)
	private Player player;
	private Integer guesses;
	private Integer guessesLeft;
	private String guessedWord;
	private String incorrectLetters;
	private GameStatus gameStatus;
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnore
	private Answer answer;

}
