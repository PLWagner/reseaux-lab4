package part1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client {

	public static void main (String[] args) throws Exception {

		// Print arguments for debugging.
		for (String s: args) {
			System.out.println(s);
		}

		if( Arrays.asList(args).contains("--help") ){
			System.out.println("Usage: java Client.java [hostname] [port]");
			System.exit(0);
		}

		String hostname = args[0];
		int port = Integer.parseInt(args[0]);

		Socket socket = null;
		try {
			socket = new Socket(hostname, port);
		} catch (UnknownHostException e1) {
			System.out.println("Could not find host");
		} catch (IOException e1) {
			System.out.println("IOException occured.");
		}

		DataInputStream is = new DataInputStream(socket.getInputStream());
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		InputStreamReader r =new InputStreamReader(System.in);
		BufferedReader br =new BufferedReader(r);
		do {
			String line = "";

			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("Error printing line");
			}
			System.out.println( String.format("\nSending \"%s\" to server.", line));
			os.writeBytes(line);
		}
		while(true);

	}

}
