package com.test.motusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewGameDto {
    private Map<String,Object> word;
    private Map<String,Object> gameState;
    private String message;
}
