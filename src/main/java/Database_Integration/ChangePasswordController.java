package Database_Integration;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {
    public TextField oldPasswordField;
    public TextField newPasswordField;
    public TextField confirmPasswordField;
    public Button submitButton;
    public Button cancelButton;

    public void submit(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        BusinessLayer bl = new BusinessLayer();
        User user = bl.getCurrentUser();                                                         // the User object for the user currently logged in is returned so its password can be checked

        String checkHash = user.getPassword();
        String checkPassword = oldPasswordField.getText();                 // user's hashed password and password from field are initialized.. password from field is checked for whitespaces to stop sql injection
        boolean isWhitespace = containsWhiteSpace(checkPassword);
        if(isWhitespace) {
            alertBox("Password Not Allowed", "You cannot have spaces in your input in the Old Password textbox.");
            throw new IllegalArgumentException("Whitespaces found. Possible SQL or LDAP Injection");               // if there are whitespaces in the password, the task is stopped by throwing an exception
        }

        boolean answer = bl.checkHashFunction(checkPassword, checkHash);                    // password in the field is hashed by the server and checked against the old password's hash value in the user object

        if(answer == true) {
            String newPassword1 = newPasswordField.getText();
            String newPassword2 = confirmPasswordField.getText();
            if(newPassword1.equals(newPassword2)) {                                                                         // the new and confirm passwords are compared to ensure they match one another
                boolean isWhitespace2 = containsWhiteSpace(newPassword1);
                if(isWhitespace2) {
                    alertBox("Password Not Allowed", "You cannot have spaces in your input in the New Password or Confirm Password textboxes.");        // checks if either the new or confirm password fields have whitespaces in the input
                    throw new IllegalArgumentException("Whitespaces found. Possible SQL or LDAP Injection");
                }

                boolean ask = askSave();         // the user is asked if they want to save before the save occurs so the program does not stop for the user while elevated privileges are activated
                if(ask) {

                    bl.switchToFromAdmin("admin", user, newPassword1);              // the window is only given admin privileges by switching to the integratedSecurity=true connectionURL after all input filters have been passed and the program is ready to change the server login password.. by not allowing the window admin privileges until the password is ready to be changed enables more security so the user does not have a chance to crash the program in any way while admin privileges are enables which would allow the user to keep elevated privileges if they are able to keep the program active after they crash it

                    bl.updatePassword(newPassword1, user);                   // this sends the new password in to be both updated in the Users table and for their server login password

                    bl.switchToFromAdmin("user", user, newPassword1);              // after the passwords are updated, the connectionURL is switched to the username and new password they have entered

                    bl.switchSceneWithDiffSize(event, "Interface.fxml", 700, 500);            // the scene is then switched to the main table scene after auto logging in the user
                }
            } else {
                alertBox("Passwords Do Not Match", "The password you entered in the New Password textbox does not match the textbox input for the Confirm Password.");
            }
        } else {
            Alert alertBox = new Alert(Alert.AlertType.ERROR);
            alertBox.setTitle("Incorrect Old Password");
            alertBox.setContentText("The old password that was entered for " + user.getLastName() + " " + user.getFirstName() + " does not match the current password on file." +
                    "\n\nPlease try again or keep your current password and press \"Cancel\" to go back to the login screen.");
            alertBox.showAndWait();
        }
    }
    public void alertBox(String title, String content) {
        Alert alertBox = new Alert(Alert.AlertType.ERROR);
        alertBox.setTitle(title);
        alertBox.setContentText(content);
        alertBox.showAndWait();
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
    public boolean askSave() {
        Alert alertBox = new Alert(Alert.AlertType.CONFIRMATION);
        alertBox.setTitle("Save Password");
        alertBox.setContentText("Are you sure you want to save your new password?");
        Optional<ButtonType> result = alertBox.showAndWait();
        if(result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    public void cancel(ActionEvent event) throws IOException {
        BusinessLayer bl = new BusinessLayer();
        bl.switchSceneWithDiffSize(event, "Interface.fxml", 700, 500);                          // if the user cancels they will go back to the main table screen without any changes or their connectionURL not being changed ever
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
