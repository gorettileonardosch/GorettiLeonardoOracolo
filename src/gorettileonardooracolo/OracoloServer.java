/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gorettileonardooracolo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class OracoloServer {
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

				// Ogni client ha il proprio thread e la propria personalità.
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
		// La cronologia è dentro il gestore: ogni client ha la propria.
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
				out.println("Benvenuto! Digita HELP per vedere i comandi.");

				String linea;
				while ((linea = in.readLine()) != null) {
					linea = linea.trim();

					if (linea.equalsIgnoreCase("HELP")) {
						out.println("Comandi: PERSONALITA, PERSONA_<nome>, DOMANDA_<testo>, STORIA, HELP");
						out.println("Personalità disponibili: Oracolo, Chiromante, Mago.");
						continue;
					}

					if (linea.equalsIgnoreCase("PERSONALITA")) {
						out.println("Personalità disponibili: Oracolo, Chiromante, Mago. Scelta attuale: " + persona + ".");
						continue;
					}

					if (linea.equalsIgnoreCase("STORIA")) {
						if (domande.isEmpty()) {
							out.println("Non hai ancora fatto domande in questa sessione.");
						} else {
							out.println("Ultime domande: " + String.join(" | ", domande));
						}
						continue;
					}

					if (linea.equalsIgnoreCase(GorettiLeonardoOracolo.EXIT_PHRASE)) {
						out.println("Addio! Alla prossima.");
						break;
					}

					if (linea.regionMatches(true, 0, "PERSONA_", 0, "PERSONA_".length())) {
						String scelta = linea.substring("PERSONA_".length()).trim();
						if (scelta.equalsIgnoreCase("Oracolo")) {
							persona = "Oracolo";
						} else if (scelta.equalsIgnoreCase("Chiromante")) {
							persona = "Chiromante";
						} else if (scelta.equalsIgnoreCase("Mago")) {
							persona = "Mago";
						} else {
							out.println("Personalità non valida. Scegli Oracolo, Chiromante o Mago.");
							continue;
						}
						out.println("Hai scelto la personalità " + persona + ".");
						continue;
					}

					if (linea.regionMatches(true, 0, "DOMANDA_", 0, "DOMANDA_".length())) {
						String domanda = linea.substring("DOMANDA_".length()).trim();
						if (domanda.isEmpty()) {
							out.println("Scrivi la domanda dopo DOMANDA_.");
							continue;
						}

						// Conserva solo le cinque domande più recenti.
						domande.add(domanda);
						if (domande.size() > 5) {
							domande.remove(0);
						}

						String risposta = scegliRisposta();
						out.println(persona + ": " + risposta);
						continue;
					}

					out.println("Comando non riconosciuto. Digita HELP per assistenza.");
				}
			} catch (IOException e) {
				System.out.println("Connessione client terminata.");
			}
		}

		// Sceglie una frase dalla lista della personalità selezionata.
		private String scegliRisposta() {
			String[] frasi = FRASI_ORACOLO;

			if (persona.equals("Chiromante")) {
				frasi = FRASI_CHIROMANTE;
			} else if (persona.equals("Mago")) {
				frasi = FRASI_MAGO;
			}

			return frasi[casuale.nextInt(frasi.length)];
		}
	}
}
