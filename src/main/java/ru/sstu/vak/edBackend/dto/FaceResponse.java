package ru.sstu.vak.edBackend.dto;

import lombok.Data;
import ru.sstu.vak.emotionRecognition.common.Emotion;
import ru.sstu.vak.emotionRecognition.identifyEmotion.dataFace.impl.VideoFace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
public class FaceResponse {

    private List<EmotionResponse> emotions = new ArrayList<>();

    public FaceResponse(VideoFace videoFace) {
        Emotion emotion = videoFace.getEmotion();
        emotions.add(new EmotionResponse(emotion.getEmotionId(), emotion.getValue(), emotion.getProbability() - getMockProb()));
        emotions.addAll(Arrays.stream(Emotion.values())
                .filter(e -> e.getEmotionId() != emotion.getEmotionId())
                .map(e -> new EmotionResponse(e.getEmotionId(), e.getValue(), getMockProb()))
                .collect(Collectors.toList()));
        emotions.sort(Comparator.comparingInt(EmotionResponse::getEmotionId));
    }

    private double getMockProb() {
        return ThreadLocalRandom.current().nextDouble(0, 0.22);
    }

}
