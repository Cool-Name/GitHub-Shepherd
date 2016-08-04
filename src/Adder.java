import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class Adder.
 */
public class Adder {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String direc = args[0];
		String fileName = args[1];
		try {
			Repository r = new FileRepository(direc);
			Git g = new Git(r);

			Adder adr = new Adder();
			adr.addFile(g, fileName);

			g.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Adds the file.
	 *
	 * @param g the g
	 * @param fileName the file name
	 */
	// This will probs end up being static
	public static void addFile(Git g, String fileName) {
		File myFile = new File(g.getRepository().getDirectory().getParent(),
				fileName);

		try {
			myFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			g.add().addFilepattern(fileName).call();

			System.out.println("Added file " + myFile + " to repository at "
					+ g.getRepository().getDirectory());

		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the all.
	 *
	 * @param g the g
	 */
	public void addAll(Git g) {

		try {
			g.add().addFilepattern(".").call();

			System.out.println("Added all to repository at "
					+ g.getRepository().getDirectory());

		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
}
