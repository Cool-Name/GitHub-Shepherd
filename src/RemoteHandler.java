import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;


// TODO: Auto-generated Javadoc
/**
 * The Class RemoteHandler.
 */
public class RemoteHandler {

	/** The fw. */
	FileWalker fw;

	/** The repos. */
	private ArrayList<Repository> repos;
	
	/** The gits. */
	private ArrayList<Git> gits;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
	}

	/**
	 * Instantiates a new remote handler.
	 */
	public RemoteHandler() {
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
	 * List all remotes for all gits.
	 */
	public void listAllRemotesForAllGits()
	{
		for(Git g : gits)
		{
			try {
				Collection<Ref> refs = g.lsRemote().call();
				for (Ref ref : refs) {
                    System.out.println(g.getRepository().toString() + " Ref: " + ref);
                }

                // heads only
                refs = g.lsRemote().setHeads(true).call();
                for (Ref ref : refs) {
                    System.out.println(g.getRepository().toString() +" Head: " + ref);
                }

                // tags only
                refs = g.lsRemote().setTags(true).call();
                for (Ref ref : refs) {
                    System.out.println(g.getRepository().toString() + " Remote tag: " + ref);
                }
			} catch (InvalidRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * List remotes for a given Git repository.
	 *
	 * @param g The git to list for
	 */
	public void listRemotes(Git g)
	{
		try {
			Collection<Ref> refs = g.lsRemote().call();
			for (Ref ref : refs) {
                System.out.println(g.getRepository().toString() + " Ref: " + ref);
            }

            // heads only
            refs = g.lsRemote().setHeads(true).call();
            for (Ref ref : refs) {
                System.out.println(g.getRepository().toString() +" Head: " + ref);
            }

            // tags only
            refs = g.lsRemote().setTags(true).call();
            for (Ref ref : refs) {
                System.out.println(g.getRepository().toString() + " Remote tag: " + ref);
            }
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * List remotes from repository URL.
	 *
	 * @param URL The url to list for
	 */
	public void listRemotesFromRepositoryURL(String URL)
	{
		try {
			Collection<Ref> refs = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(URL).call();
			for(Ref ref : refs)
			{
				System.out.println("Ref: " + ref);
			}
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
