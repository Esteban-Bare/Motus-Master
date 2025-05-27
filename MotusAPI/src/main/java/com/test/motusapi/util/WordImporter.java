package com.test.motusapi.util;

import com.test.motusapi.model.Word;
import com.test.motusapi.repository.WordRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WordImporter implements CommandLineRunner {

    private final WordRepository wordRepository;
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://trouve-mot.fr/api/random/100";

    @Autowired
    public WordImporter(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run(String... args) {
        importWords();
    }

    public void importWords() {
        try {
            ApiWord[] apiWords = restTemplate.getForObject(API_URL, ApiWord[].class);

            if (apiWords == null) {
                System.out.println("Failed to fetch words from API");
                return;
            }

            List<Word> words = Arrays.stream(apiWords)
                    .map(this::convertToWord)
                    .collect(Collectors.toList());

            wordRepository.saveAll(words);
            System.out.println("Successfully imported " + words.size() + " words");
        } catch (Exception e) {
            System.err.println("Error importing words: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Word convertToWord(ApiWord apiWord) {
        Word word = new Word();
        word.setWord(apiWord.getName().toUpperCase());
        word.setLength(apiWord.getName().length());
        word.setDifficulty(calculateDifficulty(apiWord.getName().length()));
        return word;
    }

    private int calculateDifficulty(int length) {
        if (length <= 5) {
            return 1;
        } else if (length <= 7) {
            return 2;
        } else {
            return 3;
        }
    }

    @Setter
    @Getter
    private static class ApiWord {
        private String name;
        private String categorie;

    }
}