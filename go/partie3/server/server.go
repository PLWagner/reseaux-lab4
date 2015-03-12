package main

import (
	"flag"
	"fmt"
	"net"
	"os"
	"strings"
)

func main() {
	var port = flag.String("port", "", "Port Number")
	flag.Parse()

	if *port == "" {
		fmt.Println("You must specify --port")
		os.Exit(1)
	}

	addr, _ := net.ResolveUDPAddr("udp", ":"+*port)
	sock, _ := net.ListenUDP("udp", addr)

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
		fmt.Println("REQUEST pour: " + strings.Join(domainName, "."))

	} else { // ****** Dans le cas d'un paquet reponse *****
		fmt.Print("ELLO")
	}

}
