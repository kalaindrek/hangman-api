package ee.kala.hangman.rest.error;

import org.springframework.http.HttpStatus;

import ee.kala.hangman.model.ErrorResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExceptionWithErrorResponse extends Exception {
	
	private ErrorResponse errorResponse;
	private HttpStatus status;

	public ExceptionWithErrorResponse(ErrorResponse errorResponse, HttpStatus status) {
		this.errorResponse = errorResponse;
		this.status = status;
	}
}
