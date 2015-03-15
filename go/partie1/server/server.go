package main

import (
	"bufio"
	"flag"
	"fmt"
	"net"
	"os"
)

func main() {

	var port = flag.String("port", "", "Port Number")
	flag.Parse()

	if *port == "" {
		fmt.Println("You must specify --port")
		os.Exit(1)
	}

	for {

		ln, err := net.Listen("tcp", ":"+*port)
		if err != nil {
			println("Listen failed:", err.Error())
			os.Exit(1)
		}

		con, err := ln.Accept()
		if err != nil {
			println("Could not accept:", err.Error())
		}
		ln.Close()

		connbuf := bufio.NewReader(con)
		for {
			str, err := connbuf.ReadString('\n')
			if len(str) > 0 {
				fmt.Print(str)
			}
			if err != nil {
				break
			}
		}
	}

}
