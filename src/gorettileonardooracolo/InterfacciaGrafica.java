/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gorettileonardooracolo;

import javax.swing.SwingUtilities;

/**
 *
 * @author goretti.leonardo
 */
public class InterfacciaGrafica extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(InterfacciaGrafica.class.getName());
    private static final String STATO_MENU = "MENU";
    private static final String STATO_DOMANDA = "DOMANDA";

    private transient OracoloClient client;
    private String personalitaSelezionata = "Oracolo";
    private String statoInput = STATO_MENU;
    private boolean inAttesaRisposta;

    /**
     * Creates new form InterfacciaGrafica
     */
    public InterfacciaGrafica() {
        initComponents();
        aggiornaImmaginePersonalita(personalitaSelezionata);
        LSTPersonalita.setSelectedValue(personalitaSelezionata, true);
        inizializzaConnessione();
        inizializzaEventi();
    }

    private void aggiornaImmaginePersonalita(String personalita) {
        String fileGif;
        switch (personalita) {
            case "Chiromante":
                fileGif = "/cartomante.gif";
                break;
            case "Mago":
                fileGif = "/mago.gif";
                break;
            default:
                fileGif = "/oracolo.gif";
                break;
        }

        java.net.URL risorsa = getClass().getResource(fileGif);
        if (risorsa == null) {
            logger.severe("GIF non trovata nel classpath: " + fileGif);
            LBLImmagineMistico.setIcon(null);
            LBLImmagineMistico.setText("Immagine non disponibile");
            return;
        }

        LBLImmagineMistico.setText(null);
        LBLImmagineMistico.setIcon(new javax.swing.ImageIcon(risorsa));
    }

    private void inizializzaConnessione() {
        client = new OracoloClient(this::riceviMessaggioServer);
        mostraMenu();
    }

    private void inizializzaEventi() {
        LSTPersonalita.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String scelta = LSTPersonalita.getSelectedValue();
                if (scelta != null && !scelta.equalsIgnoreCase(personalitaSelezionata)) {
                    personalitaSelezionata = scelta;
                    aggiornaImmaginePersonalita(scelta);
                    inviaComando("PERSONA_" + scelta);
                    statoInput = STATO_MENU;
                }
            }
        });

        TXTDomanda.addActionListener(event -> inviaTesto());
        BTNInviaDomanda.addActionListener(event -> inviaTesto());
    }

    private void mostraMenu() {
        appendChat("1. Fai una domanda");
        appendChat("2. Vedi le ultime domande");
        appendChat("0. Esci");
        appendChat("Scelta: ");
        statoInput = STATO_MENU;
    }

    private void inviaTesto() {
        String testo = TXTDomanda.getText() == null ? "" : TXTDomanda.getText().trim();
        if (testo.isEmpty()) {
            return;
        }

        if (STATO_MENU.equals(statoInput)) {
            gestisciSceltaMenu(testo);
            TXTDomanda.setText("");
            return;
        }

        if (STATO_DOMANDA.equals(statoInput)) {
            inviaComando("DOMANDA_" + testo);
            appendChat("Tu: " + testo);
            TXTDomanda.setText("");
            statoInput = STATO_MENU;
            return;
        }
    }

    private void gestisciSceltaMenu(String scelta) {
        if ("1".equals(scelta)) {
            appendChat("Scrivi la tua domanda:");
            statoInput = STATO_DOMANDA;
            return;
        }

        if ("2".equals(scelta)) {
            appendChat("Recupero ultime domande...");
            inviaComando("STORIA");
            statoInput = STATO_MENU;
            return;
        }

        if ("0".equals(scelta)) {
            appendChat("Chiusura del client e del server...");
            inviaComando("ESCI");
            chiudiConnessione();
            dispose();
            return;
        }

        appendChat("Scelta non valida. Scegli uno dei numeri del menu.");
        mostraMenu();
    }

    private void inviaComando(String comando) {
        if (client != null) {
            inAttesaRisposta = client.inviaComando(comando);
        }
    }

    private void chiudiConnessione() {
        if (client != null) {
            client.chiudiConnessione();
        }
        statoInput = STATO_MENU;
    }

    private void appendChat(String messaggio) {
        if (messaggio == null || messaggio.trim().isEmpty()) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            aggiungiChat(messaggio);
        });
    }

    private void riceviMessaggioServer(String messaggio) {
        SwingUtilities.invokeLater(() -> {
            aggiungiChat(messaggio);
            if (inAttesaRisposta) {
                inAttesaRisposta = false;
                mostraMenu();
            }
        });
    }

    private void aggiungiChat(String messaggio) {
        if (messaggio == null || messaggio.trim().isEmpty()) {
            return;
        }
        TXTCronologia.append(messaggio + System.lineSeparator());
        TXTCronologia.setCaretPosition(TXTCronologia.getDocument().getLength());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        LSTPersonalita = new javax.swing.JList<>();
        LBLSceltaPersonalita = new javax.swing.JLabel();
        LBLInserisciDomanda = new javax.swing.JLabel();
        TXTDomanda = new javax.swing.JTextField();
        LBLCronologiaChat = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TXTCronologia = new javax.swing.JTextArea();
        BTNInviaDomanda = new javax.swing.JButton();
        LBLImmagineMistico = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        LSTPersonalita.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Oracolo", "Chiromante", "Mago" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(LSTPersonalita);

        LBLSceltaPersonalita.setText("Scegli la personalità:");
        LBLSceltaPersonalita.setName("LBLSceltaPersonalita"); // NOI18N

        LBLInserisciDomanda.setText("Inserisci la domanda:");

        LBLCronologiaChat.setText("Cronologia chat:");

        TXTCronologia.setColumns(20);
        TXTCronologia.setRows(5);
        jScrollPane2.setViewportView(TXTCronologia);

        BTNInviaDomanda.setText("Invia domanda");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LBLInserisciDomanda)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TXTDomanda, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BTNInviaDomanda))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LBLSceltaPersonalita, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(244, 244, 244)
                        .addComponent(LBLCronologiaChat))
                    .addComponent(LBLImmagineMistico, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LBLSceltaPersonalita)
                            .addComponent(LBLCronologiaChat))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(LBLInserisciDomanda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXTDomanda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BTNInviaDomanda))
                        .addGap(18, 18, 18)
                        .addComponent(LBLImmagineMistico, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 16, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new InterfacciaGrafica().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTNInviaDomanda;
    private javax.swing.JLabel LBLCronologiaChat;
    private javax.swing.JLabel LBLImmagineMistico;
    private javax.swing.JLabel LBLInserisciDomanda;
    private javax.swing.JLabel LBLSceltaPersonalita;
    private javax.swing.JList<String> LSTPersonalita;
    private javax.swing.JTextArea TXTCronologia;
    private javax.swing.JTextField TXTDomanda;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
