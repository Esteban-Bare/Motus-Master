package com.test.motusapi.service;

import com.test.motusapi.dto.GuessDto;
import com.test.motusapi.dto.NewGameDto;
import com.test.motusapi.model.Score;
import com.test.motusapi.model.User;
import com.test.motusapi.model.Word;
import com.test.motusapi.repository.ScoreRepository;
import com.test.motusapi.repository.UserRepository;
import com.test.motusapi.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    // Store active games - userId -> gameState
    private final Map<String, Map<String, Object>> activeGames = new ConcurrentHashMap<>();

    public NewGameDto startNewGame(String username) {
        // Select random word from repository
        List<Word> allWords = wordRepository.findAll();
        Word selectedWord = allWords.get(new Random().nextInt(allWords.size()));

        // Initialize game state
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("word", selectedWord.getWord());
        gameState.put("attemptsLeft", 8);
        gameState.put("maxAttempts", 8);
        gameState.put("attempts", new ArrayList<>());
        gameState.put("currentScore", 1000); // Initial score, will decrease with attempts
        gameState.put("gameOver", false);
        gameState.put("gameWon", false);
        gameState.put("difficulty", selectedWord.getDifficulty());


        // Store game state for this user
        activeGames.put(username, gameState);

        // Create response
//        Map<String, Object> response = new HashMap<>();
//        response.put("word", Map.of(
//                "length", selectedWord.getWord().length(),
//                "difficulty", selectedWord.getDifficulty()
//        ));
//        response.put("gameState", gameState);
//        response.put("message", "New game started. Good luck!");

        NewGameDto response = new NewGameDto();
        response.setGameState(gameState);
        response.setWord(Map.of(
                "length", selectedWord.getWord().length(),
                "difficulty", selectedWord.getDifficulty()
        ));
        response.setMessage("New game started, Good luck!");

        return response;
    }

    public GuessDto makeGuess(String username, String guess) {
        Map<String, Object> gameState = activeGames.get(username);

        // Handle case where no active game exists
        if (gameState == null) {
//            return Map.of(
//                    "gameState", Map.of("gameOver", true),
//                    "message", "No active game. Please start a new game."
//            );
            return new GuessDto(Map.of("gameOver", true), "No active game. Please start a new game.");
        }

        String targetWord = (String) gameState.get("word");
        boolean gameOver = (boolean) gameState.get("gameOver");

        // Don't allow guesses if game is over
        if (gameOver) {
//            return Map.of(
//                    "gameState", gameState,
//                    "message", "Game is already over. Please start a new game."
//            );
            return new GuessDto(gameState, "Game is already over. Please start a new game.");
        }

        // Validate guess length
        if (guess.length() != targetWord.length()) {
//            return Map.of(
//                    "gameState", gameState,
//                    "message", "Your guess must be " + targetWord.length() + " letters long."
//            );
            return new GuessDto(gameState, "Your guess must be " + targetWord.length() + " letters long.");
        }

        // Process the guess
        List<Map<String, Object>> letterResults = evaluateGuess(guess, targetWord);

        // Update game state
        List<List<Map<String, Object>>> attempts = (List<List<Map<String, Object>>>) gameState.get("attempts");
        attempts.add(letterResults);

        int attemptsLeft = (int) gameState.get("attemptsLeft") - 1;
        gameState.put("attemptsLeft", attemptsLeft);

        // Calculate score - decrease score with each attempt
        int currentScore = (int) gameState.get("currentScore");
        int difficulty = (int) gameState.get("difficulty");
        currentScore -= (100 - (difficulty * 20));
        if (currentScore < 0) currentScore = 0;
        gameState.put("currentScore", currentScore);

        // Check for win
        boolean isCorrect = guess.equalsIgnoreCase(targetWord);
        String message;

        if (isCorrect) {
            gameState.put("gameOver", true);
            gameState.put("gameWon", true);
            message = "Congratulations! You guessed the word correctly!";
            addScore(username,gameState);
        } else if (attemptsLeft <= 0) {
            gameState.put("gameOver", true);
            message = "Game over! The word was: " + targetWord;
        } else {
            message = "Keep trying! " + attemptsLeft + " attempts left.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("gameState", gameState);
        response.put("message", message);


        return new GuessDto(gameState, message);
    }

    private void addScore(String username, Map<String, Object> gameState) {
        Integer score = (Integer) gameState.get("currentScore");
        User user = userRepository.findByUsername(username).get();
        Word word = wordRepository.getWordByWord((String) gameState.get("word"));
        Date dateTime = new Date();
        Score newScore = new Score();
        newScore.setScore(score);
        newScore.setUser(user);
        newScore.setWord(word);
        newScore.setDateTime(dateTime);
        scoreRepository.save(newScore);
    }

    private List<Map<String, Object>> evaluateGuess(String guess, String targetWord) {
        List<Map<String, Object>> result = new ArrayList<>();
        guess = guess.toUpperCase();
        targetWord = targetWord.toUpperCase();

        boolean isWinningGuess = guess.equals(targetWord);

        Map<Character, Integer> charFrequency = new HashMap<>();
        for (char c : targetWord.toCharArray()) {
            charFrequency.put(c, charFrequency.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            Map<String, Object> letterInfo = new HashMap<>();
            letterInfo.put("value", String.valueOf(c));

            if (i < targetWord.length() && c == targetWord.charAt(i)) {
                if (isWinningGuess) {
                    letterInfo.put("status", "first");
                } else {
                    letterInfo.put("status", "correct");
                }

                charFrequency.put(c, charFrequency.get(c) - 1);
            } else {
                letterInfo.put("status", "wrong");
            }

            result.add(letterInfo);
        }

        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            Map<String, Object> letterInfo = result.get(i);

            if (letterInfo.get("status").equals("correct") ||
                    letterInfo.get("status").equals("first")) {
                continue;
            }
            if (charFrequency.containsKey(c) && charFrequency.get(c) > 0) {
                letterInfo.put("status", "misplaced");
                charFrequency.put(c, charFrequency.get(c) - 1);
            }
        }

        return result;
    }

    public List<Map<String, Object>> getTopScores(int limit) {
        List<Score> scores = scoreRepository.findTopByOrderByScoreDesc(limit);

        List<Map<String, Object>> topScores = new ArrayList<>();
        for (Score score : scores) {
            Map<String, Object> scoreInfo = new HashMap<>();
            scoreInfo.put("score", score.getScore());
            scoreInfo.put("username", score.getUser().getUsername());
            scoreInfo.put("date", score.getDateTime());
            topScores.add(scoreInfo);
        }
        return topScores;
    }


}
