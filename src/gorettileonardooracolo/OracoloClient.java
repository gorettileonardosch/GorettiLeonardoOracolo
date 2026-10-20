/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gorettileonardooracolo;

import java.net.Socket;
import java.io.IOException;

/**
 *
 * @author goretti.leonardo
 */
public class OracoloClient {
        public static void main(String[] args) throws Exception {
                // versione molto semplice: si connette e usa avvia() per la sessione
                String host = args.length > 0 ? args[0] : "localhost";
                System.out.println("OracoloClient: connessione a " + host + ":" + GorettiLeonardoOracolo.PORTA);
                try (Socket socket = new Socket(host, GorettiLeonardoOracolo.PORTA)) {
                        GorettiLeonardoOracolo.avvia(socket);
                } catch (IOException ex) {
                        System.err.println("Impossibile connettersi al server: " + ex.getMessage());
                }
        }
}
