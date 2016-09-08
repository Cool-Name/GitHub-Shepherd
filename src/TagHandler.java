import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

// TODO: Auto-generated Javadoc
/**
 * The Class TagHandler.
 * Does most things to do with tags
 */
public class TagHandler {

	/** The file walker. */
	FileWalker fw;

	/** The local repositories. */
	private ArrayList<Repository> repos;
	
	/** The local git objects. */
	private ArrayList<Git> gits;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		GitRepoBuilder.init();

		TagHandler th = new TagHandler();
		getTagFromRemote("https://github.com/Cool-Name/Test-Repo");
		//th.listAllTagsForAllGits();
	}

	/**
	 * Instantiates a new tag handler.
	 * Hunts for repositories and creates the handler.
	 */
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

	/**
	 * List all tags for all gits.
	 */
	public void listAllTagsForAllGits() {
		for (Git g : gits) {
			try {
				List<Ref> refs = g.tagList().call();
				System.out.println(refs.size());
				for (Ref ref : refs) {
					System.out.println("Tag: " + ref + " " + ref.getName()
							+ " " + ref.getObjectId().getName());

					LogCommand log = g.log();

					Ref peeledRef = g.getRepository().peel(ref);
					if (peeledRef.getPeeledObjectId() != null) {
						log.add(peeledRef.getPeeledObjectId());
					} else {
						log.add(ref.getObjectId());
					}

					Iterable<RevCommit> logs = log.call();
					for (RevCommit rev : logs) {
						System.out.println("Commit: " + rev /*
															 * + ", name: " +
															 * rev.getName() +
															 * ", id: " +
															 * rev.getId
															 * ().getName()
															 */);
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * List tags.
	 *
	 * @param g The git to list from
	 */
	public void listTags(Git g) {
		try {
			List<Ref> refs = g.tagList().call();
			System.out.println(refs.size());
			for (Ref ref : refs) {
				System.out.println("Tag: " + ref + " " + ref.getName() + " "
						+ ref.getObjectId().getName());

				LogCommand log = g.log();

				Ref peeledRef = g.getRepository().peel(ref);
				if (peeledRef.getPeeledObjectId() != null) {
					log.add(peeledRef.getPeeledObjectId());
				} else {
					log.add(ref.getObjectId());
				}

				Iterable<RevCommit> logs = log.call();
				for (RevCommit rev : logs) {
					System.out.println("Commit: " + rev /*
														 * + ", name: " +
														 * rev.getName() +
														 * ", id: " +
														 * rev.getId().getName()
														 */);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the tag.
	 *
	 * @param g The git to tag
	 * @param tag The tag to create
	 */
	public static void createTag(Git g, String tag)
	{
		try {
			Ref t = g.tag().setName(tag).call();
	        System.out.println("Created/moved tag " + t + " to repository at " + g.getRepository().getDirectory());
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTagNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete tag.
	 *
	 * @param g The git to delete from
	 * @param tag The tag to delete
	 */
	public static void deleteTag(Git g, String tag)
	{
		try {
			g.tagDelete().setTags(tag).call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Move tag.
	 *
	 * @param sourceG The source Git
	 * @param destG The destination Git
	 * @param tag The tag to move
	 */
	public static void moveTag(Git sourceG, Git destG, String tag)
	{
		try{
			ObjectId id = sourceG.getRepository().resolve("HEAD^");
			RevWalk rev = new RevWalk(sourceG.getRepository());
			RevCommit commit = rev.parseCommit(id);
			
			Ref t = destG.tag().setObjectId(commit).setName(tag).call();
			System.out.println("Created/moved tag " + t + " to repository at " + destG.getRepository().getDirectory());
			rev.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void getTagFromRemote(String uri)	//TODO: Parse/check URI with regex
	{
		 try {
			Collection<Ref> refs = Git.lsRemoteRepository()
			            .setHeads(false)
			            .setTags(true)
			            .setRemote(uri)
			            .call();
			for (Ref ref : refs) {
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
