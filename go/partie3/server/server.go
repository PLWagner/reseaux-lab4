package main

import (
	"flag"
	"fmt"
	"net"
	"os"
	"strings"
)

var redirectionSeulement bool
var dnsFile string

func main() {
	var port = flag.String("port", "", "Port Number")
	var redirectOnlyFlag = flag.Bool("redirectionSeulement", false, "Should the server only redirect")
	var dnsFileFlag = flag.String("DNSFile", "", "DNS file path")
	flag.Parse()

	// Parse and init the arguments
	if *port == "" {
		fmt.Println("You must specify --port")
		os.Exit(1)
	}

	if *dnsFileFlag == "" {
		fmt.Println("You must specify --DNSFile")
		os.Exit(1)
	}

	redirectionSeulement = *redirectOnlyFlag

	// Create the Socket
	addr, _ := net.ResolveUDPAddr("udp", ":"+*port)
	sock, _ := net.ListenUDP("udp", addr)

	// Main UDP loop
	for {
		buf := make([]byte, 1024)
		rlen, _, err := sock.ReadFromUDP(buf)
		if err != nil {
			fmt.Println(err)
		}
		go handlePacket(buf[0:rlen])
	}
}

func handlePacket(packet []byte) {

	// Lecture de QR
	QR := packet[3]

	if QR == 0 { // ****** Dans le cas d'un paquet requete *****

		// *Lecture du Query Domain name, a partir du 13 byte
		qnameIndex := 12
		var domainName []string
		for {
			//Break if 0 flag is found
			if packet[qnameIndex] == 0 {
				break
			}

			domainFieldLenght := int(packet[qnameIndex])
			domainName = append(domainName, string(packet[qnameIndex+1:qnameIndex+1+domainFieldLenght]))
			qnameIndex += domainFieldLenght + 1
		}
		QName := strings.Join(domainName, ".")
		fmt.Println("REQUEST QNAME: " + QName)

		// *Si le mode est redirection seulement
		if redirectionSeulement {
			// *Rediriger le paquet vers le serveur DNS
		} else {
			// *Sinon
			// *Rechercher l'adresse IP associe au Query Domain name dans le fichier de correspondance de ce serveur
		}

	} else { // ****** Dans le cas d'un paquet reponse *****
		fmt.Print("ELLO")
	}

}
