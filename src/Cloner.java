import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;


// TODO: Auto-generated Javadoc
/**
 * The Class Cloner.
 */
public class Cloner {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		String remoteUrl = args[0];
		String toLocation = args[1];
		
        File localPath;
		try {
			localPath = File.createTempFile(toLocation, "");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
        localPath.delete();

        System.out.println("Cloning from " + remoteUrl + " to " + localPath);
        try (Git g = Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(localPath)
                .call()) {

	        System.out.println("Having repository: " + g.getRepository().getDirectory());
	        
	        g.getRepository().close();
	        g.close();
        } catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    }
}
