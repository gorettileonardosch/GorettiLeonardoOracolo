package gorettileonardooracolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Qui tengo la connessione con il server e gli mando i comandi.
 */
public class OracoloClient {
    private final Consumer<String> mostraMessaggio;
    private Socket socket;
    private PrintWriter versoSocket;

    /**
     * Salvo come mostrare i messaggi e provo a collegarmi al server.
     */
    public OracoloClient(Consumer<String> mostraMessaggio) {
        this.mostraMessaggio = mostraMessaggio;
        connetti();
    }

    /**
     * Apro la connessione e avvio un thread che resta in ascolto delle risposte.
     */
    private void connetti() {
        try {
            socket = new Socket("localhost", GorettiLeonardoOracolo.PORTA);
            versoSocket = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader dalSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread ascolto = new Thread(new Runnable() {
                @Override
                public void run() {
                    ascoltaServer(dalSocket);
                }
            });
            // Questo thread si chiude da solo quando termina il programma.
            ascolto.setDaemon(true);
            ascolto.start();
        } catch (IOException ex) {
            chiudiSocketDopoErrore();
            mostraMessaggio.accept("Impossibile collegarsi al server: " + ex.getMessage());
        }
    }

    /**
     * Continuo a leggere le risposte finché il server resta collegato.
     */
    private void ascoltaServer(BufferedReader dalSocket) {
        try {
            String linea;
            while ((linea = dalSocket.readLine()) != null) {
                mostraMessaggio.accept(linea);
            }
            if (socket != null && !socket.isClosed()) {
                mostraMessaggio.accept("Connessione chiusa dal server.");
            }
        } catch (IOException ex) {
            if (socket != null && !socket.isClosed()) {
                mostraMessaggio.accept("Connessione chiusa: " + ex.getMessage());
            }
        }
    }

    /**
     * Mando un comando al server. synchronized evita che due invii si pestino i piedi.
     */
    public synchronized boolean inviaComando(String comando) {
        if (socket == null || socket.isClosed() || versoSocket == null) {
            mostraMessaggio.accept("Server non disponibile.");
            return false;
        }

        versoSocket.println(comando);
        // PrintWriter non segnala gli errori da solo, quindi controllo se l'invio è andato bene.
        if (versoSocket.checkError()) {
            mostraMessaggio.accept("Errore durante l'invio del comando al server.");
            return false;
        }
        return true;
    }

    /**
     * Chiudo la connessione, se è ancora aperta.
     */
    public void chiudiConnessione() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            mostraMessaggio.accept("Errore durante la chiusura della connessione: " + ex.getMessage());
        }
    }

    /**
     * Se qualcosa va storto mentre mi collego, provo comunque a chiudere il socket.
     */
    private void chiudiSocketDopoErrore() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            mostraMessaggio.accept("Errore durante la chiusura della connessione: " + ex.getMessage());
        }
    }

    /**
     * Avvio l'applicazione dal main principale.
     */
    public static void main(String[] args) {
        GorettiLeonardoOracolo.main(args);
    }
}
