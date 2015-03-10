import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

	/**
	 * Class utilitaire pour envoyer des messages UDP a un destinataire.
	 * Le destinataire est choisit lors de la creation de l'instance.
	 * L'envoi verifie que notre adresse est valide avant d'effectuer le traitement
	 * @author lighta
	 */
	public class UDPSender  {

	private final static int BUF_SIZE = 1024;
	
	private String dest_ip = null; //ip de reception
	private int dest_port = 53;  // port de reception
	private DatagramSocket SendSocket = null; //socket d'envoi
	private InetAddress addr = null; //adresse de reception (format inet)
	
	/**
	 * Contructor
	 * @param destip = adresse ip ou envoyer le paquet
	 * @param destport = port de destination du paquet
	 * @param sendsocket : socket a utiliser pour l'envoi
	 * NB : Si le socket d'envoi n'est pas specifier on essaye d'en creer un
	 */
	public UDPSender(String destip, int destport, DatagramSocket sendsocket){
		try {
			if(sendsocket == null) SendSocket = new DatagramSocket();
			else SendSocket = sendsocket;
			System.out.println("Construction d'un socket d'envoi sur port="+SendSocket.getLocalPort());
	
			this.dest_port = destport;
			this.dest_ip = destip;
			//cree l'adresse de destination
			this.addr = InetAddress.getByName(dest_ip);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Alternative constructor
	 * @param address : adresse de destination (class Inet)
	 * @param port : port de destination
	 * @param sendsocket : socket a utiliser pour l'envoi
	 * NB : Si le socket d'envoi n'est pas specifier on essaye dans creer un
	 */
	public UDPSender(InetAddress address, int port, DatagramSocket sendsocket) {
		try {
			if(sendsocket == null) SendSocket = new DatagramSocket();
			else SendSocket = sendsocket;
			System.out.println("Construction d'un socket d'envoi sur port="+SendSocket.getLocalPort());

			this.dest_port = port;
			this.addr = address;
			dest_ip = address.getHostAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return l'adresse ip (string) specifier pour la destination
	 */
	public String getDest_ip() {
		return dest_ip;
	}

	/**
	 * @return le port specifier pour la destination
	 */
	public int getDest_port() {
		return dest_port;
	}

	/**
	 * @return l'adresse Inet specifier pour la destination
	 */
	public InetAddress getAddr() {
		return addr;
	}

	/**
	 * Effectue l'envoie du message a la destination specifie.
	 * NB : Ne ferme pas le socket apres l'envoie
	 * @param packet : data a envoyer (UDP)
	 * @throws IOException
	 */
	public void SendPacketNow(DatagramPacket packet) 
		throws IOException {
		//Envoi du packet a un serveur dns pour interrogation
		if(SendSocket == null)
			throw new IOException("Invalid Socket for send (null)");
		if(packet == null)
			throw new IOException("Invalid Packet for send (null)");
		try {
			//set la destination du packet
			packet.setAddress(addr);
			packet.setPort(dest_port);
			//Envoi le packet
			System.out.println("Sending packet to adr="+dest_ip+" port="+dest_port+ "srcport="+SendSocket.getLocalPort());
			SendSocket.send(packet);
		} catch (Exception e) {
			System.err.println("Probleme a l'execution :");
			e.printStackTrace(System.err);
		}
	}
}
