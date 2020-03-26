package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Place;

import java.util.List;

/**
 * Intro to JavaFX - https://openjfx.io/openjfx-docs/#introduction
 */
public class GUI extends Application {
    private double[][] coordinates;
    private final String TITLE = "PCTSR";
    private final int WIDTH = 1200;
    private final int HEIGHT = 800;
    // in JavaFX, the window is called Stage
    private Stage window;
    // used to draw inside our main canvas
    private GraphicsContext drawTool;

    public void run () {
        // method from Application to set up the program as Java FX app
        launch();
    }

    // abstract method from Application
    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        this.window.setTitle(this.TITLE);
        this.window.setScene(getScene());
        this.window.show();
    }

    // creates the 'canvas'
    private Scene getScene() {
        // the layouts are called panes. This is a default layout from JavaFX - https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(225, 229, 204), CornerRadii.EMPTY, Insets.EMPTY)));
        // set the layout sections
        pane.setTop(getHorizontalTextBox("Prize Collecting Traveling Sales Representative", 22));
        pane.setBottom(getHorizontalTextBox("Created by: adwi@itu.dk & kald@itu.dk", 20));
        pane.setLeft(getInteractionPanel());
        pane.setCenter(getMainCanvas());
        return new Scene(pane, this.WIDTH, this.HEIGHT);
    }

    private Canvas getMainCanvas() {
        Canvas canvas = new Canvas();
        this.drawTool = canvas.getGraphicsContext2D();
        this.drawTool.setStroke(Color.BLACK);
        this.drawTool.setLineWidth(2);
        test();
        return canvas;
    }

    private VBox getInteractionPanel() {
        final TextAlignment alignment = TextAlignment.LEFT;
        final int fontSize = 12;
        return new VBox() {{
           getChildren().addAll(
                   createLabel("Starting vertex:", alignment, fontSize),
                   createInputTextField("0 to 90"),
                   createLabel("Agents number:", alignment, fontSize),
                   createInputTextField("1 to 10"),
                   createLabel("Desired profit:", alignment, fontSize),
                   createInputTextField("max 300"),
                   createLabel("Method:", alignment, fontSize),
                   createInputTextField("one or two"),
                   createStartButton()
           );
            setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 204, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
            setSpacing(5);
            List<Node> children = getChildren();
            for(int i = 0; i < children.size(); i++) {
                if (i % 2 == 0) {
                    setMargin(children.get(i), new Insets(20, 10, 0, 5));
                } else {
                    setMargin(children.get(i), new Insets(0, 10, 0, 5));
                }
            }
        }};
    }

    private Button createStartButton() {
        return new Button("Run") {{
            setOnAction(event -> {
                test();
            });
        }};
    }

    public void test() {
        this.drawTool.beginPath();
        this.drawTool.moveTo(100, 100);
        this.drawTool.quadraticCurveTo(30, 150, 300, 200);
        this.drawTool.fill();
        this.drawTool.closePath();
    }

    // create a simple input field with placeholder
    private TextField createInputTextField(String placeHolder) {
        return new TextField() {{
           setPromptText(placeHolder);
        }};
    }

    // creates a node that contains a text box
    private HBox getHorizontalTextBox(String text, int fontSize) {
        Label label = createLabel(text, TextAlignment.CENTER, fontSize);
        return new HBox() {{
            getChildren().add(label);
            setMargin(getChildren().get(0), new Insets(5));
            setAlignment(Pos.CENTER);
            setBackground(new Background(new BackgroundFill(Color.gray(0.8, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        }};
    }

    // create a simple text
    private Label createLabel(String text, TextAlignment alignment, int fontSize) {
        return new Label(text) {{
            setTextAlignment(alignment);
            setFont(new Font("Arial", fontSize));
        }};
    }

    public void setPlacesCoordinates(double[][] coordinates) {
        this.coordinates = coordinates;
    }
}