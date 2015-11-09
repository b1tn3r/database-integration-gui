package Database_Integration;
// This layer of the application will be used for data manipulation and calculations between the DataAccessObject and Presentation Layer Controllers

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class BusinessLayer {
    private static String path;
    private static String propertiesFile = "/connection.properties";

    private static User user;
    private static int userID;                 // this is used as an instance to hold the current value of the user so it can be used for inserting information into dbo.AuditHistory table

    protected void changeUserG(User user, String password) throws Exception {
        DataAccessObject dao = new DataAccessObject();
        this.user = dao.changeProperty(user, password);                   // the user is stored as an instance to the class so the user's getUsername() can be called to place the username into a Label in the UI
        this.userID = user.getUserID();                                 // the user's UserID is saved as an instance so the userID can be retrieved to be used in inserting AuditHistory records with the userID inserted to show what user inserted itk
        password = null;                                         // made null to be garbage collected later
    }

    protected boolean checkHashFunction(String checkPassword, String checkHash) throws Exception {

        String sql = "SELECT HASHBYTES('SHA2_512', '" + checkPassword + "')";          // sql statement that gets the hash value that will be get for the password value from the password text field
        DataAccessObject dao = new DataAccessObject();
        dao.connect();
        ResultSet resultSet = dao.getHash(sql);            // the dao is called to retrieve the SHA2-512bit Hash value for the password being checked from the password field in login window

        String passwordHash = null;
        while(resultSet.next()) {
            passwordHash = resultSet.getString(1);              // password hash retrieved as String
        }
        dao.close();

        boolean answer = false;
        if(checkHash.equals(passwordHash)) {                   // if the hash value from the password field equals the hash value from the database, the method returns true so the user is able to log in
            answer = true;
        }

        checkPassword = null;
        sql = null;                                // both sql and checkPassword are made null so their String objects can be garbage collected in the login controllers

        return answer;
    }

    protected void updatePassword(String newPassword, String oldPassword, User user) throws Exception {
        DataAccessObject dao = new DataAccessObject();

        String hashSQL = "SELECT HASHBYTES('SHA2_512', '" + newPassword + "')";           // first the hash value is returned for the new password

        dao.connect();
        ResultSet resultSet = dao.getHash(hashSQL);

        String passwordHash = null;
        while(resultSet.next()) {
            passwordHash = resultSet.getString(1);
        }

        if(resultSet != null) {
            resultSet.close();
        }

        String updateSQL = "OPEN SYMMETRIC KEY UsersNameKey " +
                           "DECRYPTION BY CERTIFICATE KeyProtectionCertificate; " +
                           "UPDATE dbo.Users " +
                           "SET Password=EncryptByKey(Key_Guid('UsersNameKey'), '" + passwordHash + "') " +     // then the hash value is placed into the Users table for the new password value that also use the userID to determine what user to replace it for
                           "WHERE UserID=" + user.getUserID() + "; " +
                           "CLOSE SYMMETRIC KEY UsersNameKey;";
        dao.updatePassword(updateSQL);

        String serverSQL = "ALTER LOGIN " + user.getUsername() + " WITH PASSWORD = '" + newPassword + "' " +
                           "OLD_PASSWORD = '" + oldPassword + "';";                                                   // the server's login password for the user is changed to the newPassword that is entered un-hashed
        dao.updatePassword(serverSQL);

        newPassword = null;
        oldPassword = null;
        hashSQL = null;
        updateSQL = null;                        // all the Strings with passwords in them are made null so they can be garbage collected back in the Controller with System.gc()


        dao.close();
    }

    protected void closeFromMenuG() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exiting");
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            BusinessLayer bl = new BusinessLayer();
            bl.deleteFolderFiles(new File(path + "temp"));        // when close is called from the MenuItem, it deletes all the files in the "temp" folder and closes the Connection if it is not null already before closing the window
            bl.garbageCollectProperties();                      // the connectionURL is garbage collected every time that the application closes so that the Password cannoot be obtained in memory
            try {
                DataAccessObject dao = new DataAccessObject();
                dao.close();
            } catch (SQLException e1) {
            }
            Platform.exit();
        }
    }
    protected void deleteFolderFiles(File folder) {
        File[] files = folder.listFiles();          // listFiles() method used to return an array of files within "folder"
        if(files != null) {
            for (File f : files) {               // the files are cycled through in the folder if there are any in it
                if (f.isDirectory()) {
                    deleteFolderFiles(f);      // if one of the files in the folder is a folder, it is sent to a recursion step to traverse the entire folder to delete files and not delete any folders
                } else {
                    f.delete();            // if the file being cycled through is not a folder, it is deleted
                }
            }
        }
    }
    protected void garbageCollectProperties() {
        DataAccessObject dao = new DataAccessObject();
        dao.garbageCollectProperties();
    }

    protected void switchScene(ActionEvent event, String toFXML) throws IOException {
        String fixFXML = "/" + toFXML;                     // ensures the resources folder is property accessed with a / beforehand

        Node stageNode = (Node) event.getSource();                          // first the node is obtained from the specific event's node source
        Scene scene = stageNode.getScene();                                  // the scene the event was made in is gotten from the node
        Stage stage = (Stage) scene.getWindow();                                   // then the stage is gotten from the scene

        Parent root = FXMLLoader.load(getClass().getResource(fixFXML));         // the Parent for the form's fxml sheet is loaded

        Scene toScene = new Scene(root, scene.getWidth(), scene.getHeight());           // a new scene is created from the form's Parent and then its size is set from the current scene's width and height
        stage.setScene(toScene);
        stage.show();
    }
    protected void switchSceneWithDiffSize(ActionEvent event, String toFXML, int width, int height) throws IOException {
        String fixFXML = "/" + toFXML;

        Node stageNode = (Node) event.getSource();
        Scene scene = stageNode.getScene();
        Stage stage = (Stage) scene.getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fixFXML));
        Scene toScene = new Scene(root, width, height);                      // this method is used to switch a scene to a size that is different from the fromScene's sizes such as switching from the main table to the login screen that is a smaller height
        stage.setScene(toScene);
        stage.show();
    }

    protected void alertBox(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }

    protected String saveG(Connection conn, Employee employee) throws SQLException, IOException, ClassNotFoundException {             // used to save changes for adding an employee
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Changes");
        alert.setContentText("Are you sure you want to save these changes?\n\nFirst Name: " + employee.getFirstName() + "\nLast Name: " + employee.getLastName() + "\nPosition: " + employee.getTitle() +
                             "\nAddress: " + employee.getAddress() + "\nCity: " + employee.getCity() + "\nHome Phone: " + employee.getHomePhone() + "\nImage: " + employee.getPhoto());
        Optional<ButtonType> result = alert.showAndWait();
        String answer = null;
        if(result.get() == ButtonType.OK) {
            answer = "yes";
        } else {
            answer = "no";
        }
        DataAccessObject dao = new DataAccessObject();
        dao.commit(conn, answer);                                // the result of the dialog "yes" or "no" Confirmation box is sent to commit() method to either commit to the database for "yes" or rollback for "no"

        return answer;
    }
    protected String saveUpdateG(Connection conn, int column, String newValue) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Changes");
        String value = null;
        if(column == 1) {                        // depending on what column is being updated, a different String is used in the dialog to specify the commit to the database
            value = "Last Name";
        } if(column == 2) {
            value = "First Name";
        } if(column == 3) {
            value = "Title";
        } if(column == 4) {
            value = "Address";
        } if(column == 5) {
            value = "City";
        } if(column == 6) {
            value = "Home Phone";
        } if(column == 7) {
            value = "Photo's path";
        }
        alert.setContentText("Are you sure you want to update the " + value + " to " + newValue + "?");
        Optional<ButtonType> result = alert.showAndWait();
        String answer = null;
        if(result.get() == ButtonType.OK) {
            answer = "yes";
        } else {
            answer = "no";
        }
        DataAccessObject dao = new DataAccessObject();
        dao.commit(conn, answer);

        return answer;
    }
    protected String saveDeletionG(Connection conn, int size) throws SQLException {             // commit or rollback dialog used for when an Employee is deleted from the database
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Changes");
        alert.setContentText("Do you want to commit to deleting these records?\n\nTotal Rows Deleted: " + size);         // number of rows deleted will be shown in the alert box
        Optional<ButtonType> result = alert.showAndWait();
        String answer = null;
        if(result.get() == ButtonType.OK) {
            answer = "yes";
        } else {
            answer = "no";
        }
        DataAccessObject dao = new DataAccessObject();
        dao.commit(conn, answer);

        return answer;
    }
    protected void aboutG() throws IOException {
        HelpWindow helpWindow = new HelpWindow();
    }

    protected User getUserForLabel() {
        return user;
    }

    protected List<Employee> findEmployees(String searchText, int answer) throws Exception {
        BusinessLayer bl = new BusinessLayer();

        List<Employee> list = null;

        if (answer == 1) {    // if there are one or more characters in the text field, searchEmployees() is called with the text field input as the parameter
            list = bl.searchEmployeesG(searchText);                   // returns the results into the list

        } else if (answer ==2) {
            list = bl.getAllEmployeesG();             // if the text field input is empty or it is only spaces that can be trimmed to equal a length of 0, then getAllEmployees() is called
        }

        return list;         // the list is returned of either all the employees without the photo column or a specific search of employees with the photo column, in which answer will still be used to tell the table what columns to display
    }

    private List<Employee> searchEmployeesG(String searchText) throws Exception {
        List<Employee> list = new ArrayList<>();

        String search = "%" + searchText + "%";     // this will wrap lastName in placeholders %last% to show there could be more or after the string inputted

        String sql = "SELECT * FROM HumanResources.Employees " +
                     "WHERE LastName LIKE ? OR " +
                     "FirstName LIKE ?";
        ResultSet resultSet = null;
        DataAccessObject dao = new DataAccessObject();

        try {
            resultSet = dao.searchEmployees(search, sql);

            while (resultSet.next()) {
                Employee tempEmployee = convertRowToEmployee2(resultSet);    // this convert method will convert all the fields and Photo as well since it is a more narrowed down search that will not return 900 files
                list.add(tempEmployee);                                    // the resultSet is converted to an Employee object and added to the list in the method
            }
        } finally {
            if(resultSet != null) {
                resultSet.close();
            }
        }

        return list;
    }

    private List<Employee> getAllEmployeesG() throws Exception {
        List<Employee> list = new ArrayList<>();       // a list with type Employee object is created

        String sql = "SELECT * FROM HumanResources.Employees";

        DataAccessObject dao = new DataAccessObject();
        ResultSet resultSet = null;

        try {
            resultSet = dao.getAllEmployees(sql);

            while (resultSet.next()) {
                Employee tempEmployee = convertRowToEmployee(resultSet);      // the resultSet is converted to an Employee object and the objects are added to the list.. This method does not convert the photo because it would store too many files
                list.add(tempEmployee);
            }
        } finally {
            if(resultSet != null) {
                resultSet.close();
            }
        }

        return list;
    }

    private Employee convertRowToEmployee(ResultSet resultSet) throws SQLException, ClassNotFoundException, IOException {
        // All the values are taken from ResultSet row
        int id =  resultSet.getInt("EmployeeID");                                          // .getInt() gets from the result set as integer or it can be cast as an (int) from an object from .getObject()
        String lastName = (String) resultSet.getObject("LastName");                        // .getObject() can be used but it must always cast to the right type such as (String)
        String firstName = resultSet.getString("FirstName");
        String title = resultSet.getString("Title");
        String address = resultSet.getString("Address");
        String city = resultSet.getString("City");
        String phone = resultSet.getString("HomePhone");


        Employee employee = new Employee(id, lastName, firstName,
                title, address, city, phone);     // all the values taken from resultSet are placed into the Employees object
        return employee;
    }

    private Employee convertRowToEmployee2(ResultSet resultSet) throws SQLException, ClassNotFoundException, IOException {
        // All the values are taken from ResultSet row
        int id =  resultSet.getInt("EmployeeID");                                          // .getInt() gets from the result set as integer or it can be cast as an (int) from an object from .getObject()
        String lastName = (String) resultSet.getObject("LastName");                        // .getObject() can be used but it must always cast to the right type such as (String)
        String firstName = resultSet.getString("FirstName");
        String title = resultSet.getString("Title");
        String address = resultSet.getString("Address");
        String city = resultSet.getString("City");
        String phone = resultSet.getString("HomePhone");

        // Now for the Photo to be converted
        File file = new File(path + "temp\\image" + id + ".jpg");       // file created in temp directory, while will be cleaned after every photo is done being viewed
        FileOutputStream output = new FileOutputStream(file);               // output stream wrapped around file to be used to output to the file

        InputStream input = resultSet.getBinaryStream("Photo");        // converted from resultSet to InputStream with getBinaryStream()
        byte[] buffer = new byte[1024];
        while(input.read(buffer) > 0) {
            output.write(buffer);
        }

        File photo = file;         // the last value for photo is finally set

        Employee employee = new Employee(id, lastName, firstName,
                title, address, city, phone, photo);     // all the values taken from resultSet are placed into the Employees object
        return employee;
    }

    protected void displayTable(List<Employee> list, int answer, TableView table) throws SQLException, ClassNotFoundException, IOException {

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);           // sets the table so multiple rows can be selected
        table.setEditable(true);

        // First all the TableColumns are set up without initializing the TableView yet
        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");        // the employee id column is made
        idColumn.setMinWidth(35);
        idColumn.setMaxWidth(35);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("EmployeeID"));    // sets this column's values to the EmployeeID in the Employee object, in which all the values in the list will be outputted

        TableColumn lastNameColumn = new TableColumn("Last Name");                                            // this creates the column
        lastNameColumn.setPrefWidth(100);                                                                       // this sets the width the column will display at initially
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("LastName"));           // this populates the columns with with the "LastName" value of the Employee objects that will compose the ObservableList
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());                                    // this sets the cell factory to enable a TextField when clicked on
        lastNameColumn.setOnEditCommit(new EventHandler<CellEditEvent<Employee, String>>() {
            @Override
            public void handle(CellEditEvent<Employee, String> cellEvent) {
                ((Employee) cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setLastName(cellEvent.getNewValue());     // the LastName in the Employee object of the row is set to the new value from the cellEvent (text field change)
                try {
                    int column = cellEvent.getTablePosition().getColumn();                       // the column number is retrieved from 1-7, and 0 is the EmployeeID that is not editable
                    String newValue = cellEvent.getNewValue();                                   // the new value in the cell after the editable text field is changed
                    Employee employee = cellEvent.getRowValue();                                  // this gets all the new values in the row that has just been changed
                    updateEmployeeG(column, newValue, employee);                     // all these are placed into the updateEmployee method that manipulates the data for the DataAccessLayer
                } catch (SQLException e) {
                    e.printStackTrace();
                    alertBox("You do not have proper permissions to update the Employees table.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn firstNameColumn = new TableColumn("First Name");
        firstNameColumn.setPrefWidth(100);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("FirstName"));
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.setOnEditCommit(new EventHandler<CellEditEvent<Employee, String>>() {
            @Override
            public void handle(CellEditEvent<Employee, String> cellEvent) {
                ((Employee) cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setFirstName(cellEvent.getNewValue());
                try {
                    updateEmployeeG(cellEvent.getTablePosition().getColumn(), cellEvent.getNewValue(), cellEvent.getRowValue());
                } catch (SQLException e) {
                    e.printStackTrace();
                    alertBox("You do not have proper permissions to update the Employees table.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn titleColumn = new TableColumn("Title");
        titleColumn.setPrefWidth(150);
        titleColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("Title"));
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setOnEditCommit(new EventHandler<CellEditEvent<Employee, String>>() {
            @Override
            public void handle(CellEditEvent<Employee, String> cellEvent) {
                ((Employee) cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setTitle(cellEvent.getNewValue());
                try {
                    updateEmployeeG(cellEvent.getTablePosition().getColumn(), cellEvent.getNewValue(), cellEvent.getRowValue());
                } catch (SQLException e) {
                    e.printStackTrace();
                    alertBox("You do not have proper permissions to update the Employees table.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn addressColumn = new TableColumn("Address");
        addressColumn.setPrefWidth(150);
        addressColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("Address"));
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setOnEditCommit(new EventHandler<CellEditEvent<Employee, String>>() {
            @Override
            public void handle(CellEditEvent<Employee, String> cellEvent) {
                ((Employee) cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setAddress(cellEvent.getNewValue());
                try {
                    updateEmployeeG(cellEvent.getTablePosition().getColumn(), cellEvent.getNewValue(), cellEvent.getRowValue());
                } catch (SQLException e) {
                    e.printStackTrace();
                    alertBox("You do not have proper permissions to update the Employees table.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn cityColumn = new TableColumn("City");
        cityColumn.setPrefWidth(100);
        cityColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("City"));
        cityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cityColumn.setOnEditCommit(new EventHandler<CellEditEvent<Employee, String>>() {
            @Override
            public void handle(CellEditEvent<Employee, String> cellEvent) {
                ((Employee) cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setCity(cellEvent.getNewValue());
                try {
                    updateEmployeeG(cellEvent.getTablePosition().getColumn(), cellEvent.getNewValue(), cellEvent.getRowValue());
                } catch (SQLException e) {
                    e.printStackTrace();
                    alertBox("You do not have proper permissions to update the Employees table.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn phoneColumn = new TableColumn("Home Phone");
        phoneColumn.setPrefWidth(100);
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("HomePhone"));
        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setOnEditCommit(new EventHandler<CellEditEvent<Employee, String>>() {
            @Override
            public void handle(CellEditEvent<Employee, String> cellEvent) {
                (cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setHomePhone(cellEvent.getNewValue());
                try {
                    updateEmployeeG(cellEvent.getTablePosition().getColumn(), cellEvent.getNewValue(), cellEvent.getRowValue());
                } catch (SQLException e) {
                    e.printStackTrace();
                    alertBox("You do not have proper permissions to update the Employees table.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn fileColumn = new TableColumn("Photo");
        fileColumn.setPrefWidth(100);
        fileColumn.setCellValueFactory(new PropertyValueFactory<Employee, File>("Photo"));

        Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {          // Callback is used to create the same effect for each cell throughout the table
            public TableCell call(TableColumn tableCol) {
                TableCell cell = new TableCell<Employee, File>() {
                    @Override
                    public void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getFile().getPath());
                        setGraphic(null);
                    }
                    private File getFile() {
                        if(getItem() == null) {
                            return null;
                        } else {
                            File file = new File(getItem().toString());
                            return file;
                        }
                    }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() > 1) {                                        // if the cell is double clicked
                            TableCell c = (TableCell) event.getSource();                // gets the specific cell clicked and stores it as c

                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();       // the stage is found from the MouseEvent with getSource() cast to a Node that then uses getScene() for the Scene and getWindow() from the Scene for the stage

                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Open an Image");
                            File file = fileChooser.showOpenDialog(stage);             // a fileChooser is opened when the cell is double clicked
                            if(file == null) {}

                            String path = file.getPath();                             // the file returned from the fileChooser has its path taken to be set as the new display for the cell, while it's binary stream will be uploaded to the database
                            try {
                                int column = 7;                                    // column is set to 7 since it is the photo column

                                TableRow row = c.getTableRow();
                                Employee employee = (Employee) row.getItem();          // the TableCell gets the TableRow that gets the Item Object of the row and casts it as an Employee to be used in updateEmpoyeeG to get the EmployeeID of the row

                                updateEmployeeG(column, path, employee);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                alertBox("You do not have proper permissions to update the Employees table.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                return cell;
            }
        };
        fileColumn.setCellFactory(cellFactory);        // sets the cellFactory created above to the fileColumn to enable the handler

        table.getColumns().clear();      // clears any columns already in the table before adding anything

        ObservableList<Employee> observableList = FXCollections.observableArrayList(list);        // the Employee list passed in needs to be cast as an ObservableList<Employee> of type Employee because the TableView only accepts ObservableLists

        table.setItems(observableList);                                         // TableView object created and its Items are set to the observable list of the returned queried data
        if(answer == 1) {
            table.getColumns().addAll(idColumn, lastNameColumn, firstNameColumn,
                    titleColumn, addressColumn, cityColumn, phoneColumn, fileColumn);      // if the answer is 1 (searchEmployees() called) then all columns added
        }
        if(answer == 2) {
            table.getColumns().addAll(idColumn, lastNameColumn, firstNameColumn,
                    titleColumn, addressColumn, cityColumn, phoneColumn);            // if the answer is 2 (getAllEmployees() called) then all columns are added except for Photo
        }
    }

    private String updateEmployeeG(int column, String newValue, Employee employee) throws Exception {
        String sql = null;
        if(column == 1) {
            sql = "UPDATE HumanResources.Employees " +          // depending on what column was changed, a different sql statement will be used so only one column needs to be updated in the database
                  "SET LastName=? " +
                  "WHERE EmployeeID=?";
        } if(column == 2) {
            sql = "UPDATE HumanResources.Employees " +
                  "SET FirstName=? " +
                  "WHERE EmployeeID=?";
        } if(column == 3) {
            sql = "UPDATE HumanResources.Employees " +
                  "SET Title=? " +
                  "WHERE EmployeeID=?";
        } if(column == 4) {
            sql = "UPDATE HumanResources.Employees " +
                  "SET Address=? " +
                  "WHERE EmployeeID=?";
        } if (column == 5) {
            sql = "UPDATE HumanResources.Employees " +
                  "SET City=? " +
                  "WHERE EmployeeID=?";
        } if(column == 6) {
            sql = "UPDATE HumanResources.Employees " +
                  "SET HomePhone=? " +
                  "WHERE EmployeeID=?";
        } if(column == 7) {
            sql = "UPDATE HumanResources.Employees " +
                  "SET Photo=? " +
                  "WHERE EmployeeID=?";
        }
        int id = employee.getEmployeeID();                          // the employeeID is taken from the Employee object taken from cellEvent.getRow()

        String auditSql = "INSERT INTO dbo.AuditHistory (UserID, EmployeeID, Action, ActionDateTime) " +
                           "VALUES(?, ?, ?, ?)";                          // this is the sql statement for inserting the update action into the AuditHistory table

        DataAccessObject dao = new DataAccessObject();

        String answer = dao.updateEmployee(sql, newValue, id, column, auditSql, userID);                   // the DataAccess layer is called with the correct sql statement, the specified id, and the new value all going in.. along with the column, which will be used to separate the photo column

        return answer;                                                    // the answer from the commit alert box is returned back to the cell so the table does not update by itself if the answer is "no"
    }

    protected void addEmployeeG(Employee employee, ActionEvent event) throws Exception {
        String sql = "INSERT INTO HumanResources.Employees " +
                     "(LastName, FirstName, Title, Address, City, HomePhone, Photo) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?)";

        String queryForAddedEmployeeID = "SELECT TOP 1 EmployeeID " +
                                         "FROM HumanResources.Employees " +        // this is query is used to get the EmployeeID for the row that is being inserted with the "sql" INSERT statement.. this is so the right EmployeeID is inserted into AuditHistory
                                         "ORDER BY EmployeeID DESC";

        String auditSql = "INSERT INTO dbo.AuditHistory (UserID, EmployeeID, Action, ActionDateTime) " +
                           "VALUES(?, ?, ?, ?)";                                               // this is the INSERT statement for inserting the Employee insert data into the AuditHisotry table

        DataAccessObject dao = new DataAccessObject();

        String answer = dao.addEmployee(employee, sql, queryForAddedEmployeeID, auditSql, userID);                                         // addEmployee is called in the DataAccessObject and it gives it the Employee object created that it will use to insert all the values
                                                                                                        // the userID current instance in the class for whoever is logged in is inserted as the userID
        if(answer.equals("yes")) {
            switchScene(event, "Interface.fxml");
        }
    }

    protected void deleteEmployeeG(ObservableList<Employee> selectedEmployees, TableView table) throws Exception {
        List<Employee> list = new ArrayList<>(selectedEmployees);

        String sql = "DELETE FROM dbo.AuditHistory " +
                     "WHERE EmployeeID=? " +                      // to make a successful deletion that does not conflict between the foreign keys is to first delete all the rows with the EmployeeID and then the Employee row in the Employees table
                     "DELETE FROM HumanResources.Employees " +
                     "WHERE EmployeeID=?;";

        DataAccessObject dao = new DataAccessObject();
        Connection conn = null;
        dao.connect();

        for (int i = 0; i < selectedEmployees.size(); i++) {                 // this cycles through all the items in the selectedEmployees list and removes them from the database one by one using the EmployeeID
            Employee employee = list.get(i);
            int employeeID = employee.getEmployeeID();


            conn = dao.deleteFromDatabase(sql, employeeID);

        }

        BusinessLayer bl = new BusinessLayer();
        String answer = bl.saveDeletionG(conn, selectedEmployees.size());            // asks the user if they want to save their changes and commit or rollback the the DELETE statement

        if(answer.equals("yes")) {
            table.getItems().removeAll(selectedEmployees);                       // if the user decides to commit changes, the items are also removed from the table.. This statement only removes them from the table display
        }

        dao.close();
    }

    protected void createAuditTable(Employee employee) throws Exception {
        String sql = "OPEN SYMMETRIC KEY UsersNameKey DECRYPTION BY CERTIFICATE KeyProtectionCertificate; " +
                     "SELECT Username, HASHBYTES('SHA2_512', CAST(DecryptByKey(Password) as varchar(50))), " +
                            "CAST(DecryptByKey(LastName) as varchar(50)), " +
                            "CAST(DecryptByKey(FirstName) as varchar(50)), AuditID, EmployeeID, Action, ActionDateTime " +
                     "FROM dbo.AuditHistory AS ah " +
                     "INNER JOIN dbo.Users AS u " +
                     "ON ah.UserID = u.UserID " +
                     "WHERE EmployeeID=?; " +
                     "CLOSE SYMMETRIC KEY UsersNameKey;";

        int employeeID = employee.getEmployeeID();           // employeeID used in the WHERE clause to get the right rows in AuditHistory

        List<AuditHistory> auditList = new ArrayList<>();

        DataAccessObject dao = new DataAccessObject();
        ResultSet resultSet = dao.getAuditHistory(sql, employeeID);          // dbo.AuditHistory table queried with the sql statement above, and the employeeID to find all the rows in the ResultSet that have the id

        while(resultSet.next()) {
            AuditHistory auditHistory = convertRowToAuditHistory(resultSet);
            auditList.add(auditHistory);                                         // the resultSet is converted into AuditHistory objects and the objects placed in a list
        }

        // NEXT the new Window is opened and the table is created in it
        AuditHistoryTable auditHistoryTable = new AuditHistoryTable(employee);
        auditHistoryTable.addTableData(auditList);
    }

    private AuditHistory convertRowToAuditHistory(ResultSet resultSet) throws SQLException {         // converts the ResultSet from getting the AuditHistory data to an AuditHistory object so it can be placed in a table
        String password = resultSet.getString(2);
        String userName = resultSet.getString(3) + " " + resultSet.getString(4);
        int auditID = resultSet.getInt(5);
        int employeeID = resultSet.getInt(6);
        String action = resultSet.getString(7);
        Timestamp actionDateTimestamp = resultSet.getTimestamp(8);
        String actionDateTime = actionDateTimestamp + "";

        AuditHistory auditHistory = new AuditHistory(auditID, employeeID, action, actionDateTime, userName, password);      // instantiates the AuditHistory object with values from the query

        return auditHistory;
    }

    protected void updateAuditG(int column, String newValue, AuditHistory auditHistory) throws Exception {

        int auditID = auditHistory.getAuditID();

        String sql = "UPDATE dbo.AuditHistory " +
                     "SET Action=? " +
                     "WHERE AuditID=?";

        DataAccessObject dao = new DataAccessObject();
        dao.updateAuditHistory(sql, newValue, auditID);          // newValue is passed in for Action col

    }
    protected void deleteAuditG(AuditHistory auditHistory, TableView table) throws Exception {
        String sql = "DELETE FROM dbo.AuditHistory " +
                     "WHERE AuditID=?;";

        int auditID = auditHistory.getAuditID();

        DataAccessObject dao = new DataAccessObject();
        String answer = dao.deleteAuditHistory(sql, auditID);

        if(answer.equals("yes")) {
            table.getItems().remove(auditHistory);
        }
    }
    protected List<User> getUsersG() throws Exception {
        List<User> list = new ArrayList<>();

        String sql = "OPEN SYMMETRIC KEY UsersNameKey " +
                     "DECRYPTION BY CERTIFICATE KeyProtectionCertificate; " +
                     "SELECT UserID, Username, CAST(DecryptByKey(Password) as varchar(MAX)), " +
                                               "CAST(DecryptByKey(LastName) as varchar(MAX)), CAST(DecryptByKey(FirstName) as varchar(MAX)) " +
                     "FROM dbo.Users; " +
                     "CLOSE SYMMETRIC KEY UsersNameKey;";

        DataAccessObject dao = new DataAccessObject();
        ResultSet resultSet = dao.getLoginUsers(sql);

        while(resultSet.next()) {
            int userID = resultSet.getInt(1);
            String username = resultSet.getString(2);
            String password = resultSet.getString(3);
            String lastName = resultSet.getString(4);
            String firstName = resultSet.getString(5);

            User user = new User(userID, username, password, lastName, firstName);
            list.add(user);
        }

        if(resultSet != null) {
            resultSet.close();
        }
        dao.close();

        return list;
    }

    protected User getCurrentUser() {
        return user;                       // used to get the current User that is signed in.. which is returned to the ChangePasswordController
    }

    protected void setPathVariable(String path) {
        this.path = path;                          // sets the path static field for the BusinessLayer so path can be used in all methods
    }
}
