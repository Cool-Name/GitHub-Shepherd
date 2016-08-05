import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Shepherd extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("Shepherd.fxml"));
    
        Scene scene = new Scene(root, 1800, 800);
    
    stage.getIcons().add(new Image("file:images/logo.png"));
        stage.setTitle("Github Shepherd");
        stage.setScene(scene);
        stage.show();
    }
}