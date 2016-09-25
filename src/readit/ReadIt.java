package readit;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.LockSupport;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ReadIt {

    static Media media;
    static MediaPlayer mediaPlayer;

    public static void main(String[] args) throws Exception {
        // release all modifier keys (alt, ctrl, shift)
        releaseAllModifierKeys();

        // Copy selection to clipboard
        copySelection();

        // Get the string from clipboard
        String text = getStringFromClipboard();

        // Sent the string to tts engine to read it out
        new Thread(() -> speakIt(text)).start();

        // display the user inteface
        new UserInterface().display(args);
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

    private static void speakIt(String text) {
        try {
            File file = File.createTempFile("speech", ".wav");
            String fileName = file.getAbsolutePath();
            Process proc = Runtime.getRuntime().exec(new String[]{
                "pico2wave",
                "-w", fileName,
                text
            });
            proc.waitFor();

            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch (IOException | InterruptedException ex) {
            LockSupport.parkNanos(1_000_000_000);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Unable to create speech from text!");
                alert.setTitle("TTS error");
                alert.showAndWait();
                System.exit(1);
            });
        }
    }
}
