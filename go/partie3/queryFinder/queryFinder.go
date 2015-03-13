package main //queryFinder

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func check(e error) {
	if e != nil {
		panic(e)
	}
}

//Read the lines of the file
func startSearch(path string) {
	inFile, _ := os.Open(path)
	defer inFile.Close()
	scanner := bufio.NewScanner(inFile)
	scanner.Split(bufio.ScanLines)

	if scanner.Text() != "\n" {

		for scanner.Scan() {
			text := strings.Split(scanner.Text(), " ")
			//if scanner.Text() == "wololo" {
			fmt.Println(text[1])
			//fmt.Println(scanner.Text())
			//}
		}

	} else {

		fmt.Println("Le fichier DNS est vide")
	}

}

//Parser fichier
func main() {

	//Pour effectuer la recherche
	//Variables publiques
	//var Address string  //Variable publiques (Lettre majuscule en Go)
	//var Filename string //Variable publiques (Lettre majuscule en Go)

	startSearch("DNSFILE.TXT")
}
