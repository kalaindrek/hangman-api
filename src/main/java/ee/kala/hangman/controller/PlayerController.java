package ee.kala.hangman.controller;

import java.util.List;

import javax.annotation.Resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ee.kala.hangman.entity.Player;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.rest.error.ExceptionWithErrorResponse;
import ee.kala.hangman.service.PlayerService;

@RestController
@CrossOrigin
@RequestMapping(path = "/hangman/v1", produces = APPLICATION_JSON_VALUE)
public class PlayerController {
    
    @Resource
	private PlayerService playerService;

	@GetMapping("/player")
	public ResponseEntity<List<Player>> playerGet() {
		List<Player> players = playerService.playerGet();
		return new ResponseEntity<>(players, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping("/player")
	public ResponseEntity<Player> playerPost(@RequestBody PlayerDto playerDto) throws ExceptionWithErrorResponse {
		Player playerPost = playerService.playerPost(playerDto);
		return new ResponseEntity<>(playerPost, new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping("/player/{playerId}")
	public ResponseEntity<Player> fetchPlayerInfo(@PathVariable("playerId") Long playerId) throws ExceptionWithErrorResponse {
		Player playerPost = playerService.fetchPlayerInfo(playerId);
		return new ResponseEntity<>(playerPost, new HttpHeaders(), HttpStatus.OK);
	}
}
