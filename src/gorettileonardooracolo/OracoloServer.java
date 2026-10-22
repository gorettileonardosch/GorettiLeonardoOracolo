package gorettileonardooracolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class OracoloServer {
    private static final String COMANDO_STORIA = "STORIA";
    private static final String COMANDO_ESCI = "ESCI";
    private static final String PREFISSO_PERSONA = "PERSONA_";
    private static final String PREFISSO_DOMANDA = "DOMANDA_";
    private static final List<Socket> CLIENT_CONNESSI =
            Collections.synchronizedList(new ArrayList<>());
    private static volatile boolean chiusuraRichiesta;

    private static final String[] FRASI_ORACOLO = {
        "Vedo un cambiamento importante nel tuo futuro.",
        "Una nuova opportunità si presenterà presto.",
        "Il tuo cammino sarà chiaro se saprai avere pazienza."
    };

    private static final String[] FRASI_CHIROMANTE = {
        "Le carte mostrano il Sole: ti aspettano giorni luminosi.",
        "La Ruota della Fortuna annuncia un cambiamento.",
        "L'Eremita ti consiglia di ascoltare te stesso."
    };

    private static final String[] FRASI_MAGO = {
        "Puff! Una scintilla magica annuncia un futuro luminoso.",
        "Le rune girano nell'aria: la risposta è forse!",
        "Dal fumo appare un indizio: riprova domani."
    };

    public static void main(String[] args) {
        chiusuraRichiesta = false;

        try (ServerSocket server = new ServerSocket(GorettiLeonardoOracolo.PORTA)) {
            System.out.println("OracoloServer in ascolto sulla porta " + GorettiLeonardoOracolo.PORTA);

            while (true) {
                Socket client = server.accept();
                CLIENT_CONNESSI.add(client);
                System.out.println("Nuovo client: " + client.getRemoteSocketAddress());

                GestoreClient gestore = new GestoreClient(client, server);
                Thread thread = new Thread(gestore);
                thread.start();
            }
        } catch (IOException e) {
            if (!chiusuraRichiesta) {
                System.err.println("Impossibile avviare o mantenere attivo il server: " + e.getMessage());
            }
        }

        System.out.println("OracoloServer terminato.");
    }

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
                    String comandoMaiuscolo = linea.toUpperCase(Locale.ROOT);

                    if (COMANDO_ESCI.equals(comandoMaiuscolo)) {
                        out.println("Server in chiusura.");
                        chiudiServer();
                        break;
                    }

                    if (COMANDO_STORIA.equals(comandoMaiuscolo)) {
                        mostraCronologia(out);
                    } else if (comandoMaiuscolo.startsWith(PREFISSO_PERSONA)) {
                        cambiaPersona(linea.substring(PREFISSO_PERSONA.length()), out);
                    } else if (comandoMaiuscolo.startsWith(PREFISSO_DOMANDA)) {
                        rispondi(linea.substring(PREFISSO_DOMANDA.length()), out);
                    } else {
                        out.println("Comando non riconosciuto.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Connessione client terminata.");
            } finally {
                CLIENT_CONNESSI.remove(client);
            }
        }

        private void mostraCronologia(PrintWriter out) {
            if (domande.isEmpty()) {
                out.println("Non hai ancora fatto domande in questa sessione.");
            } else {
                out.println("Ultime domande: " + String.join(" | ", domande));
            }
        }

        private void cambiaPersona(String scelta, PrintWriter out) {
            scelta = scelta.trim();

            if ("Oracolo".equalsIgnoreCase(scelta)) {
                persona = "Oracolo";
            } else if ("Chiromante".equalsIgnoreCase(scelta)) {
                persona = "Chiromante";
            } else if ("Mago".equalsIgnoreCase(scelta)) {
                persona = "Mago";
            } else {
                out.println("Personalità non valida. Scegli Oracolo, Chiromante o Mago.");
                return;
            }

            out.println("Hai scelto la personalità " + persona + ".");
        }

        private void rispondi(String domanda, PrintWriter out) {
            domanda = domanda.trim();
            if (domanda.isEmpty()) {
                out.println("Scrivi la domanda dopo DOMANDA_.");
                return;
            }

            domande.add(domanda);
            if (domande.size() > 5) {
                domande.remove(0);
            }

            out.println(persona + ": " + scegliRisposta());
        }

        private void chiudiServer() {
            chiusuraRichiesta = true;
            try {
                server.close();
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura del server: " + e.getMessage());
            }

            synchronized (CLIENT_CONNESSI) {
                for (Socket socket : CLIENT_CONNESSI) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Errore durante la chiusura di un client: " + e.getMessage());
                    }
                }
            }
        }

        private String scegliRisposta() {
            String[] frasi = FRASI_ORACOLO;

            if ("Chiromante".equals(persona)) {
                frasi = FRASI_CHIROMANTE;
            } else if ("Mago".equals(persona)) {
                frasi = FRASI_MAGO;
            }

            int indice = casuale.nextInt(frasi.length);
            return frasi[indice];
        }
    }
}
