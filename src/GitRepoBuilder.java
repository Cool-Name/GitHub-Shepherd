import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class GitRepoBuilder.
 */
/*
 * A class that takes the git-containing repositories 
 * and constructs the relevant git objects. Also handles
 * ref objects etc.
 * Additionally, it should cache the repo directories and
 * important information like the relevant URL and log
 * data.
 * 
 * TODO Determine whether or not this should be static... Probably
 * 
 */
public class GitRepoBuilder {

	/** The repository strings. */
	// Different representations of repository systems
	private static ArrayList<String> repositoryStrings = new ArrayList<String>();
	
	/** The repository repos. */
	private static ArrayList<Repository> repositoryRepos = new ArrayList<Repository>();
	
	/** The repository gits. */
	private static ArrayList<Git> repositoryGits = new ArrayList<Git>();

	/** The string cache location. */
	// Where the cache file was most recently stored.
	private static String stringCacheLocation;

	/**
	 * Gets the repository repos.
	 *
	 * @return the repository repos
	 */
	public static ArrayList<Repository> getrepositoryRepos() {
		return repositoryRepos;
	}

	/**
	 * Gets the repository gits.
	 *
	 * @return the repository gits
	 */
	public static ArrayList<Git> getrepositoryGits() {
		return repositoryGits;
	}

	/**
	 * Sets the repository repos.
	 *
	 * @param reps the new repository repos
	 */
	public static void setrepositoryRepos(ArrayList<Repository> reps) {
		repositoryRepos = reps;
	}

	/**
	 * Sets the repository gits.
	 *
	 * @param gits the new repository gits
	 */
	public static void setrepositoryGits(ArrayList<Git> gits) {
		repositoryGits = gits;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		init();
	}

	/**
	 * Inits the local repositories.
	 * Note that this is not init in the Git sense
	 */
	/*
	 * Idealy this should only be run once, then the user can decide to re-use
	 * if they want to do a system wide update This basically searches for all
	 * repos under directory and builds different representations.
	 */
	public static void init() {
		FileWalker fw = new FileWalker();
		fw.searchDirectory(new File(Core.getSearchRoot()), ".git");
		repositoryStrings = fw.getResult();

		for (String s : repositoryStrings) {
			try {
				Repository r = new FileRepository(s);
				repositoryRepos.add(r);

				//System.out.println("Reached repo list addition");

				Git g = new Git(r);
				repositoryGits.add(g);

				//System.out.println("Reached git list addition");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cache strings.
	 */
	/*
	 * Write repo locations to a text file so that we can load in later for
	 * speed
	 * 
	 * The next methods also does this
	 */
	public static void cacheStrings() {
		if (repositoryStrings.isEmpty())
			return;

		Path current = Paths.get("");
		File f = new File(current.toAbsolutePath().toString() + "SHEPHERD.txt");

		stringCacheLocation = current.toAbsolutePath().toString()
				+ "SHEPHERD.txt";

		try {
			PrintWriter writer = new PrintWriter(f);
			for (String s : repositoryStrings) {
				writer.println(s);
			}
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cache strings.
	 *
	 * @param location The location to cache to
	 */
	public static void cacheStrings(String location) {
		if (repositoryStrings.isEmpty())
			return;

		File f = new File(location + "SHEPHERD.txt");

		stringCacheLocation = location + "SHEPHERD.txt";

		try {
			PrintWriter writer = new PrintWriter(f);
			for (String s : repositoryStrings) {
				writer.println(s);
			}
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read input strings from cache.
	 */
	/*
	 * Read in a text cache of repository locations
	 * 
	 * Same method for the next 4
	 */
	public static void readInputStrings() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					stringCacheLocation));

			repositoryStrings.clear();

			String line = br.readLine();
			while (line != null) {
				repositoryStrings.add(line);
			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Read input strings from cache.
	 *
	 * @param location The cache location
	 */
	public static void readInputStrings(String location) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));

			repositoryStrings.clear();

			String line = br.readLine();
			while (line != null) {
				repositoryStrings.add(line);
			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Read input strings.
	 *
	 * @param append Whether or not we use the append option
	 */
	public static void readInputStrings(boolean append) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					stringCacheLocation));
			if (!append)
				repositoryStrings.clear();

			String line = br.readLine();
			while (line != null) {
				if (!append && !repositoryStrings.contains(line))
					repositoryStrings.add(line);
			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Read input strings.
	 *
	 * @param location the location
	 * @param append Whether or not we append
	 */
	public static void readInputStrings(String location, boolean append) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			if (!append)
				repositoryStrings.clear();

			String line = br.readLine();
			while (line != null) {
				if (append && !repositoryStrings.contains(line))
					repositoryStrings.add(line);
			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Cache repo data.
	 */
	public static void cacheRepoData() {
		// TODO Do this once repo structure is better understood
	}

	/**
	 * Cache git data.
	 */
	public static void cacheGitData() {
		// TODO Do this once Git data is better understood
	}
}
