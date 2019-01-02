package ee.kala.hangman.converter;

import org.springframework.stereotype.Component;

import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.entity.Player;

@Component
public class PlayerDtoConverter {
	
	public Player toEntity(PlayerDto playerDto) {
		Player entity = new Player();
		entity.setAge(playerDto.getAge());
		entity.setName(playerDto.getName());
		return entity;
	}
	
	public Player copy(PlayerDto source, Player target) {
		target.setName(source.getName());
		target.setAge(source.getAge());
		return target;
	}
}
