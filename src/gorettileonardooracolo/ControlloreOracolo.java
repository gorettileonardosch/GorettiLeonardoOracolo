package gorettileonardooracolo;

import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class ControlloreOracolo {
    private final Consumer<String> mostraMessaggio;
    private final Runnable chiudiFinestra;
    private final OracoloClient client;
    private String personalitaSelezionata = "Oracolo";
    private boolean inserimentoDomanda;
    private boolean inAttesaRisposta;

    public ControlloreOracolo(Consumer<String> mostraMessaggio, Runnable chiudiFinestra) {
        this.mostraMessaggio = mostraMessaggio;
        this.chiudiFinestra = chiudiFinestra;
        client = new OracoloClient(this::riceviMessaggioServer);
        mostraMenu();
    }

    public void selezionaPersonalita(String personalita) {
        if (personalita == null || personalita.equalsIgnoreCase(personalitaSelezionata)) {
            return;
        }

        personalitaSelezionata = personalita;
        inserimentoDomanda = false;
        inviaComando("PERSONA_" + personalita);
    }

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

    private void inviaComando(String comando) {
        inAttesaRisposta = client.inviaComando(comando);
    }

    private void riceviMessaggioServer(String messaggio) {
        SwingUtilities.invokeLater(() -> {
            mostraMessaggio.accept(messaggio);
            if (inAttesaRisposta) {
                inAttesaRisposta = false;
                mostraMenu();
            }
        });
    }

    private void mostraMenu() {
        mostraMessaggio.accept("1. Fai una domanda");
        mostraMessaggio.accept("2. Vedi le ultime domande");
        mostraMessaggio.accept("0. Esci");
        mostraMessaggio.accept("Scelta: ");
    }
}
