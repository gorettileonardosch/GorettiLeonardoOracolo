/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gorettileonardooracolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OracoloServer {
    private static final String COMANDO_STORIA = "STORIA";
    private static final String COMANDO_ESCI = "ESCI";
    private static final String PREFISSO_PERSONA = "PERSONA_";
    private static final String PREFISSO_DOMANDA = "DOMANDA_";
    private static final Set<Socket> CLIENT_CONNESSI = ConcurrentHashMap.newKeySet();
    private static volatile boolean chiusuraRichiesta;

    // Frasi che l'Oracolo usa per predire il futuro.
    private static final String[] FRASI_ORACOLO = {
        "Vedo un cambiamento importante nel tuo futuro.",
        "Una nuova opportunità si presenterà presto.",
        "Il tuo cammino sarà chiaro se saprai avere pazienza."
    };

    // Frasi che la Chiromante usa quando legge i tarocchi.
    private static final String[] FRASI_CHIROMANTE = {
        "Le carte mostrano il Sole: ti aspettano giorni luminosi.",
        "La Ruota della Fortuna annuncia un cambiamento.",
        "L'Eremita ti consiglia di ascoltare te stesso."
    };

    // Frasi teatrali che il Mago accompagna con un piccolo effetto magico.
    private static final String[] FRASI_MAGO = {
        "Puff! Una scintilla magica annuncia un futuro luminoso.",
        "Le rune girano nell'aria: la risposta è forse!",
        "Dal fumo appare un indizio: riprova domani."
    };

    // Avvia il server e crea un thread separato per ogni client.
    public static void main(String[] args) {
        chiusuraRichiesta = false;
        try (ServerSocket server = new ServerSocket(GorettiLeonardoOracolo.PORTA)) {
            System.out.println("OracoloServer in ascolto sulla porta " + GorettiLeonardoOracolo.PORTA);

            while (true) {
                Socket client = server.accept();
                CLIENT_CONNESSI.add(client);
                System.out.println("Nuovo client: " + client.getRemoteSocketAddress());

                Thread thread = new Thread(new GestoreClient(client, server));
                thread.start();
            }
        } catch (IOException e) {
            if (!chiusuraRichiesta) {
                System.err.println("Impossibile avviare o mantenere attivo il server: " + e.getMessage());
            }
        } finally {
            System.out.println("OracoloServer terminato.");
        }
    }

    // Gestisce i comandi di un solo client.
    private static class GestoreClient implements Runnable {
        private final Socket client;
        private final ServerSocket server;
        private final Random casuale = new Random();
        private final List<String> domande = new ArrayList<>();
        private String persona = "Oracolo";

        GestoreClient(Socket client, ServerSocket server) {
            this.client = client;
            this.server = server;
        }

        @Override
        public void run() {
            try (
                Socket socket = client;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String linea;
                while ((linea = in.readLine()) != null) {
                    linea = linea.trim();

                    if (COMANDO_ESCI.equalsIgnoreCase(linea)) {
                        out.println("Server in chiusura.");
                        arrestaServer();
                        break;
                    }

                    if (COMANDO_STORIA.equalsIgnoreCase(linea)) {
                        if (domande.isEmpty()) {
                            out.println("Non hai ancora fatto domande in questa sessione.");
                        } else {
                            out.println("Ultime domande: " + String.join(" | ", domande));
                        }
                        continue;
                    }

                    if (haPrefisso(linea, PREFISSO_PERSONA)) {
                        String scelta = linea.substring(PREFISSO_PERSONA.length()).trim();
                        if ("Oracolo".equalsIgnoreCase(scelta)) {
                            persona = "Oracolo";
                        } else if ("Chiromante".equalsIgnoreCase(scelta)) {
                            persona = "Chiromante";
                        } else if ("Mago".equalsIgnoreCase(scelta)) {
                            persona = "Mago";
                        } else {
                            out.println("Personalità non valida. Scegli Oracolo, Chiromante o Mago.");
                            continue;
                        }
                        out.println("Hai scelto la personalità " + persona + ".");
                        continue;
                    }

                    if (haPrefisso(linea, PREFISSO_DOMANDA)) {
                        String domanda = linea.substring(PREFISSO_DOMANDA.length()).trim();
                        if (domanda.isEmpty()) {
                            out.println("Scrivi la domanda dopo DOMANDA_.");
                            continue;
                        }

                        domande.add(domanda);
                        if (domande.size() > 5) {
                            domande.remove(0);
                        }

                        out.println(persona + ": " + scegliRisposta());
                        continue;
                    }

                    out.println("Comando non riconosciuto.");
                }
            } catch (IOException e) {
                System.out.println("Connessione client terminata.");
            } finally {
                CLIENT_CONNESSI.remove(client);
            }
        }

        private void arrestaServer() {
            chiusuraRichiesta = true;
            try {
                server.close();
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura del server: " + e.getMessage());
            }

            for (Socket socket : CLIENT_CONNESSI) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Errore durante la chiusura di un client: " + e.getMessage());
                }
            }
        }

        private boolean haPrefisso(String linea, String prefisso) {
            return linea.regionMatches(true, 0, prefisso, 0, prefisso.length());
        }

        private String scegliRisposta() {
            String[] frasi;
            switch (persona) {
                case "Chiromante":
                    frasi = FRASI_CHIROMANTE;
                    break;
                case "Mago":
                    frasi = FRASI_MAGO;
                    break;
                default:
                    frasi = FRASI_ORACOLO;
                    break;
            }
            return frasi[casuale.nextInt(frasi.length)];
        }
    }
}
