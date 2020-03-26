package GUI; /**
 * Intro to JavaFX - https://openjfx.io/openjfx-docs/#introduction
 */
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUI extends Application {
    private final String TITLE = "PCTSR";
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    // in JavaFX, the window is called Stage
    private Stage window;

    public void run () {
        // method from Application to set up the program as Java FX app
        launch();
    }

    // abstract method from Application
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        this.window.setTitle(this.TITLE);
        this.window.setScene(getScene());
        this.window.show();
    }

    // creates the 'canvas'
    private Scene getScene() {
        // the layouts are called panes. This is a default layout from JavaFX - https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane pane = new BorderPane();
        pane.setTop(getHorizontalTextBox("Prize Collecting Traveling Sales Representative", 22));
        pane.setBottom(getHorizontalTextBox("Created by: adwi@itu.dk & kald@itu.dk", 20));
        // TODO create an input menu on the left and a visualization box in the center
        return new Scene(pane, this.WIDTH, this.HEIGHT);
    }

    private HBox getHorizontalTextBox(String text, int fontSize) {
        Label label = createLabel(text, TextAlignment.CENTER, fontSize);
        return new HBox() {{
            getChildren().add(label);
            setAlignment(Pos.CENTER);
            setBackground(new Background(new BackgroundFill(Color.gray(0.8, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        }};
    }

    private Label createLabel(String text, TextAlignment alignment, int fontSize) {
        return new Label(text) {{
            setTextAlignment(alignment);
            setFont(new Font("Arial", fontSize));
        }};
    }
}