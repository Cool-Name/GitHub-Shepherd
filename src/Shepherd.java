import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class Shepherd extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
       
    	//sets the stage to use our fxml layout
    	Parent root = FXMLLoader.load(getClass().getResource("Shepherd.fxml"));
       
    	//sets default window size
    	Scene scene = new Scene(root, 1800, 800);
       
       //sets icon
       stage.getIcons().add(new Image(getClass().getResourceAsStream("images/logo.png"))); 
       
       //sets title
       stage.setTitle("Github Shepherd");
       
       //shows the stage
       stage.setScene(scene);
       stage.show();
        
    }
       
    
    public static void listRepos()
    {
    	FileWalker walker = new FileWalker();
    	walker
		.searchDirectory(new File("C:/Users/Ben Templeton/Documents"), ".git");
    }
}