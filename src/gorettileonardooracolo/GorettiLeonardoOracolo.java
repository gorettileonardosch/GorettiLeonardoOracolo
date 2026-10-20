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

    // Mostra un menu e traduce le scelte in comandi per il server.
    public static void avvia(Socket socket) throws IOException {
        BufferedReader dalSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter versoSocket = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

        String benvenuto = dalSocket.readLine();
        if (benvenuto != null) {
            System.out.println(benvenuto);
        }

        boolean continua = true;
        while (continua) {
            System.out.println();
            System.out.println("1. Fai una domanda");
            System.out.println("2. Scegli la personalità");
            System.out.println("3. Vedi le ultime domande");
            System.out.println("4. Vedi la personalità attiva");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            String scelta = tastiera.readLine();
            if (scelta == null) {
                break;
            }

            if (scelta.equals("1")) {
                System.out.print("Scrivi la tua domanda: ");
                String domanda = tastiera.readLine();
                if (domanda == null) {
                    break;
                }
                if (domanda.trim().isEmpty()) {
                    System.out.println("La domanda non può essere vuota.");
                } else if (!mandaComando(versoSocket, dalSocket, "DOMANDA_" + domanda.trim())) {
                    break;
                }
            } else if (scelta.equals("2")) {
                System.out.println("Scegli una personalità:");
                System.out.println("1. Oracolo");
                System.out.println("2. Chiromante");
                System.out.println("3. Mago");
                System.out.print("Scelta: ");

                String personalita = tastiera.readLine();
                String comando = null;
                if ("1".equals(personalita)) {
                    comando = "PERSONA_Oracolo";
                } else if ("2".equals(personalita)) {
                    comando = "PERSONA_Chiromante";
                } else if ("3".equals(personalita)) {
                    comando = "PERSONA_Mago";
                }

                if (comando == null) {
                    System.out.println("Scelta non valida.");
                } else if (!mandaComando(versoSocket, dalSocket, comando)) {
                    break;
                }
            } else if (scelta.equals("3")) {
                if (!mandaComando(versoSocket, dalSocket, "STORIA")) {
                    break;
                }
            } else if (scelta.equals("4")) {
                if (!mandaComando(versoSocket, dalSocket, "PERSONALITA")) {
                    break;
                }
            } else if (scelta.equals("0")) {
                mandaComando(versoSocket, dalSocket, EXIT_PHRASE);
                break;
            } else {
                System.out.println("Scelta non valida. Scegli uno dei numeri nel menu.");
            }
        }

        socket.close();
        System.out.println("Client terminato.");
    }

    // Invia un comando al server e mostra la risposta ricevuta.
    private static boolean mandaComando(PrintWriter versoSocket, BufferedReader dalSocket, String comando) throws IOException {
        versoSocket.println(comando);
        String risposta = dalSocket.readLine();
        if (risposta == null) {
            System.out.println("Il server ha chiuso la connessione.");
            return false;
        }
        System.out.println(risposta);
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Usare OracoloServer o OracoloClient direttamente.");
    }

}
