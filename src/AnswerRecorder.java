
import java.io.FileWriter;
import java.io.IOException;

/***
 * Cette classe est utilise pour enregistrer une reponse
 * dans le fichier texte en provenance d'un Server DNS autre.
 * @author Max (aj98150)
 * Nettoyer pour eviter les erreurs de manipulation
 * @author lighta, Simon
 */
public class AnswerRecorder {
	private String filename = null; //path du fichier a utiliser

	/**
	 * Construteur
	 * @param filename : Nom du fichier pour sauvegarder les adressesIP et hostname
	 * 
	 */
	public AnswerRecorder(String filename){
		this.filename = filename;
		//TODO devrait check si on a le droit d'ecriture / lecture dans ce filename
	}
		
	/**
	 * @return file name
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param hostname : NS (ex google.com)
	 * @param adresseIP : Ip resolvant le NS
	 */
	public void StartRecord(String hostname,String adresseIP){
		if(adresseIP.length() < 7) { //@TODO add proper regex
			System.out.println("Invalid adresseIP to write ("+adresseIP+")");
			return; 	
		}
		try {
			FileWriter writerFichierSource = new FileWriter(filename,true);		
			writerFichierSource.write(hostname + " " + adresseIP);
			writerFichierSource.write("\r\n");
			writerFichierSource.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
