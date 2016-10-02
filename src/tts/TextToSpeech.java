package tts;

import java.util.Optional;
import javafx.scene.media.Media;

public interface TextToSpeech {

    public String getEngineName();
    public Optional<Media> getMedia(String text);
    public String getErrorDetails();
}
