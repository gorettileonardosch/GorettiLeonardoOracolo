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

public class OracoloServer {
    private static final String COMANDO_STORIA = "STORIA";
    private static final String PREFISSO_PERSONA = "PERSONA_";
    private static final String PREFISSO_DOMANDA = "DOMANDA_";

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
        try (ServerSocket server = new ServerSocket(GorettiLeonardoOracolo.PORTA)) {
            System.out.println("OracoloServer in ascolto sulla porta " + GorettiLeonardoOracolo.PORTA);

            while (true) {
                Socket client = server.accept();
                System.out.println("Nuovo client: " + client.getRemoteSocketAddress());

                Thread thread = new Thread(new GestoreClient(client));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Impossibile avviare il server: " + e.getMessage());
        }
    }

    // Gestisce i comandi di un solo client.
    private static class GestoreClient implements Runnable {
        private final Socket client;
        private final Random casuale = new Random();
        private final List<String> domande = new ArrayList<>();
        private String persona = "Oracolo";

        GestoreClient(Socket client) {
            this.client = client;
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
