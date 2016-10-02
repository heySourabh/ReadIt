package readit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static readit.ReadIt.mediaPlayer;

public class UserInterface extends Application {

    final static String PAUSE_IMG_PATH = "/images/pause.png";
    final static String PLAY_IMG_PATH = "/images/play.png";
    final static String ICON_IMG_PATH = "/images/icon_64x64.png";

    final static Image PAUSE_IMG;
    final static Image PLAY_IMG;
    final static Image ICON_IMG;

    boolean playing = true;
    double mouseLocX = 0.0;
    double mouseLocY = 0.0;

    ImageView control;
    boolean mouseDragged = false;

    static String textToSpeak;

    static {
        PAUSE_IMG = new Image(ReadIt.class
                .getResourceAsStream(PAUSE_IMG_PATH));
        PLAY_IMG = new Image(ReadIt.class
                .getResourceAsStream(PLAY_IMG_PATH));
        ICON_IMG = new Image(ReadIt.class
                .getResourceAsStream(ICON_IMG_PATH));
    }

    public void display(String[] args, String textToSpeak) {
        UserInterface.textToSpeak = textToSpeak;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Thread(() -> ReadIt.speakIt(textToSpeak)).start();
        control = new ImageView(PAUSE_IMG);
        control.setOnMouseEntered(e -> control.setEffect(new Glow(0.25)));
        control.setOnMouseExited(e -> control.setEffect(new Glow(0.0)));
        control.setOnMousePressed(e -> mousePressed(e, primaryStage));
        control.setOnMouseReleased(e -> mouseReleased(e));
        control.setOnMouseDragged(e -> mouseDragged(e, primaryStage));
        control.setOnScroll(e -> mouseScrolled(e));

        Group container = new Group(control);
        Scene scene = new Scene(container, Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setMinWidth(PAUSE_IMG.getWidth());
        primaryStage.setMaxWidth(PAUSE_IMG.getWidth());
        primaryStage.setMinHeight(PAUSE_IMG.getHeight());
        primaryStage.setMaxHeight(PAUSE_IMG.getHeight());
        primaryStage.getIcons().add(ICON_IMG);
        primaryStage.setTitle("Read It");
        primaryStage.show();
    }

    private void play() {
        if (mediaPlayer == null) {
            return;
        }
        control.setImage(PAUSE_IMG);
        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(1.0)));
        mediaPlayer.play();
        playing = true;
    }

    private void pause() {
        if (mediaPlayer == null) {
            return;
        }
        control.setImage(PLAY_IMG);
        mediaPlayer.pause();
        playing = false;
    }

    private void mousePressed(MouseEvent e, Stage stage) {
        getContextMenu().hide();
        if (e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        mouseDragged = false;
        control.setEffect(new Glow(0.5));
        mouseLocX = stage.getX() - e.getScreenX();
        mouseLocY = stage.getY() - e.getScreenY();
    }

    private void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            getContextMenu().show(control, e.getScreenX(), e.getScreenY());
        }
        if (e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        control.setEffect(new Glow(0.25));
        if (mouseDragged) {
            return;
        }
        if (playing) {
            pause();
        } else {
            play();
        }
    }

    private void mouseDragged(MouseEvent e, Stage stage) {
        if (e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        mouseDragged = true;
        double curLocX = stage.getX() - e.getScreenX();
        double curLocY = stage.getY() - e.getScreenY();

        stage.setX(stage.getX() + mouseLocX - curLocX);
        stage.setY(stage.getY() + mouseLocY - curLocY);
    }

    private void mouseScrolled(ScrollEvent e) {
        double direction = Math.signum(e.getDeltaY());
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(direction * 0.5)));
    }

    ContextMenu menu;

    private ContextMenu getContextMenu() {
        if (menu == null) {
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.setOnAction(e -> System.exit(0));

            MenuItem aboutItem = new MenuItem("About...");
            aboutItem.setOnAction(e -> showAboutDialog());
            menu = new ContextMenu(aboutItem, exitItem);
        }
        return menu;
    }

    private void showAboutDialog() {
        new AboutDialog(ICON_IMG).showAndWait();
    }
}
