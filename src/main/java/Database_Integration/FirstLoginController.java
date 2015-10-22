package Database_Integration;
// Opposed to Login.fxml and LoginController, this will be the login screen that the application opens on and will not have a cancel button.. only a Login button

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FirstLoginController implements Initializable {
    public Button loginButton;
    public ComboBox comboBox;
    public TextField passwordField;
    public List<User> userList;

    public void login(ActionEvent event) throws SQLException, IOException, ClassNotFoundException, IllegalArgumentException {
        for(User user: userList) {
            if(((String) comboBox.getValue()).equals(user.getUsername())) {
                User attemptUser = user;

                String checkHash = attemptUser.getPassword();
                String checkPassword = passwordField.getText();

                boolean isWhitespace = containsWhiteSpace(checkPassword);        // the password input is checked for whitespaces to ensure that the user does not try to use any type of injection to the database
                if(isWhitespace) {
                    alertBox("Password Not Allowed", "You cannot have spaces in your password.");
                    throw new IllegalArgumentException("Whitespaces found. Possible SQL or LDAP Injection");      // if there are whitespaces an exception is thrown before the input is sent to the server
                }

                BusinessLayer bl = new BusinessLayer();
                boolean answer = bl.checkHashFunction(checkPassword, checkHash);

                if(answer == true) {
                    bl.changeUserG(user, checkPassword);
                    bl.switchSceneWithDiffSize(event, "Interface.fxml", 700, 500);
                    return;
                } else {
                    Alert alertBox = new Alert(Alert.AlertType.ERROR);
                    alertBox.setTitle("Password Not Found");
                    alertBox.setContentText("The password that was entered does not match the password for " + user.getLastName() + " " + user.getFirstName() + " that is on file." +
                            "\n\nPlease try again.");
                    alertBox.showAndWait();
                }
            }
        }
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
            this.userList = bl.getUsersG();

            for(User user: userList) {
                comboBox.getItems().add(user.getUsername());
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