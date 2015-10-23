package Database_Integration;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField textField;
    public Button searchButton;
    public Button addEmployeeButton;
    public TableView<Employee> table;
    public MenuItem closeItem;
    public MenuItem aboutItem;
    public Button deleteButton;
    public MenuItem displayPhotoItem;
    public PhotoWindow photoController;
    public Label userLabel;
    public Button historyButton;
    public Button logoutButton;
    public ToggleButton toggleVisualsButton;
    public Button changePasswordButton;

                                        // USE FORWARD SLASHES FOR THIS ONE BELOW
    private final String projectPath = "/Database_Integration/";
    private static String path;

    public void properClose(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> {            // the properClose method is set here due to surprising errors
            e.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exiting");
            alert.setContentText("Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                BusinessLayer bl = new BusinessLayer();
                bl.deleteFolderFiles(new File(path + "temp"));           // on a close of the window from the 'X', the deleteFolderFiles method is called to delete all the files in the temp folder that is used to temporarily save all the files from the database
                try {
                    DataAccessObject dao = new DataAccessObject();
                    dao.close();                                         // in case there is still an open Connection, the DataAccessObject's close() method is called to close the Connection if it is not null
                } catch (SQLException e1){}
                primaryStage.close();
            }
        });
    }

    public void closeFromMenu() {                     // used to close the window from the MenuItem "Close"
        BusinessLayer bl = new BusinessLayer();
        bl.closeFromMenuG();
    }

    public void alertBox(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }
    public void about(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.aboutG();
    }

    public void changePassword(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.switchSceneWithDiffSize(event, "ChangePassword.fxml", 611, 200);
    }

    public void displaySelectedPhoto(ActionEvent event) throws IOException {
        /* THIS WOULD OPEN A NEW SCENE UP WITHOUT ANY COMMUNICATION TO THE MAIN WINDOW
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PhotoWindow.fxml"));
        root = loader.load();
        Stage stage = new Stage();
        stage.setX(50);
        stage.setY(200);
        stage.setTitle("Image Viewer");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        */
        try {
            Employee employee = table.getSelectionModel().getSelectedItem();
            File file = employee.getPhoto();                                                      // gets the selected Employee object in the table and gets its photo as a file
            Controller controller = new Controller();
            photoController = new PhotoWindow(controller, file);                              // The PhotoWindow object is initialized creating a new stage that  has the current object (controller) sent through so PhotoWindow can communicate with it.. also "file" is sent through to tell PhotoWindow what file to open
        } catch(NullPointerException e) {
            alertBox("You need to select only one row that needs to be displaying a Photo Column.");
        } catch(IOException e) {
            alertBox("You need to select only one row that needs to be displaying a Photo Column.");
        } catch(IllegalArgumentException e) {
            alertBox("The program directory does not recognize the image that has just been loaded into temp. The only way " +
                    "to open the selected file that has by now been saved in the temp directory is to reload the program " +
                    "and repeat the same action.");
            e.printStackTrace();
        }
    }

    public void search(ActionEvent e) {

        try {
            String searchText = textField.getText();

            int answer;          // answer will be used to determine if searchEmployees was called or getAllEmployees and will be used to determine if Photo should be added to the table

            if (searchText != null && searchText.trim().length() > 0) {    // if there are one or more characters in the text field, searchEmployees() is called with the text field input as the parameter
                answer = 1;
            } else {
                answer = 2;
            }

            BusinessLayer bl = new BusinessLayer();
            List<Employee> list = bl.findEmployees(searchText, answer);             // the text from the search field is passed in along with the answer which will determine if allEmployees should be returned or only a specific employee/employees are searched for


            bl.displayTable(list, answer, table);              // the displayTable method is called to input the list into the TableView

        } catch(SQLException ex) {
            alertBox("You do not have the proper permissions to access data from the Employees records.");
        } catch(ClassNotFoundException ex) {
            alertBox(ex.getMessage());
        } catch(IOException ex) {
            alertBox(ex.getMessage());
        } catch (Exception ex) {
            alertBox("Error found when returning search results.");
        }
    }

    // This method will change the scene of the Window to the Insert Employee form
    public void addEmployee(ActionEvent event) throws IOException {                        // the stage cannot be sent from main like in properClose because this is an action from a Button
        BusinessLayer bl = new BusinessLayer();
        bl.switchScene(event, "InsertForm.fxml");               // switchScene called from BusinessLayer that switches from the scene where the ActionEvent took place to the scene with "InsertForm.fxml" as the fxml file
    }

    public void deleteEmployee(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        try {
            ObservableList<Employee> selectedEmployees;
            selectedEmployees = table.getSelectionModel().getSelectedItems();          // all the selected rows in the table are placed in an ObservableList

            BusinessLayer bl = new BusinessLayer();
            bl.deleteEmployeeG(selectedEmployees, table);                    // the selectedEmployee ObservableList is passed to the business layer
        } catch(SQLException ex) {
            alertBox("You do not have the proper permissions to delete the selected rows.");
        }
    }
    public void logout(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.switchSceneWithDiffSize(event, "Login.fxml", 611, 200);
    }
    public void viewHistory(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        try {
            Employee employee = table.getSelectionModel().getSelectedItem();        // the selected employee is gotten

            BusinessLayer bl = new BusinessLayer();
            bl.createAuditTable(employee);                              // the employee selected by the user is sent through to query the database and create the table from its initial data
        } catch(NullPointerException ex) {
            alertBox("One employee must be selected in the table to view their data history.");
        } catch(SQLServerException ex) {
            alertBox("You do not have the proper permissions to access audit history.");
        } catch(Exception ex) {
            alertBox("There was an unknown error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void toggleVisuals(ActionEvent event) {
        Node stageNode = (Node) event.getSource();               // the Scene  that the even takes place in is get
        Scene scene = stageNode.getScene();
        String css = this.getClass().getResource("/mainStylesheet.css").toExternalForm();          // the css stylesheet is get and stored to "css" variable
        if(toggleVisualsButton.isSelected()) {
            scene.getStylesheets().add(css);                          // when the toggle button is pressed, either the stylesheet is added or removed to the current scene, in which if the it is toggled on, it is added and if toggled off, it is removed
        } else {
            scene.getStylesheets().remove(css);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the path variable
        String targetPath = this.getClass().getResource(projectPath).getPath();     // retrieve the current local path the application is in, which will point to ../target/classes/Database_Integration/, but it needs to be changed to point to the java source code
        String localPathToProject = targetPath.substring(0, targetPath.length() - 36);                     // this takes off some directories so it is a direct path to the project in the local path it is in on the computer it is run on
        this.path = localPathToProject + "src/main/java/Database_Integration/";             // this adds the directory of the folder that all the java code is located in

        // Initialize User Label with Username logged in
        BusinessLayer bl = new BusinessLayer();
        User user = bl.getUserForLabel();                         // the getUserForLabel method is called to retrieve the User object stored in the BusinessLayer after the User has signed in
        if(user != null) {
            userLabel.setText(user.getUsername());                 // the user object's username is set to the text for the label
        }

        bl.setPathVariable(path);
        DataAccessObject dao = new DataAccessObject();
        dao.setPathVariable(path);
    }
}
