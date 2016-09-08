import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

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
	TableColumn<Row, Boolean> enabled;
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
	@FXML
	MenuItem fileChangeDirectory;
	@FXML
	ProgressIndicator progressCircle;
	@FXML
	StackPane TableStackPane;

	// sets format for last modified column
	final String dateFormat = "dd/MM/yyyy hh:mm a";

	TreeItem<String> root;
	ObservableList<Row> observableList = FXCollections.observableArrayList();

	// this code is run on startup
	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		// adds the root item servers
		root = new TreeItem<String>("Servers");
		root.setExpanded(true);
		serverList.setRoot(root);

		CheckBox selectAll = new CheckBox();
		// Use box as column header
		enabled.setGraphic(selectAll);
		selectAll.setSelected(true);
		// Select all checkboxes
		selectAll.setOnAction(e -> selectAllBoxes(e));

		progressCircle.setMaxSize(300, 300);
	}

	public void selectAllBoxes(ActionEvent e) {

		// Iterate through all items in ObservableList
		for (Row item : observableList) {
			// changes checkbox to match state of selected box
			item.setEnabled(((CheckBox) e.getSource()).isSelected());
		}

	}

	public void buildList() {
		// clears the table
		observableList = FXCollections.observableArrayList();

		for (Git g : GitRepoBuilder.getrepositoryGits()) {
			try {

				// System.out.println(g.getRepository().getRepositoryState().toString());

				Config conf = g.getRepository().getConfig();
				String url = conf.getString("remote", "origin", "url");
				List<Ref> branchesList = BranchHandler.getBranches(g);
				List<String> branchNames = new ArrayList<String>();
				String hashString = "";
				String description = "";
				String authorDate = "";
				boolean upToDate = true;
				ObjectId head = g.getRepository().resolve(Constants.HEAD);

				try {

					RevWalk revWalk = new RevWalk(g.getRepository());
					revWalk.markStart(revWalk.parseCommit(head));
					RevCommit commit = revWalk.next();

					hashString = commit.getId().abbreviate(7).name();
					description = commit.getFullMessage();

					// author
					PersonIdent authorIdent = commit.getAuthorIdent();
					// committer
					PersonIdent committerIdent = commit.getCommitterIdent();
					// date time
					authorDate = modifyDateLayout(committerIdent.getWhen().toString());

					// attempting to check for new changes
					// try {
					// upToDate = !commit.getId().getName().equals(
					// g.fetch().call().getAdvertisedRef(Constants.HEAD).getObjectId().getName());
					// }catch (TransportException e) {
					// System.out.println("Not allowed access to repository");
					// } catch (Exception e) {
					// e.printStackTrace();
					// }

					revWalk.close();

				} catch (Exception e) {
					System.err.println(e);

				}

				for (Ref r : branchesList) {
					branchNames.add(r.getName());
				}

				observableList.add(
						new Row(new SimpleBooleanProperty(upToDate), g.getRepository().getDirectory().getAbsolutePath(),
								"v1.0", "v1.3", authorDate, branchNames, hashString, description, g));

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

	// allows the user to change the root directory
	@FXML
	private void setRootDirectory() {
		observableList = FXCollections.observableArrayList();
		data_table.setItems(observableList);

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Set Root Directory");
		File defaultDirectory = new File(Core.getSearchRoot());
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(new Stage());
		Core.setSearchRoot(selectedDirectory.toString());
	}

	@FXML
	private void ListRepos() {

		Task task = new Task<Void>() {
			@Override
			public Void call() {

				GitRepoBuilder.init();
				buildList();

				enabled.setCellValueFactory(param -> param.getValue().enabledProperty());
				enabled.setCellFactory(CheckBoxTableCell.forTableColumn(enabled));
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
				

				return null;
			}
		};

		//progress indicator runs while task is running
				progressCircle.visibleProperty().bind(task.runningProperty());
				TableStackPane.setEffect(new GaussianBlur());
				
				new Thread(task).start();

				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent t) {
						data_table.setItems(observableList);
						//removes blur effect
						TableStackPane.setEffect(null);
					}
				});
	}

	@FXML
	private void ShowContextMenu() {

	}

	@FXML
	private void pullSelectedRepos() throws InterruptedException {

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {

				// pulls all repos that have a checkbox next to them
				Puller p = new Puller();

				List<PullThread> threads = new ArrayList<PullThread>();

				for (Row r : observableList) {
					if (r.getEnabled() == true) {
						PullThread pt = new PullThread(r.getGit());
						pt.start();
						threads.add(pt);
					}
				}

				// waits for threads to finish
				for (int i = 0; i < threads.size(); i++) {
					threads.get(i).join();
				}

				return null;
			}
		};
		
		//progress indicator runs while task is running
		progressCircle.visibleProperty().bind(task.runningProperty());
		TableStackPane.setEffect(new GaussianBlur());
		new Thread(task).start();

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				
				//updates the new list
				buildList();
				
				//sets the list
				data_table.setItems(observableList);
				
				//removes blur effect
				TableStackPane.setEffect(null);
			}
		});
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
}
