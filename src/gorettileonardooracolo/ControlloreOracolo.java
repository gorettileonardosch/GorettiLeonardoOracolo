package gorettileonardooracolo;

import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 * Qui gestisco le scelte del menu e mando i comandi al server.
 */
public class ControlloreOracolo {
    // Uso queste due azioni per scrivere nella chat e chiudere la finestra.
    private final Consumer<String> mostraMessaggio;
    private final Runnable chiudiFinestra;
    private final OracoloClient client;

    // Mi servono per ricordare la personalità scelta e se sto aspettando una risposta.
    private String personalitaSelezionata = "Oracolo";
    private boolean inserimentoDomanda;
    private boolean inAttesaRisposta;

    /**
     * Avvio il client e faccio comparire il menu iniziale.
     */
    public ControlloreOracolo(Consumer<String> mostraMessaggio, Runnable chiudiFinestra) {
        this.mostraMessaggio = mostraMessaggio;
        this.chiudiFinestra = chiudiFinestra;
        client = new OracoloClient(this::riceviMessaggioServer);
        mostraMenu();
    }

    /**
     * Quando scelgo un'altra personalità, lo dico anche al server.
     */
    public void selezionaPersonalita(String personalita) {
        if (personalita == null || personalita.equalsIgnoreCase(personalitaSelezionata)) {
            return;
        }

        personalitaSelezionata = personalita;
        inserimentoDomanda = false;
        inviaComando("PERSONA_" + personalita);
    }

    /**
     * Controllo se quello che ho scritto è una domanda o una scelta del menu.
     */
    public void inviaTesto(String testo) {
        testo = testo.trim();
        if (testo.isEmpty()) {
            return;
        }

        if (inserimentoDomanda) {
            inviaComando("DOMANDA_" + testo);
            mostraMessaggio.accept("Tu: " + testo);
            inserimentoDomanda = false;
            return;
        }

        gestisciSceltaMenu(testo);
    }

    /**
     * Qui decido cosa fare in base al numero che ho inserito.
     */
    private void gestisciSceltaMenu(String scelta) {
        switch (scelta) {
            case "1":
                mostraMessaggio.accept("Scrivi la tua domanda:");
                inserimentoDomanda = true;
                break;
            case "2":
                mostraMessaggio.accept("Recupero ultime domande...");
                inviaComando("STORIA");
                break;
            case "0":
                mostraMessaggio.accept("Chiusura del client e del server...");
                inviaComando("ESCI");
                client.chiudiConnessione();
                chiudiFinestra.run();
                break;
            default:
                mostraMessaggio.accept("Scelta non valida. Scegli uno dei numeri del menu.");
                mostraMenu();
                break;
        }
    }

    /**
     * Mando il comando e segno se devo aspettare la risposta del server.
     */
    private void inviaComando(String comando) {
        inAttesaRisposta = client.inviaComando(comando);
    }

    /**
     * Quando arriva una risposta, la faccio vedere nella chat e poi ristampo il menu.
     */
    private void riceviMessaggioServer(String messaggio) {
        // Swing vuole che gli aggiornamenti della finestra passino dal suo thread.
        SwingUtilities.invokeLater(() -> {
            mostraMessaggio.accept(messaggio);
            if (inAttesaRisposta) {
                inAttesaRisposta = false;
                mostraMenu();
            }
        });
    }

    /**
     * Scrivo nella chat le opzioni che posso scegliere.
     */
    private void mostraMenu() {
        mostraMessaggio.accept("1. Fai una domanda");
        mostraMessaggio.accept("2. Vedi le ultime domande");
        mostraMessaggio.accept("0. Esci");
        mostraMessaggio.accept("Scelta: ");
    }
}
