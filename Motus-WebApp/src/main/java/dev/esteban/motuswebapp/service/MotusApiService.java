package dev.esteban.motuswebapp.service;

import dev.esteban.motuswebapp.dto.GuessDto;
import dev.esteban.motuswebapp.dto.LoginResponseDto;
import dev.esteban.motuswebapp.dto.NewGameDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST,proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MotusApiService {
    private final RestTemplate restTemplate;
    private final String apiUrl = "http://localhost:8077/api/v1/";
    private final HttpSession session;

    public MotusApiService(RestTemplate restTemplate, HttpServletRequest request) {
        this.session = request.getSession();
        this.restTemplate = restTemplate;
    }

    public boolean login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        HttpEntity<Map<String,String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(apiUrl + "/auth/login", request, LoginResponseDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                LoginResponseDto responseBody = response.getBody();
                if (responseBody != null) {
                    session.setAttribute("userToken", responseBody.getToken());
                    session.setAttribute("authenticated", true);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        }

    }

    public boolean isAuthenticated() {
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        String token = (String) session.getAttribute("userToken");
        if (authenticated != null && authenticated && token != null) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                ResponseEntity<Boolean> response = restTemplate.postForEntity(apiUrl + "/auth/verify", request, Boolean.class);

                return response.getBody() != null && response.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public String getUserToken() {
        return (String) session.getAttribute("userToken");
    }

    public void logout() {
        session.removeAttribute("userToken");
        session.removeAttribute("authenticated");
    }

    public ResponseEntity<NewGameDto> startNewGame() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getUserToken());
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(apiUrl + "/game/new", HttpMethod.GET, request, NewGameDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new NewGameDto(null,null,"Error"));
        }
    }


    public ResponseEntity<GuessDto> makeGuess(String word) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getUserToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("guess", word);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(apiUrl + "/game/guess", HttpMethod.POST, request, GuessDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GuessDto(null,"error"));
        }
    }

    public boolean register(String username, String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl + "/auth/register", request, String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResponseEntity<List<Map<String, Object>>> getTopScores(int limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getUserToken());
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(
                    apiUrl + "/game/top-scores?limit=" + limit,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
