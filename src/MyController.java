import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
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
	@FXML
	Label progressText;

	// sets format for last modified column
	final String dateFormat = "dd/MM/yyyy hh:mm a";
	int activeItems = 0;
	static AtomicInteger finishedThreads = new AtomicInteger(0);
	static boolean processing = false;

	TreeItem<String> root;

	ObservableList<Row> observableList = FXCollections.observableArrayList();

	// this code is run on startup
	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		// adds the root item servers
		root = new TreeItem<String>("Servers");
		root.setExpanded(true);
		serverList.setRoot(root);

		// Places select all check box in header
		CheckBox selectAll = new CheckBox();
		enabled.setGraphic(selectAll);
		selectAll.setSelected(true);
		selectAll.setOnAction(e -> selectAllBoxes(e));

		// removes empty table txt
		data_table.setPlaceholder(new Label(""));

		// sets size of progress indicator
		progressCircle.setMaxSize(300, 300);
	}

	// changes row of check boxes to match state of header check box
	public void selectAllBoxes(ActionEvent e) {

		for (Row item : observableList) {
			item.setEnabled(((CheckBox) e.getSource()).isSelected());
		}

	}

	// creates table rows and builds an observable list
	public void buildList() {

		// clears the table
		observableList = FXCollections.observableArrayList();

		for (Git g : GitRepoBuilder.getrepositoryGits()) {
			try {
				Config conf = g.getRepository().getConfig();
				String url = conf.getString("remote", "origin", "url");
				List<Ref> branchesList = BranchHandler.getBranches(g);
				List<String> branchNames = new ArrayList<String>();
				ObjectId head = g.getRepository().resolve(Constants.HEAD);

				// initialises variables
				String tag = "";
				String latestTag = "";
				String hashString = "";
				String description = "";
				String authorDate = "";
				PersonIdent committerIdent;
				PersonIdent authorIdent;
				boolean upToDate = true;

				try {
					tag = Git.wrap(g.getRepository()).describe().setTarget(ObjectId.fromString(head.getName())).call();
				} catch (Exception e1) {
					System.err.println("Tag not found: " + g.getRepository());
				}

				// try {
				// Collection<Ref> refs = g.lsRemoteRepository().call();
				//
				// for (Ref ref : refs) {
				// System.out.println("Ref: " + ref);
				// }
				//
				// latestTag =
				// Git.wrap(g.getRepository()).describe().setTarget(ObjectId.fromString(head.getName())).call();
				// } catch (Exception e1) {
				// System.err.println("Tag not found: " + g.getRepository());
				// }

				try {

					RevWalk revWalk = new RevWalk(g.getRepository());
					revWalk.markStart(revWalk.parseCommit(head));
					RevCommit commit = revWalk.next();

					// abbreviated commit hash
					hashString = commit.getId().abbreviate(7).name();

					// gets the description
					description = commit.getFullMessage();

					try {
						// author
						authorIdent = commit.getAuthorIdent();
					} catch (Exception e2) {
						System.err.println("Date not found" + commit.name());
					}

					try {

						// committer
						committerIdent = commit.getCommitterIdent();

						// date time
						authorDate = modifyDateLayout(committerIdent.getWhen().toString());
					} catch (Exception e1) {
						System.err.println("Committer not found: " + commit.name());
					}

					revWalk.close();

				} catch (Exception e) {
					System.err.println("There was an error in the RevWalk: " + g.getRepository());
				}

				for (Ref r : branchesList) {
					branchNames.add(r.getName());
				}

				// builds row
				observableList.add(
						new Row(new SimpleBooleanProperty(upToDate), g.getRepository().getDirectory().getAbsolutePath(),
								tag, "", authorDate, branchNames, hashString, description, g));

			} catch (Exception e) {
				System.err.println("There was an error while creating the row");
			}
		}
	}

	// modifies the git date string to a recogniseable state
	private String modifyDateLayout(String inputDate) throws ParseException {
		Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(inputDate);
		return new SimpleDateFormat(dateFormat).format(date);
	}

	// allows the user to change the root directory
	@FXML
	private void setRootDirectory() {

		try {
			observableList = FXCollections.observableArrayList();
			data_table.setItems(observableList);

			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Set Root Directory");
			File defaultDirectory = new File(Core.getSearchRoot());
			chooser.setInitialDirectory(defaultDirectory);
			File selectedDirectory = chooser.showDialog(new Stage());
			Core.setSearchRoot(selectedDirectory.toString());
		} catch (Exception e) {
			System.err.println("There was a problem choosing that directory");
		}
	}

	@FXML
	private void ListRepos() {

		if (!processing) {
			processing = true;
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

			// progress indicator runs while task is running
			progressCircle.visibleProperty().bind(task.runningProperty());
			TableStackPane.setEffect(new GaussianBlur());

			new Thread(task).start();

			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					data_table.setItems(observableList);
					// removes blur effect
					TableStackPane.setEffect(null);
					processing = false;
				}
			});
		}
	}

	@FXML
	private void ShowContextMenu() {

	}

	@FXML
	private void pullSelectedRepos() throws InterruptedException {
		if (!processing) {
			if (observableList.size() == 0) {
				data_table.setPlaceholder(new Label("Scan directory before pulling"));
			}

			processing = true;
			Task<Void> task = new Task<Void>() {
				@Override
				public Void call() throws InterruptedException {

					// pulls all repos that have a checkbox next to them
					Puller p = new Puller();

					List<PullThread> threads = new ArrayList<PullThread>();

					finishedThreads = new AtomicInteger(0);
					for (Row r : observableList) {
						if (r.getEnabled() == true) {
							PullThread pt = new PullThread(r.getGit());

							pt.start();

							threads.add(pt);
						} else {
							r.setEnabled(false);
						}
					}

					setActiveRows();
					while (finishedThreads.get() < activeItems) {
						updateMessage(finishedThreads.get() + " / " + activeItems);
					}

					updateMessage(finishedThreads.get() + " / " + activeItems);
					// waits for threads to finish
					for (int i = 0; i < threads.size(); i++) {
						threads.get(i).join();
					}

					return null;
				}
			};

			// progress indicator runs while task is running
			progressCircle.visibleProperty().bind(task.runningProperty());
			progressText.visibleProperty().bind(task.runningProperty());
			progressText.textProperty().bind(task.messageProperty());
			TableStackPane.setEffect(new GaussianBlur());

			new Thread(task).start();

			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {

					// updates the new list
					buildList();

					// sets the list
					data_table.setItems(observableList);

					// removes blur effect
					TableStackPane.setEffect(null);
					processing = false;
				}
			});
		}
	}

	public void setActiveRows() {
		activeItems = 0;
		for (Row r : observableList) {
			if (r.getEnabled() == true) {
				activeItems++;
			}
		}
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
