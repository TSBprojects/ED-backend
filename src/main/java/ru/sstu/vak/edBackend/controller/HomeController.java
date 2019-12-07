package ru.sstu.vak.edBackend.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.sstu.vak.emotionRecognition.identifyEmotion.dataInfo.impl.FrameInfo;
import ru.sstu.vak.emotionRecognition.identifyEmotion.emotionRecognizer.EmotionRecognizer;
import ru.sstu.vak.emotionRecognition.identifyEmotion.emotionRecognizer.impl.EmotionRecognizerBase;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @Value("${ed-app.emotion-recognition.video-path}")
    private String videoPath;

    private FrameInfo currentFrameInfo;

    private Path userImagesFolder;
    private EmotionRecognizer emotionRecognizer;

    public HomeController() {
        File userImages = new File("userImages");
        userImages.mkdir();
        userImagesFolder = userImages.toPath();
    }

    @PostConstruct
    public void postConstructor() throws IOException {
        emotionRecognizer = new EmotionRecognizerBase("cnnModel.bin");
        if (!videoPath.equals("-1")) {
            emotionRecognizer.processVideo(videoPath, frameInfo -> {
                this.currentFrameInfo = frameInfo;
                if (!frameInfo.getVideoFaces().isEmpty()) {
                    System.out.println(frameInfo.getVideoFaces().get(0).getEmotion());
                }
                if (frameInfo.getFrameIndex() % 100 == 0) {
                    System.gc();
                }
            });
        }
    }

    @GetMapping("/api/currentEmotion")
    public FrameInfo test() {
        return currentFrameInfo;
    }

    @PostMapping("/api/recognize")
    public Map<String, String> recognize(@RequestPart(name = "img") MultipartFile image) throws Exception {

        InputStream in = new ByteArrayInputStream(image.getBytes());
        BufferedImage processedImage = emotionRecognizer.processImage(ImageIO.read(in)).getProcessedImage();

        if (processedImage.getHeight() > 700) {
            throw new Exception("Provided image is too large!");
        }

        if (FileUtils.sizeOfDirectory(userImagesFolder.toFile()) < 1000000000L) {
            ImageIO.write(
                    processedImage,
                    "jpg", new File(
                            userImagesFolder + "/" + FilenameUtils.removeExtension(image.getOriginalFilename()) + ".jpg"
                    )
            );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, "jpg", baos);
        byte[] bytes = baos.toByteArray();

        String encodeImage = Base64.getEncoder().withoutPadding().encodeToString(bytes);

        Map<String, String> jsonMap = new HashMap<>();

        jsonMap.put("content", encodeImage);

        return jsonMap;
    }

}
