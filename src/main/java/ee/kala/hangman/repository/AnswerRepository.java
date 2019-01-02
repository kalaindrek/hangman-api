package ee.kala.hangman.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.kala.hangman.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
