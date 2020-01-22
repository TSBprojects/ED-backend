package ru.sstu.vak.edBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmotionResponse {

    private int emotionId;

    private String emotionName;

    private double probability;

}
