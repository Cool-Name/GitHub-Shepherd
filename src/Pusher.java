import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class Pusher {

	private ArrayList<Repository> repos;
	private ArrayList<Git> gits;

	public Pusher() {
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
	
	public void pushAll()
	{
		for(Git g : gits)
		{
			PushThread pt = new PushThread(g);
			pt.start();
		}
	}

	public void pushSingle(Git g) {
		if (g == null) {
			return;
		}
		try
		{
			g.push().setCredentialsProvider(Core.getCreds()).call();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void pushSingle(String s) {
		if (s == null) {
			return;
		}
		try
		{
			Git g = new Git(new FileRepository(s));
			g.push().setCredentialsProvider(Core.getCreds()).call();
			g.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

class PushThread extends Thread {
	private Git g;

	public PushThread(Git g) {
		this.g = g;
	}

	public void run() {
		try {
			g.push().setCredentialsProvider(Core.getCreds()).call();
		} catch (TransportException e) {
			System.out.println("Not allowed access to repository");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}