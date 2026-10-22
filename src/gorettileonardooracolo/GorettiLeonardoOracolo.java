package gorettileonardooracolo;

/**
 * Da qui parte il programma; qui tengo anche la porta usata dal client e dal server.
 */
public class GorettiLeonardoOracolo {
    // Client e server devono usare lo stesso numero per collegarsi.
    public static final int PORTA = 12345;

    /**
     * Avvio la finestra principale.
     */
    public static void main(String[] args) {
        // Swing vuole che la finestra venga creata sul suo thread grafico.
        java.awt.EventQueue.invokeLater(() -> {
            InterfacciaGrafica finestra = new InterfacciaGrafica();
            finestra.setVisible(true);
        });
    }
}
