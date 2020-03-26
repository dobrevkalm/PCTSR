/**
 * This is just a class I use to test random stuff
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class t extends Application {
    // a button to use in FX
    Button button;

    public static void main(String[] args) throws CloneNotSupportedException {
        // method from Application to set up the program as Java FX app
        launch(args);
    }

    // abstract method from Application
    @Override
    public void start(Stage primaryStage) throws Exception {
        // the stage in JavaFX is the window. Title referes to the window title
        primaryStage.setTitle("Test visualization");

        button = new Button("click");
        // we can also set the text like this
        //button.setText("click");

        // create a layout
        StackPane layout = new StackPane();
        // append the button
        layout.getChildren().add(button);

        // in FX, the canvas is called a scene
        // pass a layout + size in pixels
        Scene scene = new Scene(layout, 300, 300);
        // append the scene from the stage
        primaryStage.setScene(scene);
        // run the visualization
        primaryStage.show();
    }
}
