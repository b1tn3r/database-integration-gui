package Database_Integration;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpWindow {

    public HelpWindow() throws IOException {

        Stage stage = new Stage();
        stage.setX(1150);
        stage.setY(200);
        stage.setTitle("Help Window");

        Text text1 = new Text("Logging into the Application");
        text1.setFont(new Font("Verdana", 20));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        text1.setEffect(dropShadow);
        Text text2 = new Text(
                "\n\nAn authorized Username and Password need to be\n" +
                "entered in the logon screen to gain access to certain\n" +
                "features of the database. The logout button can be pressed\n" +
                "to log out of the database and log in as a different\n" +
                "user. If you would like to stay on the same user account\n" +
                "press \"Cancel\" to go back to the main table window\n" +
                "without changing the account.\n\n" +
                "You will only be able to access data that you are authorized\n" +
                "to use in the database. Before using this application it\n" +
                "may be needed to check with your designated department to\n" +
                "ensure you have all the proper permissions to access each\n" +
                "part of the database needed for your position.");
        TextFlow leftTextFlow = new TextFlow();
        leftTextFlow.getChildren().addAll(text1, text2);
        leftTextFlow.setPadding(new Insets(10, 10, 10, 10));

        ScrollPane scrollPaneLeft = new ScrollPane(leftTextFlow);

        Text text3 = new Text("Application Functions");
        text3.setFont(new Font("Verdana", 20));
        DropShadow dropShadow2 = new DropShadow();
        dropShadow2.setRadius(5.0);
        dropShadow2.setOffsetX(3.0);
        dropShadow2.setOffsetY(3.0);
        dropShadow2.setColor(Color.color(0.4, 0.5, 0.5));
        text3.setEffect(dropShadow2);
        Text text4 = new Text(
                "\n\nRetrieve All Employee Data: All the Employees within\n" +
                "the company can be searched for by pressing the \"Search\"\n" +
                "button without anything in the text box." +
                "\n\nSearch for Employees: A specific employee or\n" +
                "employees can be searched for in the database entering\n" +
                "their first or last name into the search text box or a\n" +
                "portion of their first or last name can be entered to\n" +
                "retrieve any results with the series of letters in\n" +
                "them." +
                "\n\nAdd Employee: An employee can be added by pressing\n" +
                "the \"Add Employee\" button and inputting all the text\n" +
                "fields in the form along with adding a file for the photo.\n" +
                "The \"Add to Databaes\" button can then be pressed to add\n" +
                "the employee to the database or the \"Back\" button can\n" +
                "be pressed to go back to the Employee table." +
                "\n\nDelete Employee: An employee record or multiple records\n" +
                "selected by holding CTRL and left mouse clicks can SHIFT\n" +
                "and UP or DOWN arrow keys can be deleted after they are\n" +
                "selected and then clicking the \"Delete\" button." +
                "\n\nUpdate Employee Data: Individual data in an employee\n" +
                "record in the employee table can be updated by selecting\n" +
                "the desired record in the table and double clicking on\n" +
                "the cell you want to change. A textbox will appear to\n" +
                "change the value and then ENTER should be pressed to save\n" +
                "the update to the database." +
                "\n\nView Photo from Record: A photo saved to an employee's\n" +
                "file can be viewed from the main table window by searching\n" +
                "for a specific employee name to get the records for them\n" +
                "that include their photo's location in the temp folder\n" +
                "and selecting the desired record the photo is present in\n" +
                "and then navigating to the \"Photo\" menu and selecting\n" +
                "\"Display Selected Photo\", which will open a new window\n" +
                "that shows the image. If the image is not displayed the\n" +
                "first time, then the program needs ot be restarted to\n" +
                "successfully display the photo since the program does not\n" +
                "recognize the new photo created in temp until the program\n" +
                "has been restarted with the file already in the temp folder\n" +
                "on startup. When the image appears successfully, it can be\n" +
                "saved to any directory as a jpg, png or any type file. A new\n" +
                "image that is already located in the program's temp folder\n" +
                "can be opened and displayed in the window.\n\n" +
                "Changing the Program's Design Theme: The toggle button\n" +
                "named \"Toggle Visuals\" can be pressed to change the\n" +
                "theme of the current table window and it can be pressed\n" +
                "again to switch the design back\n\n" +
                "Viewing Audit History of an Employee: The current history\n" +
                "of a specific employee's file and data in the company's\n" +
                "database can be viewed and managed by pressing the button,\n" +
                "\"View Audit History\n\n, that will open up a new window with\n" +
                "a table displaying the activity of the user's file for when\n" +
                "they were added as an employee, when their file and data were\n" +
                "updated in any way such as a promotion to a new position, and\n" +
                "any other updated data. Their audit history will be deleted\n" +
                "when the employee is deleted in the database." +
                "\n\nEditing Audit History: The audit history data can be managed\n" +
                "by deleting any audit history data with the \"Delete Record\"\n" +
                "button in the audit history table window and first selecting\n" +
                "a record to delete in the Employee's audit table. The audit\n" +
                "history records can also have their action column's description\n" +
                "changed and updated to the database by double clicking on the\n" +
                "cell wanting to update.");
        TextFlow rightTextFlow = new TextFlow();
        rightTextFlow.getChildren().addAll(text3, text4);
        rightTextFlow.setPadding(new Insets(10, 10, 10, 10));

        ScrollPane scrollPaneRight = new ScrollPane(rightTextFlow);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(scrollPaneLeft, scrollPaneRight);

        Scene scene = new Scene(splitPane, 750, 600);
        stage.setScene(scene);
        stage.show();
    }
}
