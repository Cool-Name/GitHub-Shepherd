import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MyController implements Initializable {

	// declares fxml controls using their id
	@FXML
	Button listRepoBtn;
	@FXML
	Button addServer;
	@FXML
	VBox ServerList;
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
	TableColumn<Row, String> hash;
	@FXML
	TableColumn<Row, String> description;
	@FXML
	MenuItem fileChangeDirectory;
	@FXML
	MenuItem fileClose;
	@FXML
	ProgressIndicator progressCircle;
	@FXML
	StackPane TableStackPane;
	@FXML
	Label progressText;
	@FXML
	Label statusText;
	@FXML
	Label statusTitleText;
	@FXML
	Label directoryText;
	@FXML
	Label directoryTitleText;
	@FXML
	TextField serverTextField;

	// sets format for last modified column
	final String dateFormat = "dd/MM/yyyy hh:mm a";
	int activeRepos = 0;
	int totalRepos = 0;
	String currentDirectory = Core.getSearchRoot();
	static AtomicInteger finishedThreads = new AtomicInteger(0);
	static AtomicInteger scannedRepos = new AtomicInteger(0);
	static boolean processing = false;
	static double startTime;

	TreeItem<String> root;
	static ObservableList<Row> observableList = FXCollections
			.observableArrayList();

	public static ObservableList<Row> getRepos() {
		return observableList;
	}

	ObservableList<Row> state = FXCollections.observableArrayList();

	// this code is run on startup
	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

		// Places select all check box in header
		CheckBox selectAll = new CheckBox();
		enabled.setGraphic(selectAll);
		selectAll.setSelected(true);
		selectAll.setOnAction(e -> selectAllBoxes(e));

		// removes empty table txt
		data_table.setPlaceholder(new Label(""));

		// sets size of progress indicator
		progressCircle.setMaxSize(300, 300);

		// updates bottom status bar
		updateStatus();
	}

	// changes row of check boxes to match state of header check box
	public void selectAllBoxes(ActionEvent e) {

		for (Row item : observableList) {
			item.setEnabled(((CheckBox) e.getSource()).isSelected());
		}
		// updates bottom status bar
		updateStatus();
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

				/*
				 * try { tag = g.describe().call(); Ref latestCommit =
				 * g.fetch().call() .getAdvertisedRef(Constants.HEAD); // gets
				 * the tag from the latest commit latestTag = g.describe()
				 * .setTarget(latestCommit.getObjectId().getName()) .call();
				 * 
				 * } catch (Exception e) { // TODO: handle exception }
				 */

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
						authorDate = modifyDateLayout(committerIdent.getWhen()
								.toString());
					} catch (Exception e1) {
						System.err.println("Committer not found: "
								+ commit.name());
					}

					revWalk.close();

				} catch (Exception e) {
					// e.printStackTrace();
					System.err.println("There was an error in the RevWalk: "
							+ g.getRepository());
				}

				for (Ref r : branchesList) {
					branchNames.add(r.getName());
				}

				// builds row
				observableList.add(new Row(new SimpleBooleanProperty(upToDate),
						g.getRepository().getDirectory().getAbsolutePath(),
						tag, latestTag, authorDate, branchNames, hashString,
						description, g, url));

			} catch (Exception e) {
				System.err.println("There was an error while creating the row");
			}
		}
	}

	// Close the form/app
	@FXML
	public void close() {
		Stage stage = (Stage) addServer.getScene().getWindow();
		stage.close();

	}

	// Starts form to build environment
	@FXML
	public void startEnvBuilder() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
					"EnvBuilder.fxml"));
			Parent root1 = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root1));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Reads in an environment to the table
	@FXML
	public void readEnv() {
		observableList = FXCollections.observableArrayList();
		data_table.setItems(observableList);

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose environment");

		File start = new File("Env/");
		if (!start.exists())
			start.mkdir();
		chooser.setInitialDirectory(start);
		File selected = chooser.showOpenDialog(new Stage());

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(
					selected));

			ArrayList<String> envRepos = new ArrayList<String>();
			String line = fileReader.readLine();

			while ((line = fileReader.readLine()) != null) {
				if(line.charAt(0) != '~')
				{
					envRepos.add(line);
				}
			}
			for(String rep : envRepos)
			{
				
				String tag = "";
				String latestTag = "";
				String hashString = "";
				String description = "";
				String authorDate = "";
				PersonIdent committerIdent;
				PersonIdent authorIdent;
				boolean upToDate = true;
				List<String> branchNames = new ArrayList<String>();
				
				observableList.add(new Row(new SimpleBooleanProperty(true),
						rep,
						tag, latestTag, authorDate, branchNames, hashString,
						description, new Git(new FileRepository(new File(""))), rep));
			}
			
			enabled.setCellValueFactory(param -> param.getValue()
					.enabledProperty());
			enabled.setCellFactory(CheckBoxTableCell
					.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
						@Override
						public ObservableValue<Boolean> call(
								Integer param) {
							updateStatus();
							return observableList.get(param)
									.enabledProperty();
						}
					}));

			repositories
					.setCellValueFactory(new PropertyValueFactory<Row, String>(
							"repositories"));
			current_version
					.setCellValueFactory(new PropertyValueFactory<Row, String>(
							"current_version"));
			latest_version
					.setCellValueFactory(new PropertyValueFactory<Row, String>(
							"latest_version"));
			last_pulled
					.setCellValueFactory(new PropertyValueFactory<Row, String>(
							"last_pulled"));

			// allows sorting by date
			last_pulled.setComparator(new Comparator<String>() {
				@Override
				public int compare(String t, String t1) {
					SimpleDateFormat format = new SimpleDateFormat(
							dateFormat);
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

			hash.setCellValueFactory(new PropertyValueFactory<Row, String>(
					"hash"));

			// cell factory for click event on a hash cell
			Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new Callback<TableColumn<Row, String>, TableCell<Row, String>>() {
				public TableCell call(TableColumn p) {
					TableCell cell = new TableCell<Row, String>() {
						@Override
						public void updateItem(String item,
								boolean empty) {
							super.updateItem(item, empty);
							setGraphic(new Hyperlink(item));
						}
					};

					// attaches mouse click event
					cell.addEventFilter(MouseEvent.MOUSE_CLICKED,
							new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent event) {
									TableCell c = (TableCell) event
											.getSource();

									// gets the row of the table clicked
									Row r = observableList.get((c
											.getTableRow().getIndex()));

									// filters out null values
									if (r.getHash().trim() != "") {

										// builds url for specific
										// commit
										String url = r
												.getUrl()
												.substring(
														0,
														r.getUrl()
																.length() - 4)
												+ "/commit/"
												+ r.getHash();
										try {
											// opens the url in the
											// users
											// default browser
											Desktop.getDesktop()
													.browse(new URI(url));
										} catch (IOException
												| URISyntaxException e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
										}
									}
								}
							});
					return cell;
				}
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// updates the bottom status Bar
	private void updateStatus() {
		setActiveRows();

		statusTitleText.setText("Selected Repos:");
		statusText.setText(activeRepos + " / " + totalRepos);
		directoryTitleText.setText("Directory:");
		directoryText.setText(currentDirectory);
	}

	// modifies the git date string to a recogniseable state
	private String modifyDateLayout(String inputDate) throws ParseException {
		Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy")
				.parse(inputDate);
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
			currentDirectory = selectedDirectory.getAbsolutePath();
			Core.setSearchRoot(selectedDirectory.toString());
		} catch (Exception e) {
			System.err.println("There was a problem choosing that directory");
		}

		// updates bottom status bar
		updateStatus();
	}

	// List the repos into the table
	@FXML
	private void ListRepos() {
		startTime = System.currentTimeMillis();
		// clears text warnings from the table
		data_table.setPlaceholder(new Label(""));

		if (!processing) {
			processing = true;
			Task<Void> task = new Task<Void>() {
				@Override
				public Void call() {

					updateMessage("Searching");

					GitRepoBuilder.init();

					updateMessage("Building");
					buildList();

					enabled.setCellValueFactory(param -> param.getValue()
							.enabledProperty());
					enabled.setCellFactory(CheckBoxTableCell
							.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
								@Override
								public ObservableValue<Boolean> call(
										Integer param) {
									updateStatus();
									return observableList.get(param)
											.enabledProperty();
								}
							}));

					repositories
							.setCellValueFactory(new PropertyValueFactory<Row, String>(
									"repositories"));
					current_version
							.setCellValueFactory(new PropertyValueFactory<Row, String>(
									"current_version"));
					latest_version
							.setCellValueFactory(new PropertyValueFactory<Row, String>(
									"latest_version"));
					last_pulled
							.setCellValueFactory(new PropertyValueFactory<Row, String>(
									"last_pulled"));

					// allows sorting by date
					last_pulled.setComparator(new Comparator<String>() {
						@Override
						public int compare(String t, String t1) {
							SimpleDateFormat format = new SimpleDateFormat(
									dateFormat);
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

					hash.setCellValueFactory(new PropertyValueFactory<Row, String>(
							"hash"));

					// cell factory for click event on a hash cell
					Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new Callback<TableColumn<Row, String>, TableCell<Row, String>>() {
						public TableCell call(TableColumn p) {
							TableCell cell = new TableCell<Row, String>() {
								@Override
								public void updateItem(String item,
										boolean empty) {
									super.updateItem(item, empty);
									setGraphic(new Hyperlink(item));
								}
							};

							// attaches mouse click event
							cell.addEventFilter(MouseEvent.MOUSE_CLICKED,
									new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											TableCell c = (TableCell) event
													.getSource();

											// gets the row of the table clicked
											Row r = observableList.get((c
													.getTableRow().getIndex()));

											// filters out null values
											if (r.getHash().trim() != "") {

												// builds url for specific
												// commit
												String url = r
														.getUrl()
														.substring(
																0,
																r.getUrl()
																		.length() - 4)
														+ "/commit/"
														+ r.getHash();
												try {
													// opens the url in the
													// users
													// default browser
													Desktop.getDesktop()
															.browse(new URI(url));
												} catch (IOException
														| URISyntaxException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}
									});
							return cell;
						}
					};

					hash.setCellFactory(cellFactory);
					description
							.setCellValueFactory(new PropertyValueFactory<Row, String>(
									"description"));

					return null;
				}
			};

			// progress indicator runs while task is running
			progressCircle.visibleProperty().bind(task.runningProperty());
			progressText.textProperty().bind(task.messageProperty());
			progressText.visibleProperty().bind(task.runningProperty());
			TableStackPane.setEffect(new GaussianBlur());

			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();

			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					// populates table
					data_table.setItems(observableList);

					// updates bottom status bar
					updateStatus();

					// removes blur effect
					TableStackPane.setEffect(null);

					// resets flag
					processing = false;

					if (observableList.size() == 0) {
						data_table
								.setPlaceholder(new Label(
										"No repos found... Try a different directory."));
					} else {
						data_table.setPlaceholder(new Label(""));
					}
					double estimatedTime = System.currentTimeMillis()
							- startTime;
					System.out.println(estimatedTime / 1000);
				}
			});
		}
	}

	@FXML
	private void ShowContextMenu() {
		updateStatus();
	}

	// Pulls down repos with checkbox checked
	@FXML
	private void pullSelectedRepos() throws InterruptedException {
		if (!processing) {
			if (observableList.size() == 0) {
				data_table
						.setPlaceholder(new Label("List repos before pulling"));
			} else {
				data_table.setPlaceholder(new Label(""));
				processing = true;

				Task<Void> task = new Task<Void>() {
					@Override
					public Void call() throws InterruptedException {

						// pulls all repos that have a checkbox next to them

						List<PullThread> threads = new ArrayList<PullThread>();

						finishedThreads = new AtomicInteger(0);
						for (Row r : observableList) {
							if (r.getEnabled() == true) {
								PullThread pt = new PullThread(r.getGit());
								pt.setDaemon(true);
								pt.start();
								pt.row = r;
								threads.add(pt);
							} else {
								r.setEnabled(false);
							}
						}

						setActiveRows();
						while (finishedThreads.get() < activeRepos) {
							updateMessage("Pulling\n" + finishedThreads.get()
									+ " / " + activeRepos);
						}
						updateMessage("Pulling\n" + finishedThreads.get()
								+ " / " + activeRepos);

						// waits for threads to finish
						for (int i = 0; i < threads.size(); i++) {
							threads.get(i).join();
							state.add(threads.get(i).row);
						}

						return null;
					}
				};

				// progress indicator runs while task is running
				progressCircle.visibleProperty().bind(task.runningProperty());
				progressText.visibleProperty().bind(task.runningProperty());
				progressText.textProperty().bind(task.messageProperty());
				TableStackPane.setEffect(new GaussianBlur());

				Thread t = new Thread(task);
				t.setDaemon(true);
				t.start();

				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent t) {

						// updates the new list
						buildList();

						// sets the list
						data_table.setItems(observableList);

						// updates bottom status bar
						updateStatus();

						// removes blur effect
						TableStackPane.setEffect(null);
						processing = false;
					}
				});
			}
		}
	}

	public void setActiveRows() {
		activeRepos = 0;
		totalRepos = 0;
		for (Row r : observableList) {
			if (r.getEnabled() == true) {
				activeRepos++;
			}
			totalRepos++;
		}
	}

	// Adds a server to the left-hand side list
	@FXML
	private void AddServer() {
		Pattern IP4PATTERN = Pattern
				.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		String ip = serverTextField.getText();
		if (IP4PATTERN.matcher(ip).matches() || ip.equals("localhost")) {
			ServerCard server = new ServerCard(ip, "Production",
					"Click to deploy selected", new Image("images/server.png"));
			ServerList.getChildren().add(server);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("ERROR: Bad IP Address");
			alert.setHeaderText("IP address did not match expected format");
			alert.setContentText("Expected format is xxx.xxx.xxx.xxx");

			alert.showAndWait();
		}
	}
}
