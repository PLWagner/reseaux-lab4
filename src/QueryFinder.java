/******************************************************
 Laboratoire #3 : Programmation d'un serveur DNS
 
 Cours :             LOG610
 Session :           Hiver 2007
 Groupe :            01
 Projet :            Laboratoire #3
 Etudiant(e)(s) :    Maxime Bouchard
 Code(s) perm. :     BOUM24028309
 
 Professeur :        Michel Lavoie 
 Nom du fichier :    QueryFinder.java
 Date cree :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Cette classe est utilise pour la recherche d'un hostname
 * dans le fichier contenant l'information de celui-ci.
 * Si le hostname existe, l'adresse IP est retroune, sinon
 * l'absence de cette adresse est signale
 * @author Max
 */
	public class QueryFinder  {
	
	private String adresse = null; //hostname de l'adresse a chercher
	private String filename = null; //fichier ou effectuer la recherche
	
	private Scanner scanneurFichierSource = null;
	private String uneligne = null;
	private String[] hostnameFromFile = null;
	private String valueToReturn = null;
	
	/**
	 * Constructeur
	 * @param filename
	 * @param adresse
	 */
	public QueryFinder(String filename, String adresse){
		this.filename = filename;
		this.adresse = adresse;
	}
	
	/**
	 * Constructeur
	 * @param filename
	 */
	public QueryFinder(String filename){
		this.filename = filename;
	}
	
	public String getadresse(){
		return adresse;
	}
	
	/**
	 * Search un hostname et retourne une ip
	 * @param hostname = adresse dns a chercher
	 */
	public List<String> StartResearch(String hostname){
		List<String> adresslist = new ArrayList<>();
		
		try {
			scanneurFichierSource = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		//Test pour savoir si le fichier est vide
		//S'il n'y a pas de ligne apr�s le d�but du fichier (quand le curseur est avant le
		//d�but du fichier), le fichier est vide
		
		if(!scanneurFichierSource.hasNextLine()){
			System.out.println("Le fichier DNS est vide");
			scanneurFichierSource.close();
			return adresslist;
		}
		
		//prend une ligne
		uneligne = scanneurFichierSource.nextLine();
		hostnameFromFile = uneligne.split(" ");
		
		while( scanneurFichierSource.hasNextLine() ){
			uneligne = scanneurFichierSource.nextLine();
			hostnameFromFile = uneligne.split(" ");
			if(hostnameFromFile[0].equals(hostname)){
				adresslist.add(hostnameFromFile[1]);
			}
		}
		scanneurFichierSource.close();
		return adresslist;
	}
	
	/**
	 * Affiche l'ensemble du contenu du DNSFILE
	 */
	public void listCorrespondingTable(){	
		try {
			scanneurFichierSource = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		if(!scanneurFichierSource.hasNextLine()){
			System.out.println("La table est vide!");
			return;
		}
		
		while(scanneurFichierSource.hasNextLine()){
			uneligne = scanneurFichierSource.nextLine();
			System.out.println(uneligne);
		}
		scanneurFichierSource.close();
	}
}