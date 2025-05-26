package dev.esteban.motuswebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuessDto {
    private Map<String,Object> gameState;
    private String message;
}
