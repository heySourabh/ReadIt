package readit;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.util.Objects;

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
        PAUSE_IMG = new Image(Objects.requireNonNull(ReadIt.class
                .getResourceAsStream(PAUSE_IMG_PATH)));
        PLAY_IMG = new Image(Objects.requireNonNull(ReadIt.class
                .getResourceAsStream(PLAY_IMG_PATH)));
        ICON_IMG = new Image(Objects.requireNonNull(ReadIt.class
                .getResourceAsStream(ICON_IMG_PATH)));
    }

    public void display(String[] args, String textToSpeak) {
        UserInterface.textToSpeak = textToSpeak;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        control = new ImageView(PAUSE_IMG);
        control.setOnMouseEntered(e -> control.setEffect(new Glow(0.25)));
        control.setOnMouseExited(e -> control.setEffect(new Glow(0.0)));
        control.setOnMousePressed(e -> mousePressed(e, primaryStage));
        control.setOnMouseReleased(this::mouseReleased);
        control.setOnMouseDragged(e -> mouseDragged(e, primaryStage));
        control.setOnScroll(this::mouseScrolled);

        Text text = new Text(textToSpeak);
        text.setFont(Font.font(12));
        text.setTextAlignment(TextAlignment.JUSTIFY);
        text.setWrappingWidth(2 * PAUSE_IMG.getWidth());
        text.setFill(Color.BLUE);
        VBox container = new VBox(control, text);
        container.setAlignment(Pos.CENTER);
        container.setBackground(Background.fill(Color.TRANSPARENT));
        Scene scene = new Scene(container, Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().add(ICON_IMG);
        primaryStage.setTitle("Read It");
        showOnCurrentScreen(primaryStage);
        new Thread(() -> ReadIt.speakIt(textToSpeak)).start();
    }

    private void showOnCurrentScreen(Stage stage) {
        stage.show();
        Point location = MouseInfo.getPointerInfo().getLocation();
        Screen screen = Screen.getScreensForRectangle(location.getX(), location.getY(), 1, 1).get(0);
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();
        Rectangle2D visualBounds = screen.getVisualBounds();
        double minX = visualBounds.getMinX();
        double minY = visualBounds.getMinY();
        double screenWidth = visualBounds.getWidth();
        double screenHeight = visualBounds.getHeight();
        stage.setX(minX + (screenWidth - stageWidth) / 2);
        stage.setY(minY + (screenHeight - stageHeight) / 2);
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
