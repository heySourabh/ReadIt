package tts;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.media.Media;

public class LocalTTS_Pico2Wave implements TextToSpeech {

    String errorDetails = "No Error Occured";

    @Override
    public String getEngineName() {
        return "Local TTS: pico2wave";
    }

    @Override
    public Optional<Media> getMedia(String text) {
        try {
            File file = File.createTempFile("speech", ".wav");
            String fileName = file.getAbsolutePath();

            Process proc = Runtime.getRuntime().exec(new String[]{
                "pico2wave",
                "-w", fileName,
                text
            });
            proc.waitFor();
            Media media = new Media(file.toURI().toString());
            return Optional.of(media);
        } catch (IOException | InterruptedException ex) {
            errorDetails = "Error: " + ex.getMessage();
            return Optional.empty();
        }
    }

    @Override
    public String getErrorDetails() {
        return errorDetails;
    }
}
