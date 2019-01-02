package ee.kala.hangman;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ee.kala.hangman.converter.PlayerDtoConverter;
import ee.kala.hangman.dto.PlayerDto;
import ee.kala.hangman.entity.Player;
import ee.kala.hangman.repository.PlayerRepository;
import ee.kala.hangman.service.PlayerService;

public class PlayerServiceTest {
	
	@Mock
	private PlayerRepository playerRepository;

	@Mock
	private PlayerDtoConverter playerDtoConverter;

	@InjectMocks
	private PlayerService playerService;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testPlayerPost() throws Exception {
		Player player = new Player();
		player.setId(1L);
		player.setAge(10);
		player.setName("User");

		PlayerDto playerDto = new PlayerDto();
		playerDto.setAge(10);
		playerDto.setName("User");

		when(playerDtoConverter.toEntity(playerDto)).thenReturn(player);
		when(playerRepository.findByName(playerDto.getName())).thenReturn(null);
		when(playerRepository.save(player)).thenReturn(player);

		Player returnedPlayer = playerService.playerPost(playerDto);
		
		assertNotNull(returnedPlayer.getName());
		assertNotNull(returnedPlayer.getAge());

		assertEquals(playerDto.getName(), returnedPlayer.getName());
		assertEquals(playerDto.getAge(), returnedPlayer.getAge());
	}
}
