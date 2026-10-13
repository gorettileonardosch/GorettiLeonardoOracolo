/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gorettileonardooracolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author goretti.leonardo
 */
public class GorettiLeonardoOracolo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
          public static final int PORTA = 12345;

    public static void avvia(Socket socket) throws IOException, InterruptedException {
        BufferedReader dalSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter versoSocket = new PrintWriter(socket.getOutputStream(), true); // true = autoflush con println
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));
    }
    
}
