import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Shepherd extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// sets the stage to use our fxml layout
		Parent root = FXMLLoader.load(getClass().getResource("Shepherd.fxml"));

		// sets default window size
		Scene scene = new Scene(root, 1800, 800);

		// sets icon
		stage.getIcons().add(new Image(getClass().getResourceAsStream("images/logo.png")));

		// sets title
		stage.setTitle("Git Shepherd");

		// shows the stage
		stage.setScene(scene);
		stage.show();

		// Get current screen of the stage
		ObservableList<Screen> screens = Screen.getScreensForRectangle(
				new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()));

		// Sets relative screen size and center position
		Rectangle2D bounds = screens.get(0).getVisualBounds();
		stage.setWidth(bounds.getWidth() * 0.9);
		stage.setHeight(bounds.getHeight() * 0.95);
		stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((bounds.getHeight() - stage.getHeight()) / 2);

	}
}