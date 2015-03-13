package main

import (
	"flag"
	"fmt"
	"net"
	"os"
	"strconv"
	"strings"

	"github.com/PLWagner/reseaux-lab4/go/partie3/queryFinder"
)

var redirectionSeulement bool
var qFinder queryFinder.QueryFinder

// Those are the queries we have no answer for yet
var waitingQueries map[string][2]string

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

	// Create a QueryFinder with the dnsFile
	qFinder = *queryFinder.NewQueryFinder(*dnsFileFlag)

	redirectionSeulement = *redirectOnlyFlag

	waitingQueries = make(map[string][2]string)

	// Create the Socket
	addr, _ := net.ResolveUDPAddr("udp", ":"+*port)
	sock, _ := net.ListenUDP("udp", addr)

	// Main UDP loop
	for {
		buf := make([]byte, 1024)
		rlen, sourceAddr, err := sock.ReadFromUDP(buf)
		if err != nil {
			fmt.Println(err)
		}
		go handlePacket(sock, sourceAddr, buf[0:rlen])
	}
}

func handlePacket(conn *net.UDPConn, sourceAddr *net.UDPAddr, packet []byte) {
	// Lecture de QR
	QR := packet[3]

	ID := packet[1:2]
	fmt.Println("Received request id:", ID)

	if QR == 0 { // ****** Dans le cas d'un paquet requete *****
		// *Lecture du Query Domain name, a partir du 13 byte
		qnameIndex := 12
		var domainName []string
		for {
			if packet[qnameIndex] == 0 {
				break //Break if 0 flag is found
			}
			domainFieldLenght := int(packet[qnameIndex])
			domainName = append(domainName, string(packet[qnameIndex+1:qnameIndex+1+domainFieldLenght]))
			qnameIndex += domainFieldLenght + 1
		}
		QName := strings.Join(domainName, ".")
		fmt.Println("REQUEST QNAME: " + QName)

		var ip string
		if !redirectionSeulement {
			ip = qFinder.SearchHost(QName) //Seulement regarder le ficher si on est pas en mode redirection
		}
		if ip != "" {
			fmt.Println("Found in .TXT ", ip)
			//TODO: answer the query
		} else {
			forwardPacket("8.8.8.8", "53", conn, packet)
			waitingQueries[string(ID)] = [2]string{sourceAddr.IP.String(), strconv.Itoa(sourceAddr.Port)}
		}

	} else { // ****** Dans le cas d'un paquet reponse *****
		fmt.Println("Received answer for : ", ID)
		//Find the id
		waitingAddr := waitingQueries[string(ID)]
		forwardPacket(waitingAddr[0], waitingAddr[1], conn, packet)
		delete(waitingQueries, string(ID))
	}

}

func forwardPacket(hostname, port string, conn *net.UDPConn, packet []byte) {
	fmt.Println("Forwarding to", hostname, ":", port)
	serverAddr, _ := net.ResolveUDPAddr("udp", hostname+":"+port)
	conn.WriteToUDP(packet, serverAddr)
}
