import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class MyController implements Initializable {

	// declares fxml controls using their id
	@FXML
	Button listRepoBtn;
	@FXML
	Button addServer;
	@FXML
	TextField serverTextField;
	@FXML
	TreeView<String> serverList;
	@FXML
	TableView<Row> data_table;
	@FXML
	TableColumn<Row, String> enabled;
	@FXML
	TableColumn<Row, String> repositories;
	@FXML
	TableColumn<Row, String> current_version;
	@FXML
	TableColumn<Row, String> latest_version;
	@FXML
	TableColumn<Row, String> last_pulled;
	@FXML
	TableColumn<Row, Hyperlink> hash;
	@FXML
	TableColumn<Row, String> description;

	final String dateFormat = "dd/MM/yyyy hh:mm a";

	TreeItem<String> root;

	ObservableList<Row> observableList = FXCollections.observableArrayList();

	public void buildList() {
		observableList.clear();
		for (Git g : GitRepoBuilder.getrepositoryGits()) {
			try {
				System.out.println(g.getRepository().getRepositoryState().toString());
				Config conf = g.getRepository().getConfig();
				String url = conf.getString("remote", "origin", "url");
				List<Ref> branchesList = BranchHandler.getBranches(g);
				List<String> branchNames = new ArrayList<String>();
				String hashString = (g.getRepository().resolve("HEAD") == null) ? ""
						: "" + g.getRepository().resolve("HEAD").abbreviate(7).name();

				String description = g.getRepository().readCommitEditMsg();
				String authorDate;

				try {
					RevWalk revWalk = new RevWalk(g.getRepository());
					revWalk.markStart(revWalk.parseCommit(g.getRepository().resolve(Constants.HEAD)));
					RevCommit commit = revWalk.next();
					revWalk.close();
					
					//author
					PersonIdent authorIdent = commit.getAuthorIdent();
					//committer
					PersonIdent committerIdent = commit.getCommitterIdent();
					//date time
					authorDate = modifyDateLayout(committerIdent.getWhen().toString());
					
				} catch (Exception e) {
					authorDate = "";
				}

				for (Ref r : branchesList) {
					branchNames.add(r.getName());
				}

				observableList.add(new Row("✓", g.getRepository().getDirectory().getAbsolutePath(), "v1.0", "v1.3",
						authorDate, branchNames, hashString, description, g));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String modifyDateLayout(String inputDate) throws ParseException {
		Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(inputDate);
		return new SimpleDateFormat(dateFormat).format(date);
	}

	@FXML
	private void ListRepos() {
		buildList();

		enabled.setCellValueFactory(new PropertyValueFactory<Row, String>("enabled"));
		repositories.setCellValueFactory(new PropertyValueFactory<Row, String>("repositories"));
		current_version.setCellValueFactory(new PropertyValueFactory<Row, String>("current_version"));
		latest_version.setCellValueFactory(new PropertyValueFactory<Row, String>("latest_version"));
		last_pulled.setCellValueFactory(new PropertyValueFactory<Row, String>("last_pulled"));

		// allows sorting by date
		last_pulled.setComparator(new Comparator<String>() {
			@Override
			public int compare(String t, String t1) {
				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				Date d1;
				Date d2;
				try {
					d1 = format.parse(t);
				} catch (Exception e) {
					d1 = new Date(Long.MIN_VALUE);
				}

				try {
					d2 = format.parse(t1);
				} catch (Exception e) {
					d2 = new Date(Long.MIN_VALUE);
				}

				return Long.compare(d1.getTime(), d2.getTime());
			}
		});

		hash.setCellValueFactory(new PropertyValueFactory<Row, Hyperlink>("hash"));
		description.setCellValueFactory(new PropertyValueFactory<Row, String>("description"));

		data_table.setItems(observableList);
	}

	@FXML
	private void ShowContextMenu() {

	}

	@FXML
	private void pullAllRepos() {
		Puller p = new Puller();
		p.pullAll();
		ListRepos();
	}

	@FXML
	private void pullSingleRepo() {
		Row temp = data_table.getSelectionModel().getSelectedItem();
		if (temp == null)
			return;
		Puller.pullSingle(temp.getGit());
	}

	@FXML
	private void AddServer() {
		// checks if the search box is empty
		if (serverTextField.getText() != null && !serverTextField.getText().isEmpty()) {
			// adds the server to the server list
			TreeItem<String> item = new TreeItem<>(serverTextField.getText());
			root.getChildren().add(item);
			serverList.setRoot(root);

			// clears the text from the server textField
			serverTextField.clear();
		}
	}

	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		// adds the root item servers
		root = new TreeItem<String>("Servers");
		root.setExpanded(true);
		serverList.setRoot(root);
	}
}