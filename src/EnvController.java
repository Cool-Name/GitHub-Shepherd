import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class EnvController implements Initializable {

	@FXML
	private TextArea EnvArea;

	@FXML
	private Button BuildButton;

	@FXML
	private Button CancelButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

	// Close the form
	@FXML
	private void close() {
		Stage stage = (Stage) EnvArea.getScene().getWindow();
		stage.close();
	}

	// Build environment from the provided text
	@FXML
	private void build() {
		String textIn = EnvArea.getText();

		String[] lines = textIn.split("\n");
		if (lines.length < 3)
			return;
		String title = lines[0];

		File out = new File("Env/" + title + ".txt");
		if (!out.exists())
		{
			try {
				out.getParentFile().mkdirs();
				out.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
		}
		try {
			PrintWriter writer = new PrintWriter(out);

			for (int i = 0; i < lines.length; i++) {
				writer.println(lines[i]);
			}
			writer.close();
			close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
