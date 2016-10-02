package readit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutDialog extends Stage {

    public AboutDialog(Image image) {
        initModality(Modality.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        initStyle(StageStyle.UTILITY);
        setTitle("About");

        ImageView appIcon = new ImageView(image);
        WebView infoView = new WebView();
        infoView.setPrefSize(500, 300);
        infoView.getEngine().loadContent(""
                + "<html><body>"
                + "<h1>About</h1>"
                + "<p>This is a convenient text to speech (TTS) program.</p>"
                + "<p>To use the program set a shortcut key combination to launch "
                + "the program, then select text in any application and press "
                + "the assigned shortcut key combination to listen to the "
                + "computer read it out.</p>"
                + "<font color=\"blue\">Programmed by: <br>"
                + "Sourabh Bhat (heySourabh@gmail.com)</font>"
                + "</body></html>"
        );
        HBox hBox1 = new HBox(appIcon, infoView);
        hBox1.setSpacing(20);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> this.close());
        HBox hBox2 = new HBox(closeButton);
        hBox2.setAlignment(Pos.BOTTOM_CENTER);

        VBox vBox = new VBox(hBox1, hBox2);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20));

        HBox.setHgrow(infoView, Priority.ALWAYS);
        VBox.setVgrow(hBox1, Priority.ALWAYS);

        setScene(new Scene(vBox));
    }
}
