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
    // Porta di default usata da server e client
    public static final int PORTA = 12345;
    // Frase speciale per terminare la sessione da parte del client
    // Se l'utente digita la frase qui sotto, il client invia la riga al server
    // e poi chiude la connessione locale (ovvero: "se l'utente digita la frase poi esce").
    public static final String EXIT_PHRASE = "grazie per la convocazione grande mago";

    // Metodo di utilità usato dal client per avviare una sessione interattiva
    // Riceve un Socket già connesso al server e gestisce due thread:
    // - uno che stampa le risposte arrivate dal server
    // - uno che legge dalla tastiera e invia al server
    public static void avvia(Socket socket) throws IOException {
        BufferedReader dalSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter versoSocket = new PrintWriter(socket.getOutputStream(), true); // true = autoflush con println
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

        // Thread che legge dal server e stampa sulla console
        Thread reader = new Thread(() -> {
            try {
                String linea;
                while ((linea = dalSocket.readLine()) != null) {
                    System.out.println("[SERVER] " + linea);
                }
            } catch (IOException ex) {
                System.out.println("Connessione chiusa dal server.");
            }
        });
        reader.setDaemon(true);
        reader.start();

        // Loop principale: leggere dalla tastiera e inviare al server
        // Istruzioni semplici per l'utente:
        // - Digita "HELP" per vedere i comandi disponibili
        // - Per terminare la sessione digita esattamente la frase:
        //   "grazie per la convocazione grande mago" (il client la invia e poi esce)
        System.out.println("Digitare comandi (HELP per aiuto).");
        System.out.println("Per uscire digita: " + EXIT_PHRASE);
        String input;
        while ((input = tastiera.readLine()) != null) {
            if (input.trim().isEmpty()) continue;
            // Se l'utente digita la frase magica, inviamo l'ultima riga e usciamo
            // (nota per l'utente: "se l'utente digita la frase poi esce").
            versoSocket.println(input);
            if (input.equalsIgnoreCase(EXIT_PHRASE)) {
                System.out.println("Frase di uscita rilevata, chiudo connessione...");
                break;
            }
        }

        // chiudere socket alla fine
        socket.close();
        System.out.println("Client terminato.");
    }

    public static void main(String[] args) {
        System.out.println("Usare OracoloServer o OracoloClient direttamente.");
    }

}
