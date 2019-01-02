package ee.kala.hangman.controller;

import java.util.List;

import javax.annotation.Resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ee.kala.hangman.entity.Game;
import ee.kala.hangman.model.Guess;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.rest.error.ExceptionWithErrorResponse;
import ee.kala.hangman.service.GameService;

@RestController
@CrossOrigin
@RequestMapping(path = "/hangman/v1", produces = APPLICATION_JSON_VALUE)
public class GameController {

	@Resource
	private GameService gameService;
	
	@GetMapping("/game")
	public ResponseEntity<List<Game>> listGames() {
		List<Game> listGames = gameService.listGames();
		return new ResponseEntity<>(listGames, new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping("/game")
	public ResponseEntity<Game> startGame(@RequestBody PlayerDto playerDto) throws ExceptionWithErrorResponse {
		Game game = gameService.startGame(playerDto);
		return new ResponseEntity<>(game, new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping("/game/{gameId}")
	public ResponseEntity<Game> fetchGameInfo(@PathVariable("gameId") Long gameId) throws ExceptionWithErrorResponse {
		Game game = gameService.fetchGameInfo(gameId);
		return new ResponseEntity<>(game, new HttpHeaders(), HttpStatus.OK);
	}

	@PutMapping("/game/{gameId}")
	public ResponseEntity<Game> guess(@PathVariable("gameId") Long gameId, @RequestBody Guess guess) throws ExceptionWithErrorResponse {
		Game game = gameService.guess(gameId, guess);
		return new ResponseEntity<>(game, new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping("/game/{gameId}")
	public ResponseEntity<Game> giveUp(@PathVariable("gameId") Long gameId) throws ExceptionWithErrorResponse {
		Game game = gameService.giveUp(gameId);
		return new ResponseEntity<>(game, new HttpHeaders(), HttpStatus.OK);
	}
}
