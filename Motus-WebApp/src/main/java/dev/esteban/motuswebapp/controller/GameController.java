package dev.esteban.motuswebapp.controller;

import dev.esteban.motuswebapp.dto.GuessDto;
import dev.esteban.motuswebapp.dto.NewGameDto;
import dev.esteban.motuswebapp.service.MotusApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Controller
public class GameController {
    @Autowired
    private MotusApiService motusApiService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Motus");
        return "index";
    }

    @GetMapping("/game")
    public String game(Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("title", "Motus Game");
        return "game";
    }

    @GetMapping("/game/new")
    public ResponseEntity<NewGameDto> newGame() {
        return motusApiService.startNewGame();
    }

    @PostMapping("/game/guess")
    public ResponseEntity<GuessDto> guess(@RequestBody Map<String,String> guess) {
        String word = guess.get("guess");
        return motusApiService.makeGuess(word);
    }

    @GetMapping("/game/top-scores")
    public ResponseEntity<List<Map<String, Object>>> getTopScores() {
        return motusApiService.getTopScores(20); // Fetch top 20 scores
    }
}
