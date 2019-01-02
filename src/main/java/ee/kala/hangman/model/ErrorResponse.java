package ee.kala.hangman.model;

import lombok.Data;

@Data
public class ErrorResponse {
	
	public static final ErrorResponse GAME_DOES_NOT_EXIST = new ErrorResponse(404, "Game does not exist.");
	public static final ErrorResponse UNEXPECTED_ERROR = new ErrorResponse(500, "Unexpected error");
	public static final ErrorResponse GUESS_WAS_NOT_MADE = new ErrorResponse(400, "Guess was not made.");
	public static final ErrorResponse GAME_WAS_NOT_FOUND = new ErrorResponse(404, "Game was not found.");
	public static final ErrorResponse PLAYER_ALREADY_EXISTS = new ErrorResponse(400, "Player already exists");
	public static final ErrorResponse PLAYER_WAS_NOT_FOUND = new ErrorResponse(404, "Player was not found");
	
	private Integer code;
	private String message;

	private ErrorResponse(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
