import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class Core.
 */
/*
 * Class that handles the main running of the program,
 * including interaction with the gui. Much to do here.
 * 
 */
public class Core {

	// NOTE : CHANGE THESE

	/** The search root. */
	// The main directory the program will operate on.
	private static String searchRoot = "C:/Users/Hakau/Documents/comp314";

	/** The credential provider. */
	// Credentials used to access Github
	private static CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
			"", "");

	/**
	 * Gets the search root.
	 *
	 * @return the search root
	 */
	public static String getSearchRoot() {
		return searchRoot;
	}

	/**
	 * Gets the creds.
	 *
	 * @return the creds
	 */
	public static CredentialsProvider getCreds() {
		return cp;
	}
}
