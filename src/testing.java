import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class testing. No longer relevant. We just used this to check JGit functionality as we went
 */
public class testing {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		testing t = new testing();

		File file = new File("C:/Users/Hakau/Documents/comp314/Test-Repo");
		System.out.println("Initial: " + file.listFiles());

		try {
			t.init();
			t.testCreate();
			System.out.println("Create: " + file.listFiles());

			try {
				t.testClone();
				System.out.println("Clone: " + file.listFiles());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Already cloned");
			}

			t.testAdd();
			System.out.println("Add " + file.listFiles());

			t.testCommit();
			System.out.println("Commit " + file.listFiles());

			t.testPush();
			System.out.println("Push " + file.listFiles());

			t.testTrackMaster();
			System.out.println("Track " + file.listFiles());

			t.testPull();
			System.out.println("Pull " + file.listFiles());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** The remote path. */
	private String localPath, remotePath;
	
	/** The local repo. */
	private Repository localRepo;
	
	/** The git. */
	private Git git;

	/** The cp. */
	// NOTE : CHANGE THESE
	CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
			"GIT USER", "GIT PASS");

	/**
	 * Inits the.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void init() throws IOException {

		// NOTE : CHANGE THESE
		localPath = "C:/Users/Hakau/Documents/Comp314/Test-Repo";
		remotePath = "https://github.com/Cool-Name/Test-Repo";
		localRepo = new FileRepository(localPath + "/.git");
		git = new Git(localRepo);

	}

	/**
	 * Test create.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void testCreate() throws IOException {
		// Repository newRepo = new FileRepository(localPath + ".git");
		Repository existingRepo = new FileRepositoryBuilder().setGitDir(
				new File(localPath + ".git")).build();
		// newRepo.create();
	}

	/**
	 * Test clone.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GitAPIException the git API exception
	 */
	public void testClone() throws IOException, GitAPIException {
		Git.cloneRepository().setURI(remotePath).setCredentialsProvider(cp)
				.setDirectory(new File(localPath)).call();
	}

	/**
	 * Test add.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GitAPIException the git API exception
	 */
	public void testAdd() throws IOException, GitAPIException {
		File myfile = new File(localPath + "/myfile");
		myfile.createNewFile();
		git.add().addFilepattern("myfile").call();
	}

	/**
	 * Test commit.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GitAPIException the git API exception
	 * @throws JGitInternalException the j git internal exception
	 */
	public void testCommit() throws IOException, GitAPIException,
			JGitInternalException {
		git.commit().setMessage("Added myfile Fuck yeah!").call();
	}

	/**
	 * Test push.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JGitInternalException the j git internal exception
	 * @throws GitAPIException the git API exception
	 */
	public void testPush() throws IOException, JGitInternalException,
			GitAPIException {
		git.push().setCredentialsProvider(cp).call();
	}

	/**
	 * Test track master.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JGitInternalException the j git internal exception
	 * @throws GitAPIException the git API exception
	 */
	public void testTrackMaster() throws IOException, JGitInternalException,
			GitAPIException {
		git.branchCreate().setName("master")
				.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
				.setStartPoint("origin/master").setForce(true).call();
	}

	/**
	 * Test pull.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GitAPIException the git API exception
	 */
	public void testPull() throws IOException, GitAPIException {
		git.pull().setCredentialsProvider(cp).call();
	}
}
