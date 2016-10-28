import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


// Used to build an environment file
public class EnvBuilder extends Application {
	 
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// sets the stage to use our fxml layout
		Parent root = FXMLLoader.load(getClass().getResource("EnvBuilder.fxml"));

		// sets default window size
		Scene scene = new Scene(root);

		// sets icon
		stage.getIcons().add(new Image(getClass().getResourceAsStream("images/logo.png")));

		// sets title
		stage.setTitle("Environment Builder");

		// shows the stage
		stage.setScene(scene);
		stage.show();
	}
}