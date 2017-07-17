package tts;

import http.HTTPRequest;
import http.HTTPResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import javafx.scene.media.Media;

public class OnlineTTS_FromTextToSpeech implements TextToSpeech {

    String errorDetails = "No Error Occured";

    @Override
    public String getEngineName() {
        return "Online TTS: From Text To Speech";
    }

    @Override
    public Optional<Media> getMedia(String text) {
        try {
            text = URLEncoder.encode(text.replace("\n", " "), "UTF-8");
            Map<String, String> params = new HashMap<>();
            params.put("language", "British English");
            params.put("voice", "IVONA Amy22 (UK English)");
            params.put("input_text", text);
            params.put("speed", "0");
            params.put("action", "process_text");

            String baseURL = "http://fromtexttospeech.com";

            HTTPRequest request = new HTTPRequest(baseURL, params);
            HTTPResponse response = request.sendPostRequest();

            File mp3File;
            if (response.getContentType().startsWith("text")) {
                String fileURLString = null;
                // Search for mp3 file
                Scanner html = new Scanner(response.getInputStream());
                while (html.hasNextLine()) {
                    Scanner sc = new Scanner(html.nextLine());
                    fileURLString = sc.findInLine("file=[a-zA-Z0-9/]+.mp3");
                    if (fileURLString != null) {
                        fileURLString = fileURLString.substring(5);
                        break;
                    }
                }
                if (fileURLString == null) {
                    throw new IOException("Could not locate audio file in the webpage.");
                }
                fileURLString = baseURL + fileURLString;
                URL fileURL = new URL(fileURLString);
                mp3File = File.createTempFile("speech", ".mp3");
                try (InputStream in = fileURL.openStream()) {
                    Files.copy(in, mp3File.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                throw new IOException("The online tts engine did not return expected output.");
            }

            File wavFile = File.createTempFile("speech", ".wav");
            Process proc = Runtime.getRuntime().exec(
                    new String[]{
                        "ffmpeg", "-y", "-i",
                        mp3File.getAbsolutePath(), wavFile.getAbsolutePath()
                    });

            proc.waitFor();

            Media media = new Media(wavFile.toURI().toString());
            return Optional.of(media);
        } catch (IOException | InterruptedException ex) {
            errorDetails = "Error: " + ex.toString();
            return Optional.empty();
        }
    }

    @Override
    public String getErrorDetails() {
        return errorDetails;
    }
}
