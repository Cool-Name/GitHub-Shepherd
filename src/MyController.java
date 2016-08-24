import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class MyController implements Initializable
{
	//declares fxml controls using their id
	@FXML Button listRepoBtn;
	@FXML Button addServer;
	@FXML TextField serverTextField;
	@FXML TreeView<String> serverList;
	@FXML TableView<Row> data_table;
	@FXML TableColumn<Row, String> enabled;
	@FXML TableColumn<Row, String> repositories;
	@FXML TableColumn<Row, String> current_version;
	@FXML TableColumn<Row, String> latest_version;
	@FXML TableColumn<Row, String> last_pulled;
	@FXML TableColumn<Row, String> description;
	
	
	TreeItem<String> root;
	
	private final ObservableList<String> repoList = FXCollections.observableArrayList(
    		new String("A"),
    		new String("B"),
    		new String("C")
    		); 
	 
		ObservableList<Row> observableList = FXCollections.observableArrayList(          
				new Row("yes", "Test Repo","4.1","5.0", "5:00pm 10/10/2016", "This is a sample description"),
				new Row("yes", "Test Repo","4.1","5.0", "5:00pm 10/10/2016", "This is a sample description"),
				new Row("yes", "Test Repo","4.1","5.0", "5:00pm 10/10/2016", "This is a sample description")
	           );
		
	 
		
		            	
	@FXML 
	private void ListRepos()
	{
		enabled.setCellValueFactory(
		        new PropertyValueFactory<Row,String>("enabled"));
		repositories.setCellValueFactory(
		        new PropertyValueFactory<Row,String>("repositories"));
		current_version.setCellValueFactory(
		        new PropertyValueFactory<Row,String>("current_version"));
		latest_version.setCellValueFactory(
		        new PropertyValueFactory<Row,String>("latest_version"));
		last_pulled.setCellValueFactory(
		        new PropertyValueFactory<Row,String>("last_pulled"));
		description.setCellValueFactory(
		        new PropertyValueFactory<Row,String>("description"));

		
		data_table.setItems(observableList);
	
	}
	
	
	@FXML 
	private void ShowContextMenu()
	{
				
	}
	
	@FXML 
	private void AddServer()
	{
		 //checks if the search box is empty 	
		 if (serverTextField.getText() != null && !serverTextField.getText().isEmpty())
		 {
				//adds the server to the server list
			 	TreeItem<String> item = new TreeItem<>(serverTextField.getText());
			    root.getChildren().add(item);
		   	    serverList.setRoot(root);
		   	    
		   	    //clears the text from the server textField
		   	    serverTextField.clear();
		 }
	}
	
	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources)
	{
		//adds the root item servers
		root = new TreeItem<String>("Servers");
		root.setExpanded(true);
		serverList.setRoot(root);    
	}
}