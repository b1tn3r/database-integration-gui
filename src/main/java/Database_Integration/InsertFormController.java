package Database_Integration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InsertFormController implements Initializable {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField positionField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField homePhoneField;
    @FXML private TextField photoField;
    @FXML private Button addPhoto;
    @FXML private Button addToDB;
    @FXML private Stage stage;                // stage is used to hold the stage passed through the init method as this.stage
    @FXML private Button backButton;
    @FXML private MenuItem closeItem;
    @FXML private MenuItem aboutItem;
    @FXML private MenuItem backItem;

    @FXML
    private void closeFromMenu(ActionEvent event) {
        BusinessLayer bl = new BusinessLayer();
        bl.closeFromMenuG();
    }
    @FXML
    private void backToTable(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.switchScene(event, "Interface.fxml");
    }
    @FXML
    private void about(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.aboutG();
    }

    @FXML
    private void addToDatabase(ActionEvent event) {
        try {
            String lastName = lastNameField.getText();
            String firstName = firstNameField.getText();
            String position = positionField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String phone = homePhoneField.getText();         // all variables set from their text fields values except for file, which needs to be taken from a filechooser

            File file = new File(photoField.getText());         // a new file is made from the path given to the photoField from the fileChooser

            Employee employee = new Employee(0, lastName, firstName, position, address, city, phone, file);     // an Employee object is created from all the values in the fields

            BusinessLayer bl = new BusinessLayer();
            bl.addEmployeeG(employee, event);

        } catch(SQLException e) {
            alertBox("You do not have the proper permissions to add the record to the database.");
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            alertBox(e.getMessage());
            e.printStackTrace();
        } catch(IOException e) {
            alertBox(e.getMessage());
            e.printStackTrace();
        } catch(Exception e) {
            alertBox(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void init(Stage stage) {        // this method is sent from Main and is used to pass the primaryStage to this controller
        this.stage = stage;
    }

    // the button opens a fileChooser window that returns a file that sets its file path to the photoField
    @FXML
    private void addPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open an Image");

        File file = fileChooser.showOpenDialog(stage);      // stage is passed in as a parameter

        if(file == null) {
            Tooltip tool = new Tooltip("No File was Entered");
            addPhoto.setTooltip(tool);
        } else {
            String path = file.getPath();            // the file returned from the fileChooser has it's path set to the photoField
            photoField.setText(path);
        }
    }

    @FXML
    private void alertBox(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
