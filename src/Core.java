import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/*
 * Class that handles the main running of the program,
 * including interaction with the gui. Much to do here.
 * 
 */
public class Core {

	// NOTE : CHANGE THESE

	// The main directory the program will operate on.
	private static String searchRoot = "C:/Users/Hakau/Documents/comp314";

	// Credentials used to access Github
	private static CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
			"", "");

	public static String getSearchRoot() {
		return searchRoot;
	}

	public static CredentialsProvider getCreds() {
		return cp;
	}
}
