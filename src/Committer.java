import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;


public class Committer {

	public static void main(String[] args)
	{
		String direc = args[0];
		String fileName = args[1];
		String message = args[2];
		
		try {
			Repository r = new FileRepository(direc);
			Git g = new Git(r);
			
			File myfile = new File(r.getDirectory().getParent(), fileName);
            myfile.createNewFile();
            
            //TODO set all options - - SNORE
            g.add().addFilepattern(fileName).call();
            g.commit().setMessage(message).call();
            
            g.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void commitSingle(Git g, String fileName, String direc, String message)
	{
		File myfile = new File(g.getRepository().getDirectory().getParent(), fileName);
        try {
			myfile.createNewFile();
			
        //TODO set all options - - SNORE
        g.add().addFilepattern(fileName).call();
        g.commit().setMessage(message).call();
        
        g.close();
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commitSingle(Git g, String fileName, String direc, String message, boolean amend)
	{
		File myfile = new File(g.getRepository().getDirectory().getParent(), fileName);
        try {
			myfile.createNewFile();
			
        //TODO set all options - - SNORE
        g.add().addFilepattern(fileName).call();
        g.commit().setMessage(message).setAmend(amend).call();
        
        g.close();
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commitAll(Git g)
	{
		try {
			//g.add().addFilepattern(".").call();
			
			//g.commit().setMessage("Committing all").call();
			
			g.commit().setAll(true).setMessage("Committing all").call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void commitAll(Git g, String message)
	{
		try {
			//g.add().addFilepattern(".").call();
			
			//g.commit().setMessage("Committing all").call();
			
			g.commit().setAll(true).setMessage(message).call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
