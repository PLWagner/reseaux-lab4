package main

//sudo go run queryFinder.go server.go --default
//sudo go run queryFinder.go server.go --port 53 --forwardAddress 8.8.8.8 --DNSFile ./DNSFILE.TXT --redirectionSeulement
//sudo go run queryFinder.go server.go --DNSFile ./DNSFILE.TXT showtable

import (
	"flag"
	"fmt"
	"net"
	"os"
	"strconv"
	"strings"
)

var redirectionSeulement bool
var qFinder *QueryFinder
var forwardAddress string

// Those are the queries we have no answer for yet
var waitingQueries map[string][2]string

func main() {
	var port = flag.String("port", "", "Port Number")
	var redirectOnlyFlag = flag.Bool("redirectionSeulement", false, "Should the server only redirect")
	var dnsFileFlag = flag.String("DNSFile", "", "DNS file path")
	var defaultFlag = flag.Bool("default", false, "DNS file path")
	var forwardFlag = flag.String("forwardAddress", "8.8.8.8", "Address to forward to")
	flag.Parse()

	if flag.Arg(0) == "showtable" {
		qFinder = NewQueryFinder(*dnsFileFlag)
		fmt.Println("DNS TABLE:")
		qFinder.Dump()
		os.Exit(0)
	}

	if *defaultFlag {
		*port = "53"
		redirectionSeulement = true
		forwardAddress = "8.8.8.8"
	} else {
		// Parse and init the arguments
		if *port == "" {
			fmt.Println("You must specify --port")
			os.Exit(1)
		}

		redirectionSeulement = *redirectOnlyFlag

		if !redirectionSeulement && *dnsFileFlag == "" {
			fmt.Println("You must specify --DNSFile")
			os.Exit(1)
		}

		forwardAddress = *forwardFlag
	}

	if !redirectionSeulement {
		// Create a QueryFinder with the dnsFile
		qFinder = NewQueryFinder(*dnsFileFlag)
	}

	waitingQueries = make(map[string][2]string)

	// Create the Socket
	addr, _ := net.ResolveUDPAddr("udp", ":"+*port)
	sock, _ := net.ListenUDP("udp", addr)

	fmt.Println("Starting server on port", *port)

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
			var answer []byte

			//HEADER: ID
			answer = append(answer, packet[1])
			answer = append(answer, packet[2])

			//HEADER: QR (3e byte)
			answer = append(answer, 1<<uint(7))

			packet[3] = 1 << uint(7)

			forwardPacket(sourceAddr.IP.String(), strconv.Itoa(sourceAddr.Port), conn, packet)

			fmt.Print("BYTES:", answer)
		} else {
			forwardPacket(forwardAddress, "53", conn, packet)
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

//dnsMsgHdr is a DNS query reply packet
type dnsHeader struct {
	Id                                 uint16
	Bits                               uint16
	Qdcount, Ancount, Nscount, Arcount uint16
}
