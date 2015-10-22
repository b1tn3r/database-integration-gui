package Database_Integration;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class PhotoWindow extends Stage {
    public ImageView imageView;
    public MenuItem closeItem;
    public Controller mainController;
    public Stage subStage;
    public Image image;
    public File file;
    public BorderPane borderPane;
    public ImageViewPane viewPane;

    public PhotoWindow(Controller mainController, File file) throws IOException {

        this.mainController = mainController;               // a new window is opened with the Controller as the parentController, which allows for this window to send information back and communicate with the Interface.fxml window
        subStage = new Stage();
        subStage.setX(100);
        subStage.setY(200);

        this.file = file;
        InputStream is = new FileInputStream(file);             // to properly display the file that was just loaded into the project, the file needs to be converted to an InputStream and placed in the Image
        final Image image = new Image(is);                                                //  A URL to an image on the Internet could also be placed in it such as-- final Image image = new Image("https://fb.com/image.jpg");
        borderPane = new BorderPane();                              // the file is loaded directly out of the temp folder
        imageView = new ImageView();                       // image is set in the window using ImageView
        imageView.setImage(image);

        viewPane = new ImageViewPane(imageView);        // places the ImageView in the inner class created ImageViewPane that places the ImageView in a ObjectProperty to resize on window resize
        borderPane.setCenter(viewPane);

        Menu fileMenu = new Menu("_File");
        MenuItem openFile = new MenuItem("Open...");

        openFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"));
            File fileChosen = fileChooser.showOpenDialog(subStage);
            if(file != null) {
                try {
                    InputStream input = new FileInputStream(fileChosen);                   // the file opened in fileChooser is wrapped in an InputStream so it can be outputted to the image
                    Image image2 = new Image(input);
                    imageView.setImage(image2);
                } catch(FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        fileMenu.getItems().add(openFile);
        MenuItem saveFile = new MenuItem("Save...");
        fileMenu.getItems().add(saveFile);
        saveFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("image.jpg");                 // the initial name placed in the fileChooser along with the initial directory it starts at
            fileChooser.setInitialDirectory(new File("C://"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("All Images", "*.*"), new FileChooser.ExtensionFilter("PNG", "*.png"));     // adds all the filters into the fileChooser that can be chosen with the ComboBox in it
            fileChooser.setTitle("Save Image");
            File file2 = fileChooser.showSaveDialog(subStage);
            try {
                if (file2 != null) {
                    FileOutputStream output = new FileOutputStream(file2);
                    InputStream input = new FileInputStream(file);

                    byte[] buffer = new byte[1024];           // a buffer array is set up with a type bytes, in which the array is 1024 bytes large
                    while (input.read(buffer) > 0) {
                        output.write(buffer);           // input2 is read into the buffer until it is at 0 bytes, and the buffer then writes to output until the transfer leaves 0 bytes in input2
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        MenuItem closeFile = new MenuItem("Close All");
        fileMenu.getItems().add(closeFile);
        BusinessLayer bl = new BusinessLayer();
        closeFile.setOnAction(e -> bl.closeFromMenuG());

        MenuBar menuBar = new MenuBar();      // menuBar will hold the Menus created (File)
        menuBar.getMenus().add(fileMenu);
        borderPane.setTop(menuBar);

        Scene scene = new Scene(borderPane, image.getWidth(), image.getHeight());
        subStage.setScene(scene);
        subStage.show();
    }

    private class ImageViewPane extends Region {                                                      // the ImageViewPane will extend Region's ability to grow and shrink in a specified area

        private ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<ImageView>();        // the SimpleObjectProperty in the class holds an ImageView

        public ImageViewPane() {
            this(new ImageView());            // the constructor for the class initializes and creates the ImageView object
        }

        public ObjectProperty<ImageView> imageViewProperty() {
            return imageViewProperty;                                     // method that returns the imageViewProperty
        }

        public ImageView getImageView() {
            return imageViewProperty.get();                          // method that returns the ImageView listed within the property
        }

        public void setImageView(ImageView imageView) {
            this.imageViewProperty.set(imageView);                     // method that sets an ImageView to the ObjectProperty
        }

        @Override
        protected void layoutChildren() {
            ImageView imageView = imageViewProperty.get();                // layoutChildren is an implemented method from region that sets the ImageView's size in the ObjectProperty
            if(imageView != null) {
                imageView.setFitWidth(getWidth());
                imageView.setFitHeight(getHeight());
                layoutInArea(imageView, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
            }
            super.layoutChildren();
        }
        public ImageViewPane(ImageView imageView) {
            imageViewProperty.addListener(new ChangeListener<ImageView>() {
                @Override
                public void changed(ObservableValue<? extends ImageView> arg0, ImageView oldIV, ImageView newIV) {        // a changeListener readjusts the class's child, ImageView, to auto resize every time the size of the ImageView is changed, and because the ImageView and Pane are fit to the surrounding borderPane, they resize with its width and height
                    if(oldIV != null) {
                        getChildren().remove(oldIV);
                    }
                    if(newIV != null) {
                        getChildren().add(newIV);                      // the old value size of the ImageView is removed from the ObjectProperty field and the new value of the size is added to the Object Property
                    }
                }

            });
            this.imageViewProperty.set(imageView);                    // the adjusted ObjectProperty is now set with the ImageView to completely resize it if the new and old values are not null
        }
    }
}
