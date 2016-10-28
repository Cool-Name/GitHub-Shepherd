import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

// Listens on port 65051 for a list of repos to clone down
public class Listener {

	private static final int PORT = 65051;

	public static void main(String[] args) throws Exception {
		String line;
		ServerSocket welcomeSocket = new ServerSocket(PORT);

		while (true) {
			// Listen for connections
			Socket connectionSocket = welcomeSocket.accept();
			System.out.println("Accepted connection");
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			
			// Get a line
			line = inFromClient.readLine();
			System.out.println(line);
			
			// Setup the recognized directory
			File localPath = new File("Shepherd/");
			localPath.delete();
			if (!localPath.exists()) {
				localPath.mkdir();
			}
			deleteFolderInternals(localPath);
			System.out.println("Created/Cleared Shepherd");

			// Get repos and process
			String[] splits = line.split("&&");
			if (splits.length == 0)
				continue;
			else {
				// Clone valid repos - exec call determines validity here
				for (String s : splits) {
					if (!s.isEmpty() && !s.equals("null")) {
						try {
							line = line.substring(0, line.length() - 2);
							
							System.out.println("git clone " + line + " " + Paths.get("").toAbsolutePath().toString()+"/Shepherd");
							Runtime.getRuntime().exec("git clone " + line + " " + Paths.get("").toAbsolutePath().toString()+"/Shepherd/"+line.substring(line.lastIndexOf("/")));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	// Clears out the contents of a directory - beware... client was actually ok with this
	public static void deleteFolderInternals(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolderInternals(f);
				} else {
					f.delete();
				}
			}
		}
	}
}
