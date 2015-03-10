
import java.io.File;
import java.io.IOException;

 /**
 * Application principale qui lance les autres processus
 * @author Maxime Bouchard
 *
 */

public class ServeurDNS {
	
	public static void main(String[] args) {
		
		System.out.println("--------------------------------------");
		System.out.println("Ecole de Technologie Superieures (ETS)");
		System.out.println("GTI610 - Reseau de telecommunication");
		System.out.println("      Serveur DNS simplifie");
		System.out.println("--------------------------------------");
		
		if (args.length == 0) {
			System.out.println("Usage: "
					+"[addresse DNS] <Fichier DNS> <TrueFalse/Redirection seulement>");
			System.out.println("Pour lister la table: "
					+"showtable <Fichier DNS>");
			System.out.println("Pour lancer par defaut, tapper : default");
			System.exit(1);
		}
		
		UDPReceiver UDPR = new UDPReceiver();
		File f = null;	
		UDPR.setport(53);
		
		/* cas ou l'argument = default
		 Le serveur DNS de redirection par defaut est celui de l'ecole "10.162.8.51" 
		         ====> attention, si vous travaillez ailleurs, pensez a le mettre a jour
		 Le cache dns est le fichier: "DNSFILE.TXT"
		 et la redirection est par defaut a "false" 
		*/
		if(args[0].equals("default")){
			if (args.length <= 1) {
				UDPR.setSERVER_DNS("10.162.8.51");
				f = new File("DNSFILE.TXT");
				if(f.exists()){
					UDPR.setDNSFile("DNSFILE.TXT");
				}
				else{
					try {
						f.createNewFile();
						UDPR.setDNSFile("DNSFILE.TXT");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				UDPR.setRedirectionSeulement(false);
				
				// et on lance le thread
				UDPR.start();
			}
			else{
				System.out.print("L'�x�cution par d�faut n'a pas d'autres arguments");
			}
		}
		else{
			if(args[0].equals("showtable")){ // cas o� l'argument = showtable cacheDNS
				if (args.length == 2) {
					f = new File(args[1]);
					if(f.exists()){
						UDPR.setDNSFile(args[1]);
					}
					else{
						try {
							f.createNewFile();
							UDPR.setDNSFile(args[1]);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					QueryFinder QF = new QueryFinder(args[1]);
					QF.listCorrespondingTable();	
				}
				else if (args.length < 2){
					System.out.println("vous n'avez pas indique le nom du fichier");
				} else {
					System.out.println("vous avez indiquez trop d'arguments");
				}
			}
			else{
				if (args.length == 3) { // cas ou les arguments sont: [IPserveurDNS] [cacheDNS] [redirectionOuNon]
					UDPR.setSERVER_DNS(args[0]);
					f = new File(args[1]);
					if(f.exists()){
						UDPR.setDNSFile(args[1]);
					}	
					else{
						try {
							f.createNewFile();
							UDPR.setDNSFile(args[1]);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(args[2].equals("false")){
						UDPR.setRedirectionSeulement(false);
					}
					else{
						UDPR.setRedirectionSeulement(true);
					}
					// et on lance le thread
					UDPR.start();
				}
				else
					System.out.println("Un argument est manquant!");
			}
		}
	}	
}

