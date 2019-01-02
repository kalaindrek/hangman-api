package ee.kala.hangman.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Guess {
	private String letter;

	public Guess(String letter) {
		this.letter = letter;
	}
}
