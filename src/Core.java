import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class Core.
 */
/*
 * Class that handles the main running of the program,
 * including interaction with the gui
 * 
 */
public class Core {

	/** The search root. */
	// The main directory the program will operate on.
	private static String searchRoot = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();//default to my documents
	
	private static ArrayList<Git> repos;

	/** The credential provider. */
	// Credentials used to access Github - deprecated as client really only cares about public, may add in future
	private static CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
			"user", "password");

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
	
	public static ArrayList<Git> getRepos()
	{
		return repos;
	}
	
	public static void setSearchRoot(String _searchRoot)
	{
		searchRoot = _searchRoot;
	}
}