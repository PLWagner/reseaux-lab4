package part1;
import java.net.*;
import java.io.*;

public class Serveur {
    public static void main(String[] zero) throws Exception {

        ServerSocket serverSocket = null;
        Socket socketDuServeur ;
        try {
            serverSocket = new ServerSocket(4444);
            System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
            socketDuServeur = serverSocket.accept(); 
			System.out.println("Un être s'est connecté !");
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                clientSocket.getInputStream()));
        String inputLine, outputLine;
        
        clientSocket.close();
        serverSocket.close();
        
    }
}