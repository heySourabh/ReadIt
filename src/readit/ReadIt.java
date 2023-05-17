package readit;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import tts.LocalTTS_Pico2Wave;
import tts.OnlineTTS_FromTextToSpeech;
import tts.OnlineTTS_VoiceRSS;
import tts.TextToSpeech;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReadIt {

    static MediaPlayer mediaPlayer;

    public static void main(String[] args) throws Exception {

        System.out.println("Arguments: " + Arrays.toString(args));
        String text = args.length == 1 ? args[0] : getSelectedText();

        if (text.length() < 5) return;

        // Sent the string to tts engine to read it out
        // new Thread(() -> speakIt(text)).start();
        // display the user interface
        new UserInterface().display(args, text);
    }

    private static String getSelectedText() throws AWTException {
        // release all modifier keys (alt, ctrl, shift)
        releaseAllModifierKeys();

        // Copy selection to clipboard
        copySelection();

        // Get the string from clipboard
        return getStringFromClipboard();
    }

    private static void releaseAllModifierKeys() throws AWTException {
        Robot robot = new Robot();
        robot.delay(1000);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    private static void copySelection() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    private static String getStringFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (HeadlessException | UnsupportedFlavorException | IOException ex) {
            return "Sorry! I was unable to read from the clipboard.";
        }
    }

    public static void speakIt(String text) {

        List<TextToSpeech> ttsList = Arrays.asList(
                new OnlineTTS_VoiceRSS(),
                new OnlineTTS_FromTextToSpeech(),
                new LocalTTS_Pico2Wave()
        );

        TextToSpeech tts = ttsList.get(0);

        Media media = tts.getMedia(text).orElse(null);
        if (media == null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                String errorMessage = "TTS Engine Name: " + tts.getEngineName() + "\n"
                        + "Unable to create speech from text!\n"
                        + tts.getErrorDetails();
                System.out.println(errorMessage);
                alert.setContentText(errorMessage);
                alert.setTitle("TTS error");
                alert.showAndWait();
                System.exit(1);
            });

        } else {
            System.out.println("Received audio file from TTS.");
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }
}
