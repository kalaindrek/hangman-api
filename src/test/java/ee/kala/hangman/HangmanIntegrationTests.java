package ee.kala.hangman;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import ee.kala.hangman.configuration.ApplicationStartup;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.entity.Answer;
import ee.kala.hangman.entity.Game;
import ee.kala.hangman.entity.Player;
import ee.kala.hangman.model.GameStatus;
import ee.kala.hangman.model.Guess;
import ee.kala.hangman.repository.AnswerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode= ClassMode.AFTER_EACH_TEST_METHOD)
public class HangmanIntegrationTests {

	private static final String LIST_GAMES = "/hangman/v1/game";
	private static final String START_GAME = "/hangman/v1/game";
	private static final String FETCH_GAME_INFO = "/hangman/v1/game/{gameId}";
	private static final String GUESS = "/hangman/v1/game/{gameId}";
	private static final String GIVE_UP = "/hangman/v1/game/{gameId}";

	private static final String PLAYER_GET = "/hangman/v1/player";
	private static final String PLAYER_POST = "/hangman/v1/player";
	private static final String FETCH_PLAYER_INFO = "/hangman/v1/player/{playerId}";
	
	private static final String ANSWER = "ssa";

	@MockBean
	private ApplicationStartup applicationStartup;

	@Resource
	private AnswerRepository answerRepository;
	
	@Resource
	private TestRestTemplate restTemplate;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Transactional
	public void prepareAnswer() {
		Answer answer = new Answer(ANSWER);
		answerRepository.saveAndFlush(answer);
	}

	@Test
	public void testGameWin() {
		prepareAnswer();
		
		PlayerDto playerDto = new PlayerDto();
		playerDto.setName("Indrek");
		playerDto.setAge(30);
		
		Player player = createPlayer(playerDto);
		Game game = createGame(player);

		Game returnedGame1 = guess(game.getId(), new Guess("s"));
		assertTrue(returnedGame1.getGameStatus() == GameStatus.ONGOING);
		
		Game returnedGame2 = guess(game.getId(), new Guess("a"));
		assertTrue(returnedGame2.getGameStatus() == GameStatus.WON);
	}

	@Test
	public void testGameLoose() {
		prepareAnswer();

		PlayerDto playerDto = new PlayerDto();
		playerDto.setName("Indrek");
		playerDto.setAge(30);

		Player player = createPlayer(playerDto);
		Game game = createGame(player);
		
		Game returnedGame1 = guess(game.getId(), new Guess("n"));
		assertTrue(returnedGame1.getGameStatus() == GameStatus.ONGOING);
		
		Game returnedGame2 = guess(game.getId(), new Guess("m"));
		assertTrue(returnedGame2.getGameStatus() == GameStatus.ONGOING);
		
		Game returnedGame3 = guess(game.getId(), new Guess("o"));
		assertTrue(returnedGame3.getGameStatus() == GameStatus.LOST);
	}
	
	@Test
	public void testGiveUp() {
		prepareAnswer();

		PlayerDto playerDto = new PlayerDto();
		playerDto.setName("Indrek");
		playerDto.setAge(30);

		Player player = createPlayer(playerDto);
		Game game = createGame(player);

		Game returnedGame1 = giveUp(game.getId());
		assertTrue(returnedGame1.getGameStatus() == GameStatus.LOST);
	}

	private Player createPlayer(PlayerDto playerDto) {
		ResponseEntity<Player> responseEntity = restTemplate.postForEntity(PLAYER_POST, playerDto, Player.class);
		Player response = responseEntity.getBody();
		assertNotNull(response);
		assertTrue(response.getName().equalsIgnoreCase(playerDto.getName()));
		assertTrue(response.getAge().equals(playerDto.getAge()));
		return response;
	}

	private Game createGame(Player player) {
		ResponseEntity<Game> responseEntity = restTemplate.postForEntity(START_GAME, player, Game.class);
		Game response = responseEntity.getBody();
		assertNotNull(response);
		assertNotNull(response.getPlayer());
		assertTrue(response.getGameStatus() == GameStatus.ONGOING);
		assertTrue(response.getGuesses().equals(0));
		assertTrue(response.getGuessesLeft().equals(ANSWER.length()));
		return response;
	}
	
	private Game guess(Long gameId, Guess guess) {
		HttpEntity<Guess> entity = new HttpEntity<>(guess, new HttpHeaders());
		ResponseEntity<Game> responseEntity = restTemplate.exchange(GUESS, HttpMethod.PUT, entity, Game.class, gameId);
		Game response = responseEntity.getBody();
		assertNotNull(response);
		assertNotNull(response.getPlayer());
		return response;
	}

	private Game giveUp(Long gameId) {
		ResponseEntity<Game> responseEntity = restTemplate.exchange(GIVE_UP, HttpMethod.DELETE, null, Game.class, gameId);
		Game response = responseEntity.getBody();
		assertNotNull(response);
		assertNotNull(response.getPlayer());
		return response;
	}
}
