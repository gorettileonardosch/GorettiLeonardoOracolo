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

/**
 *
 * @author goretti.leonardo
 */
public class OracoloServer {
	// Risposte di esempio che l'oracolo può usare per DOMANDA_
	private static final String[] RISPOSTE = new String[]{
		"Sì.",
		"No.",
		"Forse, prova a chiedere in un altro momento.",
		"Assolutamente.",
		"Non posso dirlo ora.",
		"Segni indicano che è probabile."
	};

	// Frasi che userà la personalità Oracolo per predire il futuro.
	private static final String[] FRASI_ORACOLO = {
		"Vedo un cambiamento importante nel tuo futuro.",
		"Una nuova opportunità si presenterà presto.",
		"Il tuo cammino sarà chiaro se saprai avere pazienza."
	};

	// Frasi che userà la personalità Chiromante parlando di tarocchi.
	private static final String[] FRASI_CHIROMANTE = {
		"Le carte mostrano il Sole: ti aspettano giorni luminosi.",
		"La Ruota della Fortuna annuncia un cambiamento.",
		"L'Eremita ti consiglia di fermarti e ascoltare te stesso."
	};

	// Il Mago risponde in modo teatrale, con un piccolo effetto magico.
	private static final String[] FRASI_MAGO = {
		"Una scintilla illumina la risposta: sì, osa! ✨",
		"Le rune girano nell'aria... la risposta è forse!",
		"Puff! Dal fumo appare un indizio: riprova domani."
	};

	// Frase speciale che, se inviata dal client, chiude la sessione per quel client
	// Se il client invia questa frase, il server risponde e chiude la connessione per quel client.
	private static final String EXIT_PHRASE = "grazie per la convocazione grande mago";

	// Entry point: server semplice, gestisce un client alla volta in modo bloccante.
	public static void main(String[] args) {
		Random rand = new Random();
		try (ServerSocket server = new ServerSocket(GorettiLeonardoOracolo.PORTA)) {
			System.out.println("OracoloServer in ascolto sulla porta " + GorettiLeonardoOracolo.PORTA);
			while (true) {
				try (Socket client = server.accept();
					 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
					System.out.println("Nuovo client: " + client.getRemoteSocketAddress());
					out.println("Benvenuto nell'Oracolo! Digita HELP per i comandi disponibili.");

					String persona = "Default";
					String linea;
					while ((linea = in.readLine()) != null) {
						linea = linea.trim();
						if (linea.isEmpty()) continue;
						if (linea.equalsIgnoreCase("HELP")) {
							out.println("Comandi: PERSONA_<nome>, DOMANDA_<testo>, HELP");
							continue;
						}
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
							// risposta semplice: scegli casuale
							String risposta = RISPOSTE[rand.nextInt(RISPOSTE.length)];
							out.println("Risposta (" + persona + "): " + risposta);
							continue;
						}
						out.println("Comando non riconosciuto. Digita HELP per assistenza.");
					}
					System.out.println("Client chiuso: " + client.getRemoteSocketAddress());
				} catch (IOException e) {
					System.err.println("Errore gestione client: " + e.getMessage());
				}
			}
		} catch (IOException ex) {
			System.err.println("Impossibile avviare il server: " + ex.getMessage());
		}
	}
}
