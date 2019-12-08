package ru.sstu.vak.edBackend.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/api")
public class HomeController {

    @Value("${ed-app.emotion-recognition.video-path}")
    private String videoPath;

    @Value("${ed-app.emotion-recognition.model-path}")
    private String modelPath;

    @Value("${ed-app.emotion-recognition.log-emotion}")
    private boolean logEmotion;


    private Path userImagesFolder;
    private EmotionRecognizer emotionRecognizer;
    private FrameInfo currentFrameInfo;

    public HomeController() {
        File userImages = new File("userImages");
        userImages.mkdir();
        userImagesFolder = userImages.toPath();
        initExit();
    }

    @PostConstruct
    public void postConstructor() throws IOException {
        emotionRecognizer = new EmotionRecognizerBase(modelPath);
        if (!videoPath.equals("-1")) {
            emotionRecognizer.processVideo(videoPath, frameInfo -> {
                this.currentFrameInfo = frameInfo;
                if (!frameInfo.getVideoFaces().isEmpty() && logEmotion) {
                    System.out.println(frameInfo.getVideoFaces().toString());
                }
                if (frameInfo.getFrameIndex() % 50 == 0) {
                    System.gc();
                }
            });
        }
    }

    @GetMapping("/currentEmotion")
    public Object test() {
        return currentFrameInfo != null ? currentFrameInfo : "Video not recording.";
    }

    @PostMapping("/recognizeImage")
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


    private void initExit() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (emotionRecognizer != null && emotionRecognizer.isRun()) {
                emotionRecognizer.stop();
            }
        }));
    }
}
