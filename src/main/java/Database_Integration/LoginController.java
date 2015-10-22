package Database_Integration;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

// This is the Login Window for only after the user has logged in once, in which this one has a cancel button to go back to the main home screen where the other one does not because the user needs to log in on that one or exit

public class LoginController implements Initializable {
    public Button loginButton;
    public Button cancelButton;
    public ComboBox comboBox;
    public TextField passwordField;
    public static List<User> userList;


    public void login(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        for(User user: userList) {
            if(((String) comboBox.getValue()).equals(user.getUsername())) {          // if the comboBox value is equal to one of the user's usernames being cycled through, it will then go on to check the password
                User attemptUser = user;

                String checkHash = attemptUser.getPassword();
                String checkPassword = passwordField.getText();                    // the hashed password from the user object and the password from the password textbox are used to check against each other

                boolean isWhitespace = containsWhiteSpace(checkPassword);        // the password input is checked for whitespaces to ensure that the user does not try to use any type of injection to the database
                if(isWhitespace) {
                    alertBox("Password Not Allowed", "You cannot have spaces in your password.");
                    throw new IllegalArgumentException("Whitespaces found. Possible SQL or LDAP Injection");      // if there are whitespaces an exception is thrown before the input is sent to the server
                }

                BusinessLayer bl = new BusinessLayer();
                boolean answer = bl.checkHashFunction(checkPassword, checkHash);        // the user object password is already hashed and is checked against the textbox's password that will be hashed before compared

                if(answer == true) {                                                    // if the hashed passwords are equal then "answer" is true
                    bl.changeUserG(user, checkPassword);                               // if password checks out, the user login to sql server is changed to the user defined in the comboBox and the scene switches back to the table window
                    bl.switchSceneWithDiffSize(event, "Interface.fxml", 700, 500);
                    return;                                                                // if the user change is successful, the loop is returned so it does not cycle through anymore User objects
                } else {
                    Alert alertBox = new Alert(Alert.AlertType.ERROR);
                    alertBox.setTitle("Password Not Found");
                    alertBox.setContentText("The password that was entered does not match the password for " + user.getLastName() + " " + user.getFirstName() + " that is on file." +
                            "\n\nPlease try again or stay on the current user account by pressing \"Cancel\"");
                    alertBox.showAndWait();
                }
            }
        }
    }
    public void cancel(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.switchSceneWithDiffSize(event,"Interface.fxml", 700, 500);
    }

    public boolean containsWhiteSpace(String password) {
        if(password != null){
            for(int i = 0; i < password.length(); i++){
                if(Character.isWhitespace(password.charAt(i))){
                    return true;
                }
            }
        }
        return false;
    }

    public void alertBox(String title, String content) {
        Alert alertBox = new Alert(Alert.AlertType.ERROR);
        alertBox.setTitle(title);
        alertBox.setContentText(content);
        alertBox.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the Login Users in ComboBox
        BusinessLayer bl = new BusinessLayer();
        try {
            this.userList = bl.getUsersG();                 // the users are gotten from a query to dbo.Users to retrieve all the users and their passwords.. userList is also instantiated as the instance for the class so it can be used in login

            for(User user: userList) {
                comboBox.getItems().add(user.getUsername());         // the list of users is cycled through and their usernames are added to the ComboBox
            }

            String currentUser = "b1tn3r";
            comboBox.setValue(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
