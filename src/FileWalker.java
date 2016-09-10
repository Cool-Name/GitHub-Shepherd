import java.io.File;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class FileWalker.
 */
/*
 * Does a recursive search though directories looking for specified
 * sub-directory
 * 
 */
public class FileWalker {

	/** The file name to search. */
	private String fileNameToSearch;

	/** The result. */
	// Store results
	private ArrayList<String> result = new ArrayList<String>();

	/** The result files. */
	private ArrayList<File> resultFiles = new ArrayList<File>();

	/**
	 * Gets the file name to search.
	 *
	 * @return the file name to search
	 */
	public String getFileNameToSearch() {
		return fileNameToSearch;
	}

	/**
	 * Sets the file name to search.
	 *
	 * @param fileNameToSearch
	 *            the new file name to search
	 */
	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public ArrayList<String> getResult() {
		return result;
	}

	/**
	 * Gets the result files.
	 *
	 * @return the result files
	 */
	public ArrayList<File> getResultFiles() {
		return resultFiles;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		FileWalker fileSearch = new FileWalker();

		fileSearch.searchDirectory(new File("C:/Users/Hakau/Documents"), ".git");

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

	/**
	 * Search directory.
	 *
	 * @param directory
	 *            the directory
	 * @param fileNameToSearch
	 *            the file name to search
	 */
	public void searchDirectory(File directory, String fileNameToSearch) {

		setFileNameToSearch(fileNameToSearch);

		if (directory.isDirectory()) {
			System.out.println("Searching: " + directory);
			search(directory);
		} else {
			System.out.println(directory.getAbsoluteFile() + " is not a directory!");
		}

	}

	/**
	 * Search.
	 *
	 * @param file
	 *            The file to search
	 */
	private void search(File file) {

		if (file.isDirectory()) {
			System.out.println("Searching directory ... " + file.getAbsoluteFile());

			// Check read permissions and not at end of tree
			if (file.canRead() && file.listFiles() != null) {
				// Get sub directories
				for (File temp : file.listFiles()) {
					// We have no reason to deal with files atm
					// so only act on directories
					if (temp.isDirectory()) {
						if (getFileNameToSearch().toLowerCase().equals(temp.getName().toLowerCase())) {
							result.add(temp.getAbsoluteFile().toString());
							resultFiles.add(temp.getAbsoluteFile());
						} else
							search(temp);
					}
				}

			} else {
				System.out.println(file.getAbsoluteFile() + " Permission Denied");
			}
		}

	}
}