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
    
   private final ObservableList<String> repoList = FXCollections.observableArrayList(
    		new String("A"),
    		new String("B"),
    		new String("C")
    		); 
    
    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("Shepherd.fxml"));
       
       //FXMLLoader loader = new FXMLLoader(getClass().getResource("Shepherd.fxml"));
       //loader.setRoot(FXMLLoader.load(getClass().getResource("Shepherd.fxml")));
       //loader.setController(new MyController());
       MyController mc = new MyController();
       
        Scene scene = new Scene(root, 1800, 800);
    
      
        
    
    stage.getIcons().add(new Image("file:images/logo.png"));
        stage.setTitle("Github Shepherd");
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