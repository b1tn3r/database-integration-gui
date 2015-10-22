package Database_Integration;
// This is the Window that opens when View History of Employee is clicked and opens a new window that displays
// the audit history of the employee selected

import com.microsoft.sqlserver.jdbc.SQLServerException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class AuditHistoryTable {
    public Stage stage;
    public Scene scene;
    public TableView table;
    public HBox hBox;
    public BorderPane borderPane;
    public Label label1;
    public Label label2;
    public Button deleteButton;
    public Region region;

    public AuditHistoryTable(Employee employee) throws IOException {
        stage = new Stage();
        stage.setX(50);
        stage.setY(200);
        stage.setTitle("Audit History");

        String employeeName = employee.getFirstName() + " " + employee.getLastName();

        this.table = new TableView();

        Label label1 = new Label();
        label1.setText("Audit History for Employee:  ");
        label1.setFont(new Font("System", 14));

        Label label2 = new Label();
        label2.setText(employeeName);
        label2.setFont(new Font("System", 14));

        Region region = new Region();
        region.setMinWidth(Region.USE_COMPUTED_SIZE);
        region.setMinHeight(Region.USE_COMPUTED_SIZE);
        region.setPrefHeight(Region.USE_COMPUTED_SIZE);
        region.setPrefWidth(Region.USE_COMPUTED_SIZE);
        region.setMaxHeight(Region.USE_COMPUTED_SIZE);
        region.setMaxWidth(Region.USE_COMPUTED_SIZE);

        Button deleteButton = new Button();
        deleteButton.setText("Delete Record");
        deleteButton.setFont(new Font("System", 13));
        deleteButton.setOnAction(e -> {
            BusinessLayer bl = new BusinessLayer();
            AuditHistory auditHistory = (AuditHistory) table.getSelectionModel().getSelectedItem();
            try {
                bl.deleteAuditG(auditHistory, table);
            } catch (SQLException e1) {
                bl.alertBox("You do not have the proper permissions to delete audit history for an employee.");
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(label1, label2, region, deleteButton);
        hBox.setPadding(new Insets(13, 13, 13, 13));
        hBox.setMinWidth(Region.USE_COMPUTED_SIZE);
        hBox.setMinHeight(Region.USE_COMPUTED_SIZE);
        hBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        hBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        hBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
        hBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
        hBox.setHgrow(region, Priority.ALWAYS);

        this.table = new TableView();
        table.setMinWidth(Region.USE_COMPUTED_SIZE);
        table.setMinHeight(Region.USE_COMPUTED_SIZE);
        table.setPrefHeight(Region.USE_COMPUTED_SIZE);
        table.setPrefWidth(Region.USE_COMPUTED_SIZE);
        table.setMaxHeight(Region.USE_COMPUTED_SIZE);
        table.setMaxWidth(Region.USE_COMPUTED_SIZE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(table);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMinWidth(Region.USE_COMPUTED_SIZE);
        scrollPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        scrollPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scrollPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        scrollPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        scrollPane.setMaxWidth(Region.USE_COMPUTED_SIZE);


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBox);
        borderPane.setCenter(scrollPane);
        borderPane.setMinWidth(Region.USE_COMPUTED_SIZE);
        borderPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        borderPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        borderPane.setMaxWidth(Region.USE_COMPUTED_SIZE);


        scene = new Scene(borderPane, 610, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void addTableData(List<AuditHistory> auditList) {

        table.setEditable(true);

        TableColumn<AuditHistory, String> actionDateColumn = new TableColumn<>("Date/Time Occurred");                           // The ActionDateTime column will be updateable
        actionDateColumn.setPrefWidth(50);
        actionDateColumn.setCellValueFactory(new PropertyValueFactory<>("ActionDateTime"));


        TableColumn actionColumn = new TableColumn("Action");                              // Action column made to be updated as well
        actionColumn.setMinWidth(100);
        actionColumn.setCellValueFactory(new PropertyValueFactory<AuditHistory, String>("Action"));
        actionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        actionColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AuditHistory, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<AuditHistory, String> cellEvent) {
                ((AuditHistory) cellEvent.getTableView().getItems().get(cellEvent.getTablePosition().getRow())).setActionDateTime(cellEvent.getNewValue());
                try {
                    int column = cellEvent.getTablePosition().getColumn();
                    String newValue = cellEvent.getNewValue();
                    AuditHistory auditHistory = cellEvent.getRowValue();

                    BusinessLayer bl = new BusinessLayer();
                    bl.updateAuditG(column, newValue, auditHistory);
                } catch (SQLServerException e) {
                    BusinessLayer bl = new BusinessLayer();
                    bl.alertBox("You do not have the proper permissions to update audit history.");
                } catch(SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        TableColumn<AuditHistory, String> userNameColumn = new TableColumn<>("Edited By");
        userNameColumn.setPrefWidth(30);
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));

        TableColumn<AuditHistory, String> passwordColumn = new TableColumn<>("Encrypted Hashing Password");
        passwordColumn.setPrefWidth(40);
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("Password"));


        table.getColumns().clear();      // clears any columns already in the table before adding anything

        ObservableList<AuditHistory> observableList = FXCollections.observableArrayList(auditList);        // AuditHistory List is converted to ObservabeList for TableView

        table.setItems(observableList);                                         // TableView object created and its Items are set to the observable list of the returned queried data

        table.getColumns().addAll(actionDateColumn, actionColumn, userNameColumn, passwordColumn);            // columns populate the table
    }

    public static void main(String[] args) {

    }

}
