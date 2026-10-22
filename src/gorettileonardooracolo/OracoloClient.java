package gorettileonardooracolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class OracoloClient {
    private final Consumer<String> mostraMessaggio;
    private Socket socket;
    private PrintWriter versoSocket;

    public OracoloClient(Consumer<String> mostraMessaggio) {
        this.mostraMessaggio = mostraMessaggio;
        connetti();
    }

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
            ascolto.setDaemon(true);
            ascolto.start();
        } catch (IOException ex) {
            chiudiSocketDopoErrore();
            mostraMessaggio.accept("Impossibile collegarsi al server: " + ex.getMessage());
        }
    }

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

    public synchronized boolean inviaComando(String comando) {
        if (socket == null || socket.isClosed() || versoSocket == null) {
            mostraMessaggio.accept("Server non disponibile.");
            return false;
        }

        versoSocket.println(comando);
        if (versoSocket.checkError()) {
            mostraMessaggio.accept("Errore durante l'invio del comando al server.");
            return false;
        }
        return true;
    }

    public void chiudiConnessione() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            mostraMessaggio.accept("Errore durante la chiusura della connessione: " + ex.getMessage());
        }
    }

    private void chiudiSocketDopoErrore() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            mostraMessaggio.accept("Errore durante la chiusura della connessione: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        GorettiLeonardoOracolo.main(args);
    }
}
