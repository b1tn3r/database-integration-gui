package Database_Integration;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Interface.fxml"));     // changed to simple loader so it can be loaded and the controller can be get
        Controller controller = loader.getController();                   // retrieves the controller so it can be referenced to in this class
        loader.setController(controller);                                 // sets the mainController for the loader so it gives a home controller


        primaryStage.setTitle("Search Engine");
        //loader.setLocation(Main.class.getResource("FirstLogin.fxml"));   NEED TO FIX!!!!
        Parent loginRoot = FXMLLoader.load(getClass().getResource("FirstLogin.fxml"));
        Scene toScene = new Scene(loginRoot, 611, 200);
        primaryStage.setScene(toScene);
        primaryStage.show();

        controller.properClose(primaryStage);

        InsertFormController formController = new InsertFormController();
        formController.init(primaryStage);                                  // this is used to pass the primaryStage to the insertForm's controller
    }


    public static void main(String[] args) {
        launch(args);
    }
}