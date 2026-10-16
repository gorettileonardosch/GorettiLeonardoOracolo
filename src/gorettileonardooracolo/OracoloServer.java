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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author goretti.leonardo
 */
public class OracoloServer {
	// Thread pool per gestire client contemporanei
	private final ExecutorService pool = Executors.newCachedThreadPool();

	// Risposte di esempio che l'oracolo può usare per DOMANDA_
	private static final String[] RISPOSTE = new String[]{
		"Sì.",
		"No.",
		"Forse, prova a chiedere in un altro momento.",
		"Assolutamente.",
		"Non posso dirlo ora.",
		"Segni indicano che è probabile."
	};

	// Frase speciale che, se inviata dal client, chiude la sessione per quel client
	// Frase speciale che, se inviata dal client, chiude la sessione per quel client
	// Nota per l'utente: se il client invia questa frase, il server risponde con
	// un saluto e chiude la connessione per quel client (in pratica: "se l'utente digita la frase poi esce").
	private static final String EXIT_PHRASE = "grazie per la convocazione grande mago";

	// Avvia il server e rimane in ascolto sulla porta definita in GorettiLeonardoOracolo.PORTA
	public void start() throws IOException {
		try (ServerSocket server = new ServerSocket(GorettiLeonardoOracolo.PORTA)) {
			System.out.println("OracoloServer in ascolto sulla porta " + GorettiLeonardoOracolo.PORTA);
			while (true) {
				Socket client = server.accept();
				System.out.println("Nuovo client connesso: " + client.getRemoteSocketAddress());
				pool.submit(new ClientHandler(client));
			}
		}
	}

	// Classe che gestisce un singolo client
	private static class ClientHandler implements Runnable {
		private final Socket socket;
		private String persona = "Default"; // personalità corrente per questo client
		private final Random rand = new Random();

		ClientHandler(Socket s) {
			this.socket = s;
		}

		@Override
		public void run() {
			try (
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			) {
				out.println("Benvenuto nell'Oracolo! Digita HELP per i comandi disponibili.");
				String linea;
				while ((linea = in.readLine()) != null) {
					linea = linea.trim();
					if (linea.isEmpty()) continue;
					// Gestione comandi di base
					if (linea.equalsIgnoreCase("HELP")) {
						out.println("Comandi: PERSONA_<nome>, DOMANDA_<testo>, HELP");
						continue;
					}
					// Se il client invia la frase di uscita, salutiamo e chiudiamo la connessione
					if (linea.equalsIgnoreCase(EXIT_PHRASE)) {
						out.println("Addio! Connessione terminata dall'utente.");
						break;
					}
					if (linea.startsWith("PERSONA_")) {
						persona = linea.substring("PERSONA_".length()).trim();
						if (persona.isEmpty()) persona = "Anonimo";
						out.println("OK: personalita' impostata su '" + persona + "'");
						continue;
					}
					if (linea.startsWith("DOMANDA_")) {
						String domanda = linea.substring("DOMANDA_".length()).trim();
						// Logica molto semplice: scegli una risposta casuale e includi la persona
						String risposta = generaRisposta(domanda);
						out.println("Risposta (" + persona + "): " + risposta);
						continue;
					}
					// Comando non riconosciuto
					out.println("Comando non riconosciuto. Digita HELP per assistenza.");
				}
			} catch (IOException ex) {
				System.out.println("ClientHandler: errore I/O: " + ex.getMessage());
			} finally {
				try { socket.close(); } catch (IOException ignore) {}
				System.out.println("Client disconnesso: " + socket.getRemoteSocketAddress());
			}
		}

		// Metodo semplice che genera una risposta casuale; in futuro potremo integrare logiche più complesse
		private String generaRisposta(String domanda) {
			// Per ora ignoriamo il contenuto della domanda e rispondiamo con una frase presa dall'array
			return RISPOSTE[rand.nextInt(RISPOSTE.length)];
		}
	}

	// Entry point per eseguire il server dalla riga di comando
	public static void main(String[] args) {
		OracoloServer server = new OracoloServer();
		try {
			server.start();
		} catch (IOException ex) {
			System.err.println("Impossibile avviare il server: " + ex.getMessage());
		}
	}
}
