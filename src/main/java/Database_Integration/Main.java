package Database_Integration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interface.fxml"));     // changed to simple loader so it can be loaded and the controller can be get
        Parent root = loader.load();                                    // the loader for the Interface.fxml (with Controller) needs to be loaded so that Main can call methods to the controller, even if root is not used in the code
        Controller controller = loader.getController();                   // retrieves the controller so it can be referenced to in this class
        loader.setController(controller);                                 // sets the mainController for the loader so it gives a home controller


        primaryStage.setTitle("Search Engine");
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/FirstLogin.fxml"));       // the fxml files are placed in the resources dir in maven, so getResource automatically is directed to that directory
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