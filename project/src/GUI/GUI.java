package GUI;

import helpers.Heuristic;
import helpers.HeuristicOne;
import helpers.HeuristicTwo;
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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.util.List;

/**
 * Intro to JavaFX - https://openjfx.io/openjfx-docs/#introduction
 */
public class GUI extends Application {
    private Canvas canvas;
    // used to draw inside our main canvas
    private double[][] distanceMatrix;
    private Place[] places;
    private GUIUtil guiUtil;
    private PathResult[] pathResults;
    private TextField startVertexTxt;
    private TextField minProfitTxt;
    private TextField agentsNumberTxt;
    private int startVertex = -1;
    private static final int CANVAS_MARGIN = 20;
    private final Color[] color = {
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.VIOLET,
            Color.HOTPINK,
            Color.BROWN,
            Color.LIME,
            Color.AQUA,
            Color.GREY
    };

    public void run() {
        // method from Application to set up the program as Java FX app
        launch();
    }

    // abstract method from Application
    @Override
    public void start(Stage primaryStage) {
        final String TITLE = "PCTSR";
        //prepare data
        DataReader reader = new DataReader();
        distanceMatrix = reader.getDistanceMatrix();
        places = reader.getAllCompanies();
        guiUtil = new GUIUtil(places);

        //prepare window
        // in JavaFX, the window is called Stage
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(getScene());
        primaryStage.show();
    }

    // creates the 'canvas'
    private Scene getScene() {
        final int WIDTH = 1400;
        final int HEIGHT = 900;

        // the layouts are called panes. This is a default layout from JavaFX - https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(225, 229, 204), CornerRadii.EMPTY, Insets.EMPTY)));

        // set the layout sections
        pane.setTop(getHorizontalTextBox("Prize Collecting Traveling Sales Representative", 22));
        pane.setBottom(getHorizontalTextBox("Created by: adwi@itu.dk & kald@itu.dk", 20));
        pane.setLeft(getInteractionPanel());
        pane.setCenter(getMainCanvas());

        return new Scene(pane, WIDTH, HEIGHT);
    }

    private Pane getMainCanvas() {
        Pane wrapperPane = new Pane();
        this.canvas = new Canvas();
        wrapperPane.getChildren().add(canvas);

        // Bind the width/height property to the wrapper Pane
        canvas.widthProperty().bind(wrapperPane.widthProperty());
        canvas.heightProperty().bind(wrapperPane.heightProperty());

        // redraw when resized
        canvas.widthProperty().addListener(event -> draw(canvas));
        canvas.heightProperty().addListener(event -> draw(canvas));
        draw(canvas);

        return wrapperPane;
    }

    private VBox getInteractionPanel() {
        final TextAlignment alignment = TextAlignment.LEFT;
        final int fontSize = 12;
        initializeTextInputs();
        VBox box = new VBox();
        box.getChildren().addAll(
                createLabel("Starting vertex:", alignment, fontSize),
                this.startVertexTxt,
                createLabel("Desired profit:", alignment, fontSize),
                this.minProfitTxt,
                createLabel("Agents number:", alignment, fontSize),
                this.agentsNumberTxt,
                createStartButton("HeuristicOne", true),
                createStartButton("HeuristicTwo", false)
        );

        box.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 204, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        box.setSpacing(5);
        List<Node> children = box.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (i % 2 == 0) {
                box.setMargin(children.get(i), new Insets(20, 10, 0, 5));
            } else {
                box.setMargin(children.get(i), new Insets(0, 10, 0, 5));
            }
        }
        return box;
    }

    private Button createStartButton(String buttonText, boolean one) {
        return new Button(buttonText) {{
            setOnAction(e -> {
                getInputAndDraw(one);
            });
        }};
    }

    private void getInputAndDraw(boolean one) {
        try {
            int startingV = Integer.parseInt(this.startVertexTxt.getText());
            int minProfit = Integer.parseInt(this.minProfitTxt.getText());
            int agentsNumber = Integer.parseInt(this.agentsNumberTxt.getText());
            if (startingV >= 0 && startingV < 91 && minProfit > 0.0 && minProfit < guiUtil.getTotalProfit() - places[startingV].getFirmProfit() && agentsNumber > 0 && agentsNumber <= 10) {
                getResultPath(one, startingV, agentsNumber, minProfit);
                System.out.println(one + ", startVertex " + startingV + ", agents " + agentsNumber + " minPof: " + minProfit);
                draw(this.canvas);
            } else {
                System.out.println("Invalid input");
                this.pathResults = null;
                draw(this.canvas);
            }
        } catch (NumberFormatException e) {
            e.getStackTrace();
        }
    }

    private void initializeTextInputs() {
        this.startVertexTxt = new TextField();
        this.startVertexTxt.setPromptText("0 to 90");
        this.minProfitTxt = new TextField();
        this.minProfitTxt.setPromptText("max 300");
        this.agentsNumberTxt = new TextField();
        this.agentsNumberTxt.setPromptText("1 to 10");
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


    private void draw(Canvas canvas) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //clear canvas
        gc.clearRect(0, 0, width, height);
        //set line color
        gc.setStroke(Color.BLACK);
        //set oval color
        gc.setFill(Color.BLACK);
        //draw places
        drawPlaces(gc, width, height);
        if (this.pathResults != null) {
            drawPaths(gc, width, height);
        }
    }

    private void drawPlaces(GraphicsContext gc, int width, int height) {
        final int diameter = 4;
        if (places != null) {
            for (int i = 0; i < places.length; i++) {
                Place p = places[i];
                int x = guiUtil.getPlaceXposition(p, CANVAS_MARGIN, width - CANVAS_MARGIN);
                int y = guiUtil.getPlaceYposition(p, CANVAS_MARGIN, height - CANVAS_MARGIN);
                if (i == this.startVertex) {
                    gc.setFill(Color.GREEN);
                    gc.fillOval(x, y, diameter, diameter);
                    gc.setFill(Color.BLACK);
                } else {
                    gc.fillOval(x, y, diameter, diameter);
                }
            }
        }
    }

    private void drawPaths(GraphicsContext gc, int width, int height) {
        final int vertexRadius = 2;
        for (int i = 0; i < this.pathResults.length; i++) {
            gc.setStroke(color[i]);
            List<Place> rp = this.pathResults[i].getResultPath();
            gc.beginPath();
            int startX = guiUtil.getPlaceXposition(rp.get(0), CANVAS_MARGIN, width - CANVAS_MARGIN) + vertexRadius;
            int startY = guiUtil.getPlaceYposition(rp.get(0), CANVAS_MARGIN, height - CANVAS_MARGIN) + vertexRadius;

            gc.moveTo(startX, startY);
            for (int j = 1; j < rp.size() - 1; j++) {
                Place p = rp.get(j);
                int x1 = guiUtil.getPlaceXposition(p, CANVAS_MARGIN, width - CANVAS_MARGIN) + vertexRadius;
                int y1 = guiUtil.getPlaceYposition(p, CANVAS_MARGIN, height - CANVAS_MARGIN) + vertexRadius;
                gc.lineTo(x1, y1);
            }
            gc.lineTo(startX, startY);
            gc.stroke();
        }
    }

    private void getResultPath(boolean one, int startVertex, int agentsNumber, int minProfit) {
        this.startVertex = startVertex;
        Heuristic h;
        if (one) {
            h = new HeuristicOne(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
        } else {
            h = new HeuristicTwo(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
        }
        this.pathResults = h.getResultPaths();
    }
}