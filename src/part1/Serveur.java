package part1;
import java.net.*;
import java.io.*;

public class Serveur {
    @SuppressWarnings("deprecation")
	public static void main(String[] zero) throws Exception {

        ServerSocket serverSocket = null;
        Socket socketDuServeur = null ;
        try {
            serverSocket = new ServerSocket(4444);
            System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
            socketDuServeur = serverSocket.accept(); 
			System.out.println("Un être s'est connecté !");
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }
        
        
        DataInputStream is = new DataInputStream(socketDuServeur.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

		do {
			String line = "";

			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("Error printing line");
			}
			System.out.println( String.format("\nRecieved \"%s\" from client.", line));
		}
		while(true);
        
       // serverSocket.close();
        
    }
}