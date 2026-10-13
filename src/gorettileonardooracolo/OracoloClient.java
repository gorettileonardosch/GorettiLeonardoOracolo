/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gorettileonardooracolo;

import java.net.Socket;

/**
 *
 * @author goretti.leonardo
 */
public class OracoloClient {
       public static void main(String[] args) throws Exception {
        // Facoltativo: indirizzo del server come argomento (default: localhost)
        String host = args.length > 0 ? args[0] : "localhost";
        Socket socket = new Socket(host, GorettiLeonardoOracolo.PORTA);
        System.out.println("Connesso a " + host + ":" + GorettiLeonardoOracolo.PORTA);
        GorettiLeonardoOracolo.avvia(socket); 
}
