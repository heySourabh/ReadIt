package readit;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class ReadIt {
    public static void main(String[] args) throws Exception {
        // Copy selection to clipboard
        copySelection();
        
        // Get the string from clipboard
        String text = getStringFromClipboard();
        
        // Sent the string to tts engine to read it out
        speakIt(text);
    }
    
    private static void copySelection() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
    
    private static String getStringFromClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String)clipboard.getData(DataFlavor.stringFlavor);
    }
    
    private static void speakIt(String text) throws IOException, InterruptedException {
        String fileName = new File("./tmp.wav").getAbsolutePath();
        Process proc = Runtime.getRuntime().exec(new String[]{
            "pico2wave",
            "-w", fileName,
            text
        });
        proc.waitFor();
        
        proc = Runtime.getRuntime().exec(new String[]{
            "aplay", 
            fileName
        });
        proc.waitFor();
    }
}
