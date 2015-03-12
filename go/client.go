package main

import (
	"bufio"
	"flag"
	"fmt"
	"net"
	"os"
	"strings"
)

func main() {

	var ip = flag.String("ip", "", "IP address")
	var port = flag.String("port", "", "Port Number")
	flag.Parse()

	if *ip == "" || *port == "" {
		fmt.Println("You must specify --ip and --port")
		os.Exit(1)
	}

	tcpAddr, err := net.ResolveTCPAddr("tcp", *ip+":"+*port)
	if err != nil {
		println("ResolveTCPAddr failed:", err.Error())
		os.Exit(1)
	}

	conn, err := net.DialTCP("tcp", nil, tcpAddr)
	if err != nil {
		println("Dial failed:", err.Error())
		os.Exit(1)
	}

	reader := bufio.NewReader(os.Stdin)
	for {
		fmt.Println("Enter line:")
		line, _ := reader.ReadString('\n')
		fmt.Println("Sending: \"" + strings.TrimSpace(line) + "\"")
		_, err = conn.Write([]byte(line))
	}

}
