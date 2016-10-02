package readit;

import tts.TextToSpeech;
import tts.OnlineTTS_VoiceRSS;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ReadIt {

    static MediaPlayer mediaPlayer;

    public static void main(String[] args) throws Exception {

        // release all modifier keys (alt, ctrl, shift)
        releaseAllModifierKeys();

        // Copy selection to clipboard
        copySelection();

        // Get the string from clipboard
        String text = getStringFromClipboard();

        // Sent the string to tts engine to read it out
        // new Thread(() -> speakIt(text)).start();
        // display the user inteface
        new UserInterface().display(args, text);
    }

    private static void releaseAllModifierKeys() throws AWTException {
        Robot robot = new Robot();
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

        TextToSpeech tts = new OnlineTTS_VoiceRSS();

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
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }
}
