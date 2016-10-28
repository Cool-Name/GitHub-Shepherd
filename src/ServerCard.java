
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Sample custom control hosting a text field and a button.
 */
public class ServerCard extends VBox {
	private static final int PORT = 65051;

	@FXML
	private Label textIP;
	@FXML
	private Label textEnvironment;
	@FXML
	private Label textStatus;
	@FXML
	private Button btnServer;
	@FXML
	private ImageView image;

	String ip;

	private Socket sock;
	private DataOutputStream out;

	public ServerCard(String ip, String env, String stat, Image img) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"ServerCard.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			this.ip = ip;

			fxmlLoader.load();

			// adjust Card info here
			textIP.setText(ip);
			textEnvironment.setText(env);
			textStatus.setText(stat);
			image.setImage(img);

		} catch (Exception e) {
			e.printStackTrace();

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR: Bad IP Address");
			alert.setHeaderText("IP address was unreachable or invalid");
			alert.setContentText("Please check IP address then try again");

			alert.showAndWait();
		}
	}

	@FXML
	protected void serverClick() {
		try {
			// set button action here
			System.out.println(textIP.getText() + " was clicked!");

			// Sends a list of repo urls to pull to the server
			sock = new Socket();
			sock.connect(new InetSocketAddress(ip, PORT), 3000);
			out = new DataOutputStream(sock.getOutputStream());

			StringBuilder repoString = new StringBuilder();

			ObservableList<Row> repos = MyController.getRepos();
			for (Row repoRow : repos) {
				if (repoRow.getEnabled()) {
					try {
						Git g = new Git(new FileRepository(
								repoRow.getRepositories()));
						String url = repoRow.getUrl();
						repoString.append(url + "&&");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			out.writeBytes(repoString.toString() + '\n');
			System.out.println(repoString.toString());
			sock.close();

		} catch (Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR: Connection Problem");
			alert.setHeaderText("IP address was unreachable or invalid or Listener is not running");
			alert.setContentText("Please check IP address then try again and make sure you are running the listener");

			alert.showAndWait();
		}
	}
}
