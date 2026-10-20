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

    // Metodo di utilità semplice: per ogni riga letta dalla tastiera invia al server
    // e poi legge una risposta e la stampa. Se l'utente digita EXIT_PHRASE, il
    // client invia la riga e poi esce ("se l'utente digita la frase poi esce").
    public static void avvia(Socket socket) throws IOException {
        BufferedReader dalSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter versoSocket = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("HELP per i comandi. Per uscire digita: " + EXIT_PHRASE);
        // Il server manda il benvenuto appena ci colleghiamo, quindi lo leggiamo subito.
        String benvenuto = dalSocket.readLine();
        if (benvenuto != null) {
            System.out.println("SERVER: " + benvenuto);
        }

        String input;
        while ((input = tastiera.readLine()) != null) {
            input = input.trim();
            if (input.isEmpty()) continue;
            // invia e aspetta una risposta (semplice, bloccante)
            versoSocket.println(input);
            // Se abbiamo inviato la frase di uscita, terminiamo subito dopo l'invio
            if (input.equalsIgnoreCase(EXIT_PHRASE)) {
                String saluto = dalSocket.readLine();
                if (saluto != null) {
                    System.out.println("SERVER: " + saluto);
                }
                System.out.println("Frase di uscita inviata, chiudo.");
                break;
            }
            // leggiamo una singola linea di risposta dal server e la mostriamo
            String risposta = dalSocket.readLine();
            if (risposta == null) {
                System.out.println("Server ha chiuso la connessione.");
                break;
            }
            System.out.println("SERVER: " + risposta);
        }

        socket.close();
        System.out.println("Client terminato.");
    }

    public static void main(String[] args) {
        System.out.println("Usare OracoloServer o OracoloClient direttamente.");
    }

}
