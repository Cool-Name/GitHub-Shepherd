import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

// TODO: Auto-generated Javadoc
/**
 * The Class Committer.
 */
public class CommitHandler {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		String direc = args[0];
		String fileName = args[1];
		String message = args[2];

		try {
			Repository r = new FileRepository(direc);
			Git g = new Git(r);

			File myfile = new File(r.getDirectory().getParent(), fileName);
			myfile.createNewFile();

			// TODO set all options - - SNORE
			g.add().addFilepattern(fileName).call();
			g.commit().setMessage(message).call();

			g.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Commit single.
	 *
	 * @param g
	 *            the g
	 * @param fileName
	 *            the file name
	 * @param direc
	 *            the direc
	 * @param message
	 *            the message
	 */
	public static void commitSingle(Git g, String fileName, String direc,
			String message) {
		File myfile = new File(g.getRepository().getDirectory().getParent(),
				fileName);
		try {
			myfile.createNewFile();

			// TODO set all options - - SNORE
			g.add().addFilepattern(fileName).call();
			g.commit().setMessage(message).call();

			g.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commit single.
	 *
	 * @param g
	 *            the g
	 * @param fileName
	 *            the file name
	 * @param direc
	 *            the direc
	 * @param message
	 *            the message
	 * @param amend
	 *            the amend
	 */
	public static void commitSingle(Git g, String fileName, String direc,
			String message, boolean amend) {
		File myfile = new File(g.getRepository().getDirectory().getParent(),
				fileName);
		try {
			myfile.createNewFile();

			// TODO set all options - - SNORE
			g.add().addFilepattern(fileName).call();
			g.commit().setMessage(message).setAmend(amend).call();

			g.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commit all.
	 *
	 * @param g
	 *            the g
	 */
	public void commitAll(Git g) {
		try {
			// g.add().addFilepattern(".").call();

			// g.commit().setMessage("Committing all").call();

			g.commit().setAll(true).setMessage("Committing all").call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Commit all.
	 *
	 * @param g
	 *            the g
	 * @param message
	 *            the message
	 */
	public void commitAll(Git g, String message) {
		try {
			// g.add().addFilepattern(".").call();

			// g.commit().setMessage("Committing all").call();

			g.commit().setAll(true).setMessage(message).call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printDifferencesToLast(Git g) {

		try {																// SUPER FUCKING IMPORTANT
			ObjectId oldHead = g.getRepository().resolve("HEAD^^{tree}");	// TODO: Make a tree searcher to detect depth of old commit (i.e. how many ^'s)
			ObjectId head = g.getRepository().resolve("HEAD^{tree}");

			ObjectReader reader = g.getRepository().newObjectReader();

			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, oldHead);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, head);

			List<DiffEntry> diffs = g.diff().setNewTree(newTreeIter)
					.setOldTree(oldTreeIter).call();
			
			for(DiffEntry diff : diffs)
			{
				System.out.println(g.getRepository().toString() + " " + diff);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}