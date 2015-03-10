import java.util.List;

/**
 * Class utilitaire pour creer un packet de reponse pour DNS en utilisant le protocol UDP.
 * Cette classe s'occupe de formater les datas selon la specification du protocole.
 * @author lighta, Simon
 */
public class UDPAnswerPacketCreator {
	public class Answerpacket {
		private int longueur; //taille du packet de reponse
		private byte[] bytes; //packet de reponse
	};
	
	Answerpacket answer_pkt; //peut etre etendu en liste
	
	/** 
	 * Doit etre utiliser apres CreateAnswerPacket
	 * @return le packet de reponse creer
	 */
	public Answerpacket getAnswrpacket(){
		return answer_pkt;
	}

	
	/**
	 * Constructeur de notre class utilitaire, peutetre utiliser
	 * pour implementer differente strategy
	 */
	private UDPAnswerPacketCreator(){
		
	}
	
	/** Holder (thread safe) */
	private static class SingletonHolder
	{	
		/** Instance unique non préinitialisée */
		private final static UDPAnswerPacketCreator instance = new UDPAnswerPacketCreator();
	}
	 
	/** Point d'accès pour l'instance unique du singleton */
	public static UDPAnswerPacketCreator getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * 
	 * @param Qpacket : Datagrame packet de la query DNS
	 * @param listadrr : Adresse IP (v4) a transmettre comme reponse
	 * @return tableau de bytes donnant un packet de reponse DNS
	 */
	public byte[] CreateAnswerPacket(byte[] Qpacket,List<String> listadrr){
		Answerpacket answer = new Answerpacket();
		int ancount = listadrr.size();
		if(ancount == 0){
			System.out.println("No adresse to search exiting");
			return null;
		}
		System.out.println("Preparing packet for len="+ancount);
		
		//System.out.println("Le packet QUERY recu");
		
		for(int i = 0;i < Qpacket.length;i++){
			if(i%16 == 0){
				//System.out.println("\r");
			}
			//System.out.print(Integer.toHexString(Qpacket[i] & 0xff).toString() + " ");
		}
		//System.out.println("\r");
		
		//copie les informations dans un tableau qui est utilise de buffer
		//durant la modification du packet
		byte[] tmp_packet = new byte[1024];
		System.arraycopy(Qpacket, 0, tmp_packet, 0, Qpacket.length);
		
		//copie de l'identifiant
		tmp_packet[0] = (byte)Qpacket[0];
		tmp_packet[1] = (byte)Qpacket[1];
		
		//modification des parametres
		//Active le champ reponse dans l'en-tete
		tmp_packet[2] = (byte) 0x81; //QR+opcode+AA+TC+RD
		tmp_packet[3] = (byte) 0x80; //RA+Z+RCODE
		tmp_packet[4] = (byte) 0x00; //Qcount & 0xFF00
		tmp_packet[5] = (byte) 0x01; //Qcount & 0x00FF
		
		tmp_packet[6] = ((byte) ((ancount&(0xFF00)) >>8) ); //Ancount & 0xFF00
		tmp_packet[7] = (byte) ((ancount&(0x00FF)) ); //Ancount & 0x00FF
		
		//Serveur authority --> 0 il n'y a pas de serveur d'autorite
		tmp_packet[8] = (byte) 0x00; //NScount & 0xFF00
		tmp_packet[9] = (byte) 0x00; //NScount & 0x00FF
		
		tmp_packet[10] = (byte) 0x00; //ARCOUNT & 0xFF00
		tmp_packet[11] = (byte) 0x00; //ARcount & 0x00FF

		//Lecture de l'hostname
		//ici comme on ne connait pas la grandeur que occupe le nom de domaine
		//nous devons rechercher l'index pour pouvoir placer l'adresse IP au bon endroit
		//dans le packet
		
		String hostName = "";
		int index = 12, len;
		
		//lire qname
		while ((len = (int)tmp_packet[index]) != 0) {
			//System.out.println("len=" + len);
			for (int i = 1; i <= len; i++) {
				hostName += (char)(tmp_packet[index+i]);
			}
			hostName = hostName + ".";
			index += len+1;
		}
		//System.out.println("hostname found="+hostName);
		//tmp_packet[index] = 0; //last index is 0 and mark end of qname

		//Identification de la class
		//type
		tmp_packet[index + 1] = (byte)0x00; //Qtype  & 0xFF00
		tmp_packet[index + 2] = (byte)0x01; //Qtype  & 0x00FF
		//class
		tmp_packet[index + 3] = (byte)0x00; //Qclass  & 0xFF00
		tmp_packet[index + 4] = (byte)0x01; //Qclass  & 0x00FF
		
		
		//Champ reponse
		int i, lenanswer=16;
		int j=index + 5;
		for(i=0; i<ancount; i++){
			//name offset !TODO whaaaat ?
			tmp_packet[j] = (byte) (0xC0); //name  & 0xFF00
			tmp_packet[j + 1] = (byte) (0x0C); //name  & 0x00FF
			
			tmp_packet[j + 2] = (byte) (0x00); //type  & 0xFF00
			tmp_packet[j + 3] = (byte) 0x01;	//type  & 0x00FF
			
			
			tmp_packet[j + 4] = (byte) 0x00; //class  & 0xFF00
			tmp_packet[j + 5] = (byte) 0x01; //class & 0x00FF
			
			//TTL
			tmp_packet[j + 6] = (byte) 0x00;
			tmp_packet[j + 7] = (byte) 0x01;
			tmp_packet[j + 8] = (byte) 0x1a;
			tmp_packet[j + 9] = (byte) (0x6c);
			
			
			//Grace a l'index de position, nous somme en mesure
			//de faire l'injection de l'adresse IP dans le packet
			//et ce au bon endroit
			tmp_packet[j + 10] = (byte) (0x00); //RDLENGHT & 0xFF00
			tmp_packet[j + 11] = (byte) 0x04;//taille RDLENGHT 0x00FF
			
			//Conversion de l'adresse IP de String en byte
			String adrr = listadrr.get(i);
			//System.out.println("Adr to transmit="+adrr);
			adrr = adrr.replace("."," ");
			String[] adr = adrr.split(" ");
			byte part1 = (byte)(Integer.parseInt(adr[0]) & 0xff);
			byte part2 = (byte)(Integer.parseInt(adr[1]) & 0xff);
			byte part3 = (byte)(Integer.parseInt(adr[2]) & 0xff);
			byte part4 = (byte)(Integer.parseInt(adr[3]) & 0xff);
			
			//IP RDATA
			tmp_packet[j + 12] = (byte) unsignedIP(part1);
			tmp_packet[j + 13] = (byte) unsignedIP(part2);
			tmp_packet[j + 14] = (byte) unsignedIP(part3);
			tmp_packet[j + 15] = (byte) unsignedIP(part4);
			j+=lenanswer;
		}
		
		answer.longueur = j; 
		answer.bytes = new byte[answer.longueur];
		for(i = 0; i < answer.longueur; i++){ //remply le reste de merde
			answer.bytes[i] = (byte) tmp_packet[i];
		}
		
//		System.out.println("Identifiant: 0x" + Integer.toHexString(answer.bytes[0] & 0xff) + Integer.toHexString(answer.bytes[1] & 0xff));
//		System.out.println("parametre: 0x" + Integer.toHexString(answer.bytes[2] & 0xff) + Integer.toHexString(answer.bytes[3] & 0xff));
//		System.out.println("question: 0x" + Integer.toHexString(answer.bytes[4] & 0xff) + Integer.toHexString(answer.bytes[5] & 0xff));
//		System.out.println("reponse: 0x" + Integer.toHexString(answer.bytes[6] & 0xff) + Integer.toHexString(answer.bytes[7] & 0xff));
//		System.out.println("autorite: 0x" + Integer.toHexString(answer.bytes[8] & 0xff) + Integer.toHexString(answer.bytes[9] & 0xff));
//		System.out.println("info complementaire: 0x" + Integer.toHexString(answer.bytes[10] & 0xff) + Integer.toHexString(answer.bytes[11] & 0xff));
		
		
//		for(i = 0;i < answer.longueur;i++){
//			if(i%16 == 0){
//				System.out.println("\r");
//			}
//			System.out.print(Integer.toHexString(answer.bytes[i] & 0xff).toString() + " ");
//		}
//		System.out.println("\r");
		
		return answer.bytes;
	}
	
	int unsignedIP(int data){
		int tmp=0;
		if( (data&(0x80))==(0x80) )
			tmp=(data&(0x7F))+128;
		else
			tmp=data;
		return tmp;
	}
}