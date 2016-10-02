package tts;

import http.HTTPRequest;
import http.HTTPResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.prefs.Preferences;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.Media;

public class OnlineTTS_VoiceRSS implements TextToSpeech {

    String errorDetails = "No Error Occured";

    @Override
    public String getEngineName() {
        return "Online TTS: Voice RSS";
    }

    @Override
    public Optional<Media> getMedia(String text) {
        try {
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            String keyKey = "key";
            String keyValue = prefs.get(keyKey, "not set");
            if (keyValue.equals("not set")) {
                TextInputDialog getKeyDialog = new TextInputDialog();
                getKeyDialog.setContentText("Enter key");
                Optional<String> result = getKeyDialog.showAndWait();
                if (result.isPresent()) {
                    keyValue = result.get();
                    prefs.put(keyKey, result.get());
                } else {
                    throw new IllegalArgumentException(
                            "The key is required parameter for this TTS. "
                            + "You have to enter it only once.");
                }
            }
            //prefs.remove(keyKey);
            text = URLEncoder.encode(text.replace("\n", ". "), "UTF-8");
            Map<String, String> params = new HashMap<>();
            params.put("key", keyValue);
            params.put("src", text);
            params.put("hl", "en-gb");
            params.put("r", "0");
            params.put("c", "WAV");
            params.put("f", "32khz_16bit_mono");

            HTTPRequest request = new HTTPRequest("http://api.voicerss.org/", params);
            HTTPResponse response = request.sendGetRequest();

            File file;
            if (response.getContentType().startsWith("audio")) {
                file = File.createTempFile("speech", ".wav");
                Files.copy(response.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else if (response.getContentType().startsWith("text")) {
                String errorMessage = new Scanner(response.getInputStream()).nextLine();
                throw new IOException(errorMessage);
            } else {
                throw new IOException("The online tts engine did not return audio file.");
            }

            Media media = new Media(file.toURI().toString());
            return Optional.of(media);
        } catch (IOException | IllegalArgumentException ex) {
            errorDetails = "Error: " + ex.toString();
            return Optional.empty();
        }
    }

    @Override
    public String getErrorDetails() {
        return errorDetails;
    }
}
