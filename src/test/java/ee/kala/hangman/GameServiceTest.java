package ee.kala.hangman;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ee.kala.hangman.converter.PlayerDtoConverter;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.entity.Answer;
import ee.kala.hangman.entity.Game;
import ee.kala.hangman.entity.Player;
import ee.kala.hangman.model.GameStatus;
import ee.kala.hangman.model.Guess;
import ee.kala.hangman.repository.AnswerRepository;
import ee.kala.hangman.repository.GameRepository;
import ee.kala.hangman.repository.PlayerRepository;
import ee.kala.hangman.service.GameService;

public class GameServiceTest {

	@Mock
	private GameRepository gameRepository;
	
	@Mock
	private AnswerRepository answerRepository;
	
	@Mock
	private PlayerRepository playerRepository;
	
	@Mock
	private PlayerDtoConverter playerDtoConverter;
	
	@InjectMocks
	private GameService gameService;

	private static final String EMPTY_STRING = "";

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testStartGame() throws Exception {
		Game game = new Game();
		game.setId(1L);
		
		Player player = new Player();
		player.setId(1L);
		player.setAge(10);
		player.setName("User");
		
		PlayerDto playerDto = new PlayerDto();
		playerDto.setAge(10);
		playerDto.setName("User");

		Answer answer = new Answer();
		answer.setId(1L);
		answer.setValue("guessword");
		
		game.setAnswer(answer);
		game.setPlayer(player);

		when(playerRepository.findByName(playerDto.getName())).thenReturn(player);
		when(answerRepository.findOne(anyLong())).thenReturn(answer);
		when(playerDtoConverter.copy(playerDto, player)).thenReturn(player);
		when(gameRepository.save(game)).thenReturn(game);

		Game returnedGame = gameService.startGame(playerDto);
		
		assertNotNull(returnedGame.getPlayer());
		assertNotNull(returnedGame.getGuesses());
		assertNotNull(returnedGame.getGuessesLeft());
		assertNotNull(returnedGame.getGuessedWord());
		assertNotNull(returnedGame.getIncorrectLetters());
		assertNotNull(returnedGame.getGameStatus());
		assertNotNull(returnedGame.getAnswer());
		
		assertEquals(game.getAnswer().getId(), returnedGame.getAnswer().getId());
		assertEquals(game.getPlayer().getId(), returnedGame.getPlayer().getId());
		assertEquals(GameStatus.ONGOING, returnedGame.getGameStatus());
	}
	
	@Test
	public void testGuess() throws Exception {
		Player player = new Player();
		
		Answer answer = new Answer();
		answer.setId(1L);
		answer.setValue("guessword");

		Game game = new Game();
		game.setId(1L);
		game.setPlayer(player);
		game.setAnswer(answer);
		game.setGuessedWord(EMPTY_STRING);
		game.setGuessesLeft(9);
		game.setGuesses(0);
		game.setGameStatus(GameStatus.ONGOING);
		game.setIncorrectLetters(EMPTY_STRING);
		
		Guess guess1 = new Guess();
		guess1.setLetter("s");

		Guess guess2 = new Guess();
		guess2.setLetter("a");

		when(gameRepository.findOne(game.getId())).thenReturn(game);
		when(gameRepository.save(game)).thenReturn(game);
		
		// correct guess
		Game returnedGame1 = gameService.guess(game.getId(), guess1);
		assertNotNull(returnedGame1.getGuessedWord());
		assertNotNull(returnedGame1.getPlayer());
		assertNotNull(returnedGame1.getAnswer());
		assertEquals(GameStatus.ONGOING, returnedGame1.getGameStatus());
		assertEquals(new BigDecimal(1), new BigDecimal(returnedGame1.getGuesses()));
		assertEquals(new BigDecimal(9), new BigDecimal(returnedGame1.getGuessesLeft()));
		assertEquals(new BigDecimal(answer.getValue().length()), new BigDecimal(returnedGame1.getGuessesLeft()));
		assertEquals(EMPTY_STRING, returnedGame1.getIncorrectLetters());
		assertEquals("***ss****", returnedGame1.getGuessedWord());

		// incorrect guess
		Game returnedGame2 = gameService.guess(game.getId(), guess2);
		assertNotNull(returnedGame2.getGuessedWord());
		assertNotNull(returnedGame2.getPlayer());
		assertNotNull(returnedGame2.getAnswer());
		assertEquals(GameStatus.ONGOING, returnedGame2.getGameStatus());
		assertEquals(new BigDecimal(2), new BigDecimal(returnedGame2.getGuesses()));
		assertEquals(new BigDecimal(8), new BigDecimal(returnedGame2.getGuessesLeft()));
		assertEquals("a", returnedGame2.getIncorrectLetters());
		assertEquals("***ss****", returnedGame2.getGuessedWord());
		
	}

	@Test
	public void testPlayerWin() throws Exception {
		Player player = new Player();

		Answer answer = new Answer();
		answer.setId(1L);
		answer.setValue("guessword");

		Game game = new Game();
		game.setId(1L);
		game.setPlayer(player);
		game.setAnswer(answer);
		game.setGuessedWord("g*essword");
		game.setGuessesLeft(9);
		game.setGuesses(0);
		game.setGameStatus(GameStatus.ONGOING);
		game.setIncorrectLetters(EMPTY_STRING);

		Guess guess1 = new Guess();
		guess1.setLetter("u");

		when(gameRepository.findOne(game.getId())).thenReturn(game);
		when(gameRepository.save(game)).thenReturn(game);

		Game returnedGame1 = gameService.guess(game.getId(), guess1);
		assertEquals(GameStatus.WON, returnedGame1.getGameStatus());
	}

	@Test
	public void testPlayerLoose() throws Exception {
		Player player = new Player();

		Answer answer = new Answer();
		answer.setId(1L);
		answer.setValue("guessword");

		Game game = new Game();
		game.setId(1L);
		game.setPlayer(player);
		game.setAnswer(answer);
		game.setGuessedWord("g*essword");
		game.setGuessesLeft(1);
		game.setGuesses(0);
		game.setGameStatus(GameStatus.ONGOING);
		game.setIncorrectLetters(EMPTY_STRING);

		Guess guess1 = new Guess();
		guess1.setLetter("x");

		when(gameRepository.findOne(game.getId())).thenReturn(game);
		when(gameRepository.save(game)).thenReturn(game);

		Game returnedGame1 = gameService.guess(game.getId(), guess1);
		assertEquals(GameStatus.LOST, returnedGame1.getGameStatus());
	}

	@Test
	public void testGiveUp() throws Exception {
		Player player = new Player();

		Answer answer = new Answer();
		answer.setId(1L);
		answer.setValue("guessword");

		Game game = new Game();
		game.setId(1L);
		game.setPlayer(player);
		game.setAnswer(answer);
		game.setGuessedWord(EMPTY_STRING);
		game.setGuessesLeft(answer.getValue().length());
		game.setGuesses(0);
		game.setGameStatus(GameStatus.ONGOING);
		game.setIncorrectLetters(EMPTY_STRING);

		when(gameRepository.findOne(game.getId())).thenReturn(game);
		when(gameRepository.save(game)).thenReturn(game);

		Game returnedGame = gameService.giveUp(game.getId());
		
		assertNotNull(returnedGame.getGuessedWord());
		assertEquals(answer.getValue(), returnedGame.getGuessedWord());
		assertEquals(GameStatus.LOST, returnedGame.getGameStatus());
	}
}
