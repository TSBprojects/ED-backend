package ru.sstu.vak.edBackend.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.sstu.vak.emotionRecognition.identifyEmotion.emotionRecognizer.EmotionRecognizer;
import ru.sstu.vak.emotionRecognition.identifyEmotion.emotionRecognizer.impl.EmotionRecognizerBase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    private Path userImagesFolder;
    private EmotionRecognizer emotionRecognizer;

    public HomeController() throws IOException {
        File userImages = new File("userImages");
        userImages.mkdir();
        userImagesFolder = userImages.toPath();
        emotionRecognizer = new EmotionRecognizerBase("cnnModel.bin");
    }


    @PostMapping("/recognize")
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
