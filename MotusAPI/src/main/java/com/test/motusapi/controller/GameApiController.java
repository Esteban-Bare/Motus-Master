package com.test.motusapi.controller;

import com.test.motusapi.dto.GuessDto;
import com.test.motusapi.dto.NewGameDto;
import com.test.motusapi.service.GameService;
import com.test.motusapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/game")
public class GameApiController {
    @Autowired
    private GameService gameService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/new")
    public ResponseEntity<NewGameDto> newGame(@RequestHeader("Authorization") String token) {
        // Modify this to strip the "Bearer " prefix before extraction
        String bearerToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String username = jwtUtil.extractUsername(bearerToken);
        return ResponseEntity.ok(gameService.startNewGame(username));
    }

    @PostMapping("/guess")
    public ResponseEntity<GuessDto> guess(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> guess) {
        // Same fix here
        String bearerToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String username = jwtUtil.extractUsername(bearerToken);
        String word = guess.get("guess");
        return ResponseEntity.ok(gameService.makeGuess(username, word));
    }

    @GetMapping("/top-scores")
    public ResponseEntity<List<Map<String, Object>>> getTopScores(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(gameService.getTopScores(limit));
    }
}
