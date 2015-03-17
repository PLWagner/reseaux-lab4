package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// QueryFinder is used to find hosts in a Text file
type QueryFinder struct {
	dnsFilePath string
}

// NewQueryFinder returns a new QueryFinder
func NewQueryFinder(DNSFilePath string) *QueryFinder {
	q := &QueryFinder{DNSFilePath}
	return q
}

//SearchHost searches the lines of the file for the hostname
func (q *QueryFinder) SearchHost(hostname string) string {
	inFile, _ := os.Open(q.dnsFilePath)
	defer inFile.Close()
	scanner := bufio.NewScanner(inFile)
	scanner.Split(bufio.ScanLines)

	for scanner.Scan() {
		text := strings.Split(scanner.Text(), " ")
		if text[0] == hostname {
			return text[1]
		}
	}
	return ""
}

//Dump prints the DNS table
func (q *QueryFinder) Dump() string {
	inFile, _ := os.Open(q.dnsFilePath)
	defer inFile.Close()
	scanner := bufio.NewScanner(inFile)
	scanner.Split(bufio.ScanLines)

	for scanner.Scan() {
		text := strings.Split(scanner.Text(), " ")
		fmt.Println(text[0], text[1])
	}
	return ""
}
