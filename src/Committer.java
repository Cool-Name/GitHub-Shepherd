import java.io.File;

import org.eclipse.jgit.api.Git;
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
}
