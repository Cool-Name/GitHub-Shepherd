import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;


// TODO: Auto-generated Javadoc
/**
 * The Class BranchHandler.
 * Does most things to do with branches.
 */
public class BranchHandler {
	
	/** The file walker. */
	FileWalker fw;

	/** The local repositories. */
	private ArrayList<Repository> repos;
	
	/** The git objects. */
	private ArrayList<Git> gits;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		GitRepoBuilder.init();

		BranchHandler bh = new BranchHandler();
		bh.listAllBranchesForAllGits();
	}

	/**
	 * Instantiates a new branch handler.
	 */
	public BranchHandler() {
		// Make sure we haven't called prematurely
		if (GitRepoBuilder.getrepositoryGits() == null
				|| GitRepoBuilder.getrepositoryRepos() == null) {
			System.out
					.println("Likely that program has not been initialized. Closing!");
			return;
		}
		// Make sure repos exist
		if (!GitRepoBuilder.getrepositoryGits().isEmpty()
				&& !GitRepoBuilder.getrepositoryRepos().isEmpty()) {
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
	 * List all branches for all gits.
	 */
	public void listAllBranchesForAllGits()
	{
		for(Git g : gits)
		{
			try {
				List<Ref> call = g.branchList().call();
                for (Ref ref : call) {
                    System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
                }

                System.out.println("Now including remote branches:");
                call = g.branchList().setListMode(ListMode.ALL).call();
                for (Ref ref : call) {
                    System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
                }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * List branches.
	 *
	 * @param g The git to list from
	 */
	public static void listBranches(Git g)
	{
		try {
			List<Ref> call = g.branchList().call();
            for (Ref ref : call) {
                System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
            }

            System.out.println("Now including remote branches:");
            call = g.branchList().setListMode(ListMode.ALL).call();
            for (Ref ref : call) {
                System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the branch.
	 *
	 * @param g The git to add the branch to
	 * @param branch The branch to add
	 */
	public static void addBranch(Git g, String branch)
	{
		try {
			g.branchCreate().setName(branch).call();
		} catch (RefAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RefNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRefNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete branch.
	 *
	 * @param g The git to delete from
	 * @param branch The branch to delete
	 */
	public static void deleteBranch(Git g, String branch)
	{
		try {
			g.branchDelete().setBranchNames(branch).call();
		} catch (NotMergedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotDeleteCurrentBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
