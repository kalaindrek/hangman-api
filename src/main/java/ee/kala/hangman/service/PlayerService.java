package ee.kala.hangman.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ee.kala.hangman.converter.PlayerDtoConverter;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.entity.Player;
import ee.kala.hangman.model.ErrorResponse;
import ee.kala.hangman.repository.PlayerRepository;
import ee.kala.hangman.rest.error.ExceptionWithErrorResponse;

@Service
public class PlayerService {

	@Resource
	private PlayerRepository playerRepository;

	@Resource
	private PlayerDtoConverter playerDtoConverter;

	@Transactional(readOnly = true)
	public List<Player> playerGet() {
		return playerRepository.findAll();
	}

	@Transactional
	public Player playerPost(PlayerDto playerDto) throws ExceptionWithErrorResponse {
		validate(playerDto);

		Player player = playerDtoConverter.toEntity(playerDto);
		
		playerRepository.save(player);
		
		return player;
	}

	private void validate(PlayerDto playerDto) throws ExceptionWithErrorResponse {
		Player player = playerRepository.findByName(playerDto.getName());
		if (player != null) {
			throw new ExceptionWithErrorResponse(ErrorResponse.PLAYER_ALREADY_EXISTS, HttpStatus.NOT_FOUND);
		}
	}

	@Transactional
	public Player fetchPlayerInfo(Long playerId) throws ExceptionWithErrorResponse {
		Player player = playerRepository.findOne(playerId);
		if (player == null) {
			throw new ExceptionWithErrorResponse(ErrorResponse.PLAYER_WAS_NOT_FOUND, HttpStatus.NOT_FOUND);
		}
		return player;
	}
}
