package GUI;

import heuristics.*;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.util.List;
import java.util.Locale;

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
    private Label errorMsg;
    private Button saveResultsBtn;
    private TextFlow totalResultTxt;
    private int startVertex = -1;
    private static final int FONT_SIZE = 12;
    private Color TEXT_COLOR = Color.rgb(31, 41, 34);
    private final Color[] color = {
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.VIOLET,
            Color.AQUA,
            Color.HOTPINK,
            Color.LIME,
            Color.BROWN,
            Color.GREY
    };
    //fields used for writing to the file
    private String resHeuristic = "";
    private String resDescription = "";

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
        this.distanceMatrix = reader.getDistanceMatrix();
        this.places = reader.getAllCompanies();
        this.guiUtil = new GUIUtil(this.places);

        //prepare window
        // in JavaFX, the window is called Stage
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(getScene());
        primaryStage.show();
    }

    // creates the 'canvas'
    private Scene getScene() {
        final int WIDTH = 860;
        final int HEIGHT = 700;

        // the layouts are called panes. This is a default layout from JavaFX - https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(225, 229, 204), CornerRadii.EMPTY, Insets.EMPTY)));

        // set the layout sections
        pane.setTop(getHorizontalTextBox("Prize Collecting Traveling Sales Representative", 22));
        pane.setBottom(getHorizontalTextBox("Created by: adwi@itu.dk & kald@itu.dk", FONT_SIZE));
        pane.setLeft(getInteractionPanel());
        pane.setCenter(getMainCanvas());

        return new Scene(pane, WIDTH, HEIGHT);
    }

    private Pane getMainCanvas() {
        Pane wrapperPane = new Pane();
        this.canvas = new Canvas();
        wrapperPane.getChildren().add(canvas);
        //add background image (this shows as an error in IntelliJ, but it's fine)
        wrapperPane.setStyle("-fx-background-image: url(\"/GUI/dk.png\");-fx-background-size: 100% 100%;-fx-background-repeat: no-repeat;");

        // Bind the width/height property to the wrapper Pane
        this.canvas.widthProperty().bind(wrapperPane.widthProperty());
        this.canvas.heightProperty().bind(wrapperPane.heightProperty());

        // redraw when resized
        this.canvas.widthProperty().addListener(event -> draw(this.canvas));
        this.canvas.heightProperty().addListener(event -> draw(this.canvas));
        draw(canvas);

        return wrapperPane;
    }

    private VBox getInteractionPanel() {
        final TextAlignment alignment = TextAlignment.LEFT;
        initializeTextInputs();
        VBox box = new VBox();
        createErrorMsgLabel(alignment, FONT_SIZE);
        this.totalResultTxt = new TextFlow();
        box.getChildren().addAll(
                createLabel("Starting vertex:", alignment, FONT_SIZE),
                this.startVertexTxt,
                createLabel("Desired profit:", alignment, FONT_SIZE),
                this.minProfitTxt,
                createLabel("Agents number:", alignment, FONT_SIZE),
                this.agentsNumberTxt,
                createHeuristicButton("HeuristicOne", "one"),
                createHeuristicButton("HeuristicTwo", "two"),
                createHeuristicButton("HeuristicThree", "three"),
                this.errorMsg,
                this.totalResultTxt,
                createSaveResultsButton()
        );

        box.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 204, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        box.setSpacing(8);
        List<Node> children = box.getChildren();
        for (int i = 0; i < children.size(); i++) {
            // only set the top margin for the input fields
            if (i % 2 == 0 && i < 7) {
                box.setMargin(children.get(i), new Insets(20, 10, 0, 5));
            } else {
                box.setMargin(children.get(i), new Insets(0, 10, 0, 5));
            }
        }
        return box;
    }

    private Button createSaveResultsButton() {
        this.saveResultsBtn = new Button("Save results") {{
            setOnAction(e -> {
                saveResultPathsToFile();
                setText("Saved");
                setDisable(true);
            });
            setPrefWidth(150);
            //hide the button
            setVisible(false);
        }};
        return this.saveResultsBtn;
    }

    private void saveResultPathsToFile() {
        ResultsPrinter.printToFile(this.resHeuristic, this.pathResults, this.resDescription);
    }

    private Button createHeuristicButton(String buttonText, String heuristic) {
        return new Button(buttonText) {{
            setOnAction(e -> {
                getInputAndDraw(heuristic);
            });
            setPrefWidth(150);
        }};
    }

    private void getInputAndDraw(String method) {
        //clear the error message, total distance and profit
        this.errorMsg.setText("");
        //clear detailed info text
        this.totalResultTxt.getChildren().clear();
        try {
            int startingV = Integer.parseInt(this.startVertexTxt.getText());
            int minProfit = Integer.parseInt(this.minProfitTxt.getText());
            int agentsNumber = Integer.parseInt(this.agentsNumberTxt.getText());
            if (startingV >= 0 && startingV < 91 && minProfit > 0.0 && minProfit < this.guiUtil.getTotalProfit() - this.places[startingV].getFirmProfit() && agentsNumber > 0 && agentsNumber <= 10) {
                // reset the save button
                this.saveResultsBtn.setDisable(false);
                this.saveResultsBtn.setText("Save results");
                getResultPath(method, startingV, agentsNumber, minProfit);
                System.out.println(method + ", startVertex " + startingV + ", agents " + agentsNumber + " minPof: " + minProfit);
                draw(this.canvas);
                //show the save results button
                this.saveResultsBtn.setVisible(true);
            } else {
                //set the error message
                String error = "Invalid input. \n";
                if (startingV < 0 || startingV > 90) {
                    error += "Starting vertex must be a number in range 0-90.\n";
                }
                if (startingV >= 0 && startingV < 91 && (minProfit < 0.0 || minProfit > this.guiUtil.getTotalProfit() - this.places[startingV].getFirmProfit())) {
                    error += "For chosen staring point profit can't be bigger than " + (int) (this.guiUtil.getTotalProfit() - this.places[startingV].getFirmProfit()) + ".\n";
                }
                if (agentsNumber < 0 || agentsNumber > 10) {
                    error += "Max no. of agents 10.";
                }
                this.errorMsg.setText(error);
                System.out.println("Invalid input");
                this.pathResults = null;
                draw(this.canvas);
                //hide the save results button
                this.saveResultsBtn.setVisible(false);
                //reset resDescription and resHeristic (variables used to print in the file)
                this.resDescription = "";
                this.resHeuristic = "";
            }
        } catch (NumberFormatException e) {
            this.errorMsg.setText("Invalid input type. Enter only numbers.");
            e.getStackTrace();
            //hide the save results button
            this.saveResultsBtn.setVisible(false);
            //reset resDescription and resHeristic (variables used to print in the file)
            this.resDescription = "";
            this.resHeuristic = "";
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
            setTextFill(TEXT_COLOR);
        }};
    }

    // create a simple text
    private Text createText(String text, TextAlignment alignment, int fontSize, Color color) {
        return new Text(text) {{
            setTextAlignment(alignment);
            setFont(new Font("Arial", fontSize));
            setFill(color);
        }};
    }

    private void createErrorMsgLabel(TextAlignment alignment, int fontSize) {
        this.errorMsg = new Label("");
        this.errorMsg.setTextAlignment(alignment);
        this.errorMsg.setFont(new Font("Arial", fontSize - 2));
        this.errorMsg.setTextFill(Color.RED);
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
        if (this.places != null) {
            for (int i = 0; i < this.places.length; i++) {
                Place p = this.places[i];
                int x = this.guiUtil.getPlaceXposition(p, width);
                int y = this.guiUtil.getPlaceYposition(p, height);
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
            gc.setStroke(this.color[i]);
            List<Place> rp = this.pathResults[i].getResultPath();
            gc.beginPath();
            int startX = this.guiUtil.getPlaceXposition(rp.get(0), width) + vertexRadius;
            int startY = this.guiUtil.getPlaceYposition(rp.get(0), height) + vertexRadius;

            gc.moveTo(startX, startY);
            for (int j = 1; j < rp.size() - 1; j++) {
                Place p = rp.get(j);
                int x1 = this.guiUtil.getPlaceXposition(p, width) + vertexRadius;
                int y1 = this.guiUtil.getPlaceYposition(p, height) + vertexRadius;
                gc.lineTo(x1, y1);
            }
            gc.lineTo(startX, startY);
            gc.stroke();
        }
    }

    private void getResultPath(String method, int startVertex, int agentsNumber, int minProfit) {
        this.startVertex = startVertex;
        Heuristic h = null;
        switch (method) {
            case "one":
                h = new HeuristicOne(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicOne";
                break;
            case "two":
                h = new HeuristicTwo(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicTwo";
                break;
            case "three":
                h = new HeuristicThree(this.distanceMatrix, this.places, startVertex, agentsNumber, minProfit);
                this.resHeuristic = "HeuristicThree";
                break;
        }
        this.pathResults = h.getResultPaths();
        //set detailed info used for GUI
        setDetailedInfo(h.getSumProfit());
        //set info used for printing results to the file
        this.resDescription = "Starting vertex: " + startVertex + "\n"
                + "Minimum profit: " + minProfit + "\n"
                + "Number of agents: " + agentsNumber + "\n"
                + "Total profit collected: " + String.format(Locale.US, "%.2f", h.getSumProfit()) + "\n"
                + "Total distance travelled: " + String.format(Locale.US, "%.2f", getTotalDistance()) + "\n\n";
    }

    private void setDetailedInfo(double sumProfit) {
        String totalInfo = "Total profit: " + String.format("%.2f", sumProfit) + "\n"
                + "Total distance: " + String.format("%.2f", getTotalDistance()) + "\n\n"
                + "Distance per agent:\n";
        Text infoTXT = createText(totalInfo, TextAlignment.LEFT, FONT_SIZE, TEXT_COLOR);
        this.totalResultTxt.getChildren().add(infoTXT);
        for (int i = 0; i < this.pathResults.length; i++) {
            PathResult p = this.pathResults[i];
            String agentNo = "#" + (i + 1) + " : ";
            String text = String.format("%.2f", p.getPathLength()) + "\n";
            Text t1 = createText(agentNo, TextAlignment.LEFT, FONT_SIZE, color[i]);
            Text t2 = createText(text, TextAlignment.LEFT, FONT_SIZE, TEXT_COLOR);
            this.totalResultTxt.getChildren().addAll(t1, t2);
        }
    }

    private double getTotalDistance() {
        double distance = 0.0;
        for (PathResult p : this.pathResults) {
            distance += p.getPathLength();
        }
        return distance;
    }
}