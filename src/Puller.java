import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
// TODO: Auto-generated Javadoc

/**
 * The Class Puller. Automatically pulls to update local git repositories.
 */
public class Puller {

	/** The file walker used to search for local repositories. */
	FileWalker fw;

	/** The repositories returned by the file walker. */
	private ArrayList<Repository> repos;

	/** The git objects returned by the file walker. */
	private ArrayList<Git> gits;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		GitRepoBuilder.init();

		Puller p = new Puller();
		p.pullAll();
		System.out.println("Done pulling");
	}

	/**
	 * Instantiates a new puller. Hunts down local repositories using the file
	 * walker and sets up a puller for them.
	 */
	public Puller() {
		// Make sure we haven't called prematurely
		if (GitRepoBuilder.getrepositoryGits() == null || GitRepoBuilder.getrepositoryRepos() == null) {
			System.out.println("Likely that program has not been initialized. Closing!");
			return;
		}
		// Make sure repos exist
		if (!GitRepoBuilder.getrepositoryGits().isEmpty() && !GitRepoBuilder.getrepositoryRepos().isEmpty()) {
			gits = GitRepoBuilder.getrepositoryGits();
			repos = GitRepoBuilder.getrepositoryRepos();
		}
		// Case of no git construction
		else if (!GitRepoBuilder.getrepositoryRepos().isEmpty()) {
			repos = GitRepoBuilder.getrepositoryRepos();

			gits.clear();
			for (Repository r : repos) {
				gits.add(new Git(r));
			}
			GitRepoBuilder.setrepositoryGits(gits);
		}
		// No repos on computer
		else {
			System.out.println("Do not have any repos. Closing!");
		}
	}

	/**
	 * Pull all. Pulls all repositories to update them.
	 */
	// Basically just calls a pull command for each git object
	public void pullAll() {
		for (Git g : gits) {
			PullThread pt = new PullThread(g);
			pt.start();
		}
	}

	/**
	 * Pull single.
	 *
	 * @param g
	 *            the g
	 */
	public static void pullSingle(Git g) {
		try {
			g.pull().setCredentialsProvider(Core.getCreds()).call();
		} catch (TransportException e) {
			System.out.println("Not allowed access to repository");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Panda");
			PopupBox.setTitle("Exception");
			PopupBox.setMessage(e.toString());
			PopupBox.main(new String[0]);
		}
	}

	/**
	 * Pull single.
	 *
	 * @param s
	 *            The string representing the it directory location
	 */
	public static void pullSingle(String s) {
		try {

			Git g = new Git(new FileRepository(s));
			g.pull().setCredentialsProvider(Core.getCreds()).call();
			g.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class PullThread extends Thread {
	private Git g;
	public boolean success;
	public Row row;
	public PullThread(Git g) {
		this.g = g;
	}

	public void run() {
		try {
			g.pull().setCredentialsProvider(Core.getCreds()).call();
			CommitHandler.printDifferencesToLast(g);
			MyController.finishedThreads.incrementAndGet();
			success = true;
		} catch (TransportException e) {
			System.out.println("Not allowed access to repository");
			success = false;
			MyController.finishedThreads.incrementAndGet();
		} catch (Exception e) {
			success = false;
			MyController.finishedThreads.incrementAndGet();
			e.printStackTrace();
		}
	}
}