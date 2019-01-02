package ee.kala.hangman.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import ee.kala.hangman.entity.Answer;
import ee.kala.hangman.repository.AnswerRepository;

import static java.util.stream.Collectors.toList;

@Configuration
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartup.class);
	private static final String FILE_NAME = "words_alpha.txt";

	@Resource
	private AnswerRepository answerRepository;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		LOG.warn("Starting hangman dictionary import");
		
		List<String> words = readDictionaryFromFile();
		
		List<Answer> answers = words.stream().map(Answer::new).collect(Collectors.toList());
		
		answerRepository.save(answers);
	}

	private List<String> readDictionaryFromFile() {
		try {
			InputStream is = new ClassPathResource(FILE_NAME).getInputStream();
			Stream<String> stream = new BufferedReader(new InputStreamReader(is)).lines();
			List<String> result = stream.map(String::trim).filter(s -> !s.isEmpty()).collect(toList());
			LOG.warn("Hangman dictionary import successful");
			return result;
		} catch (IOException e) {
			LOG.error("Hangman dictionary import failed. Error: " + e.getMessage(), e);
			return Collections.emptyList();
		}
	}
}
