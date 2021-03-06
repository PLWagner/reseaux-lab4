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

	var port = flag.String("port", "", "Port Number")
	flag.Parse()

	if *port == "" {
		fmt.Println("You must specify --port")
		os.Exit(1)
	}

	ln, err := net.Listen("tcp", ":"+*port)
	if err != nil {
		println("Listen failed:", err.Error())
		os.Exit(1)
	}

	for {
		con, err := ln.Accept()
		if err != nil {
			println("Could not accept:", err.Error())
		}
		go echo(con)
	}

}

func echo(conn net.Conn) {
	connbuf := bufio.NewReader(conn)
	for {
		str, err := connbuf.ReadString('\n')
		if len(str) > 0 {
			fmt.Print(strings.TrimSpace(str) + " (from: " + conn.RemoteAddr().String() + ")\n")
			_, err = conn.Write([]byte(strings.ToUpper(str)))
		}
		if err != nil {
			break
		}
	}
}
