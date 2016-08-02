
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class TagHandler {

	FileWalker fw;

	private ArrayList<Repository> repos;
	private ArrayList<Git> gits;

	public static void main(String[] args) {
		GitRepoBuilder.init();

		TagHandler th = new TagHandler();
		th.listAllTags();
	}

	public TagHandler() {
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
	
	public void listAllTags()
	{
		for(Git g : gits)
		{
			try {
				List<Ref> refs = g.tagList().call();
				System.out.println(refs.size());
				for(Ref ref : refs)
				{
					System.out.println("Tag: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
					
					LogCommand log = g.log();
					
					Ref peeledRef = g.getRepository().peel(ref);
                    if(peeledRef.getPeeledObjectId() != null) {
                    	log.add(peeledRef.getPeeledObjectId());
                    } else {
                    	log.add(ref.getObjectId());
                    }

        			Iterable<RevCommit> logs = log.call();
        			for (RevCommit rev : logs) {
        				System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
        			}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
