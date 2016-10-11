

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Sample custom control hosting a text field and a button.
 */
public class ServerCard extends VBox {
	@FXML private Label textIP;
	@FXML private Label textEnvironment;
	@FXML private Label textStatus;
	@FXML private Button btnServer;
	@FXML private ImageView image;

    public ServerCard(String ip, String env, String stat, Image img) {
       	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            
            //adjust Card info here
          	textIP.setText(ip);
          	textEnvironment.setText(env);
          	textStatus.setText(stat);
          	image.setImage(img);
          	
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
        
    @FXML
    protected void serverClick() {
    	//set button action here
        System.out.println(textIP.getText() + " was clicked!");
    }
}
