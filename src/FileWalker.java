import java.io.File;
import java.util.ArrayList;

/*
 * Does a recursive search though directories
 * looking for specified sub-directory
 * 
 */
public class FileWalker {

	private String fileNameToSearch;

	// Store results
	private ArrayList<String> result = new ArrayList<String>();
	private ArrayList<File> resultFiles = new ArrayList<File>();

	public String getFileNameToSearch() {
		return fileNameToSearch;
	}

	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	public ArrayList<String> getResult() {
		return result;
	}

	public ArrayList<File> getResultFiles() {
		return resultFiles;
	}

	public static void main(String[] args) {

		FileWalker fileSearch = new FileWalker();

		fileSearch
				.searchDirectory(new File("C:/Users/Hakau/Documents"), ".git");

		int count = fileSearch.getResult().size();
		if (count == 0) {
			System.out.println("\nNo result found!");
		} else {
			System.out.println("\nFound " + count + " result!\n");
			for (String matched : fileSearch.getResult()) {
				System.out.println("Found : " + matched);
			}
		}
	}

	public void searchDirectory(File directory, String fileNameToSearch) {

		setFileNameToSearch(fileNameToSearch);

		if (directory.isDirectory()) {
			System.out.println("Searching: " + directory);
			search(directory);
		} else {
			System.out.println(directory.getAbsoluteFile()
					+ " is not a directory!");
		}

	}

	private void search(File file) {

		if (file.isDirectory()) {
			System.out.println("Searching directory ... "
					+ file.getAbsoluteFile());

			// Check read permissions and not at end of tree
			if (file.canRead() && file.listFiles() != null) {
				// Get sub directories
				for (File temp : file.listFiles()) {
					// We have no reason to deal with files atm
					// so only act on directories
					if (temp.isDirectory()) {
						if (getFileNameToSearch().toLowerCase().equals(
								temp.getName().toLowerCase())) {
							result.add(temp.getAbsoluteFile().toString());
							resultFiles.add(temp.getAbsoluteFile());
						} else
							search(temp);
					}
				}

			} else {
				System.out.println(file.getAbsoluteFile()
						+ " Permission Denied");
			}
		}

	}
}