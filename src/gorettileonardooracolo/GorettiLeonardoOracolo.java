package gorettileonardooracolo;

public class GorettiLeonardoOracolo {
    public static final int PORTA = 12345;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            InterfacciaGrafica finestra = new InterfacciaGrafica();
            finestra.setVisible(true);
        });
    }
}
