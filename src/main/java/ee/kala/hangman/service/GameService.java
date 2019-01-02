package ee.kala.hangman.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ee.kala.hangman.converter.PlayerDtoConverter;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.entity.Answer;
import ee.kala.hangman.entity.Game;
import ee.kala.hangman.entity.Player;
import ee.kala.hangman.model.ErrorResponse;
import ee.kala.hangman.model.GameStatus;
import ee.kala.hangman.model.Guess;
import ee.kala.hangman.repository.AnswerRepository;
import ee.kala.hangman.repository.GameRepository;
import ee.kala.hangman.repository.PlayerRepository;
import ee.kala.hangman.rest.error.ExceptionWithErrorResponse;

import static ee.kala.hangman.model.GameStatus.LOST;
import static ee.kala.hangman.model.GameStatus.ONGOING;
import static ee.kala.hangman.model.GameStatus.WON;

@Service
public class GameService {

	@Resource
	private GameRepository gameRepository;

	@Resource
	private PlayerRepository playerRepository;
	
	@Resource
	private AnswerRepository answerRepository;
	
	@Resource
	private PlayerDtoConverter playerDtoConverter;

	private static final String EMPTY_STRING = "";

	private static final String OBFUSCATED_PLACEHOLDER = "*";

	@Transactional(readOnly = true)
	public List<Game> listGames() {
		return gameRepository.findAll();
	}

	@Transactional
	public Game startGame(PlayerDto playerDto) throws ExceptionWithErrorResponse {
		Player player = getExistingPlayer(playerDto);
		
		Answer answer = getRandomAnswer();
		Game game = prepareNewGame(player, answer);
		
		gameRepository.save(game);
		
		return game;
	}

	@Transactional
	public Game fetchGameInfo(Long gameId) throws ExceptionWithErrorResponse {
		Game game = gameRepository.findOne(gameId);
		if (game == null) {
			throw new ExceptionWithErrorResponse(ErrorResponse.GAME_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
		}
		return game;
	}

	@Transactional
	public Game guess(Long gameId, Guess guess) throws ExceptionWithErrorResponse {
		if (guess == null || guess.getLetter().length() > 1 || guess.getLetter().isEmpty()) {
			throw new ExceptionWithErrorResponse(ErrorResponse.GUESS_WAS_NOT_MADE, HttpStatus.NOT_FOUND);
		}

		Game game = gameRepository.findOne(gameId);

		validate(game);

		game.setGuesses(getGuesses(game));
		game.setGuessesLeft(getGuessesLeft(game, guess));

		game.setIncorrectLetters(getIncorrectLetters(game, guess));

		game.setGuessedWord(getGuessedWord(game, guess));
		game.setGameStatus(getGameStatus(game));

		gameRepository.save(game);

		return game;
	}

	@Transactional
	public Game giveUp(Long gameId) throws ExceptionWithErrorResponse {
		Game game = gameRepository.findOne(gameId);
		if (game == null) {
			throw new ExceptionWithErrorResponse(ErrorResponse.GAME_WAS_NOT_FOUND, HttpStatus.NOT_FOUND);
		}
		if (game.getGameStatus() != ONGOING) {
			throw new IllegalArgumentException("Invalid game status");
		}

		game.setGuessedWord(game.getAnswer().getValue());
		game.setGameStatus(LOST);

		return game;
	}

	private Player getExistingPlayer(PlayerDto playerDto) throws ExceptionWithErrorResponse {
		Player player = playerRepository.findByName(playerDto.getName());
		if (player == null) {
			throw new IllegalArgumentException("Player was not found");
		}
		playerDtoConverter.copy(playerDto, player);
		return player;
	}

	private Game prepareNewGame(Player player, Answer answer) {
		Game game = new Game();
		game.setAnswer(answer);
		game.setPlayer(player);
		game.setGuesses(0);
		game.setGuessesLeft(answer.getValue().length());
		game.setGuessedWord(EMPTY_STRING);
		game.setIncorrectLetters(EMPTY_STRING);
		game.setGameStatus(GameStatus.ONGOING);
		return game;
	}

	private Answer getRandomAnswer() {
		Long count = answerRepository.count();
		return answerRepository.findOne(getLongBetweenMinAndMax(1L, count));
	}

	private long getLongBetweenMinAndMax(long min, long max) {
		return min + (long) (Math.random() * (max - min));
	}

	private void validate(Game game) {
		if (game == null) {
			throw new IllegalArgumentException("Game does not exist");
		}

		if (game.getGameStatus() != ONGOING) {
			throw new IllegalArgumentException("Invalid game status");
		}

		if (game.getGuessesLeft() == 0) {
			throw new IllegalArgumentException("Player has no guesses left");
		}
	}

	private String getIncorrectLetters(Game game, Guess guess) {
		boolean correctGuess = isCorrectGuess(game, guess);
		String currentIncorrectLetters = game.getIncorrectLetters();
		String guessLetter = guess.getLetter();
		if (correctGuess) {
			return currentIncorrectLetters;
		} else {
			return currentIncorrectLetters + guessLetter;
		}
	}

	private Integer getGuessesLeft(Game game, Guess guess) {
		boolean correctGuess = isCorrectGuess(game, guess);
		boolean wasNotGuessedLetter = !isGuessedLetter(game, guess);
		Integer currentGuessesLeft = game.getGuessesLeft();
		if (correctGuess && wasNotGuessedLetter) {
			return currentGuessesLeft;
		} else {
			return currentGuessesLeft - 1;
		}
	}

	private Integer getGuesses(Game game) {
		Integer currentGuesses = game.getGuesses();
		return currentGuesses + 1;
	}

	private boolean isCorrectGuess(Game game, Guess guess) {
		String answer = game.getAnswer().getValue().toUpperCase();
		String guessLetter = guess.getLetter().toUpperCase();
		return answer.contains(guessLetter);
	}
	
	private boolean isGuessedLetter(Game game, Guess guess) {
		String guessedWord = game.getGuessedWord().toUpperCase();
		String guessLetter = guess.getLetter().toUpperCase();
		return guessedWord.contains(guessLetter);
	}

	private GameStatus getGameStatus(Game game) {
		String answer = game.getAnswer().getValue();
		
		if (game.getGuessedWord().equalsIgnoreCase(answer)) {
			return WON;
		} else if (game.getGuessesLeft() == 0) {
			return LOST;
		} else {
			return ONGOING;
		}
	}

	private String getGuessedWord(Game game, Guess guess) {
		if (!isCorrectGuess(game, guess)) {
			return game.getGuessedWord();
		}

		String answer  = game.getAnswer().getValue();
		String guessLetter = guess.getLetter();

		String[] answerArray = answer.split(EMPTY_STRING);
		String[] obfuscatedWord = getObfuscatedWord(game);
		
		for (int i = 0; i < answerArray.length; i++) {
			if (answerArray[i].equalsIgnoreCase(guessLetter)) {
				obfuscatedWord[i] = guessLetter;
			}
		}
		return String.join(EMPTY_STRING, obfuscatedWord);

	}

	private String[] getObfuscatedWord(Game game) {
		String answer  = game.getAnswer().getValue();
		String guessedWord = game.getGuessedWord();

		if (!guessedWord.isEmpty()) {
			return guessedWord.split(EMPTY_STRING);
		}

		StringBuilder obfuscatedWord = new StringBuilder();

		for (int i = 0; i < answer.length(); i++) {
			obfuscatedWord.append(OBFUSCATED_PLACEHOLDER);
		}

		return obfuscatedWord.toString().split(EMPTY_STRING);

	}
}
