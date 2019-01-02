package ee.kala.hangman.rest.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ee.kala.hangman.model.ErrorResponse;

@ControllerAdvice
public class ErrorHandler {

	@ExceptionHandler
	public ResponseEntity<Object> handleException(Throwable ex) {
		return handleException(ErrorResponse.UNEXPECTED_ERROR, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ExceptionWithErrorResponse.class)
	public ResponseEntity<Object> handleExceptionWithErrorResponse(ExceptionWithErrorResponse ex) {
		return handleException(ex.getErrorResponse(), new HttpHeaders(), ex.getStatus());
	}
	
	private <T> ResponseEntity<Object> handleException(T body, HttpHeaders headers, HttpStatus status) {
		return new ResponseEntity<>(body, headers, status);
	}
}
