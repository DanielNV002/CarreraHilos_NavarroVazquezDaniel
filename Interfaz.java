import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Interfaz extends JFrame {
    Caballo C = new Caballo();
    private final List<JLabel> caballosLabels;  // Lista de etiquetas para los caballos
    private final List<Caballo> caballos;
    private final JButton btnIniciar;

    public Interfaz() {
        setTitle("Simulador de Carrera de Caballos");
        setLayout(null);  // Usamos un diseño absoluto para poder mover los caballos
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 400);

        caballosLabels = new ArrayList<>();
        caballos = new ArrayList<>();

        // Crear el botón "Iniciar" para comenzar la carrera
        btnIniciar = new JButton("Iniciar Carrera");
        btnIniciar.setBounds(350, 300, 150, 30);  // Ubicación del botón
        add(btnIniciar);

        // Crear los caballos y las imágenes (JLabels)
        for (int i = 1; i <= caballos.size(); i++) {
            Caballo caballo = new Caballo("Caballo " + i, 500);  // Distancia de la carrera es 500
            caballos.add(caballo);

            // Cargar la imagen original del caballo
            ImageIcon caballoIcon = new ImageIcon("resources/Horse.png");

            // Reescalar la imagen (ajusta el tamaño según sea necesario)
            Image imagenEscalada = caballoIcon.getImage().getScaledInstance(50, 30, Image.SCALE_SMOOTH);

            // Crear el JLabel con la imagen escalada
            JLabel caballoLabel = new JLabel(new ImageIcon(imagenEscalada));
            caballoLabel.setBounds(50, 50 + i * 50, 50, 30);  // Posición inicial de cada caballo
            caballosLabels.add(caballoLabel);

            // Agregar las etiquetas de los caballos a la ventana
            add(caballoLabel);

            // Establecer la referencia a la ventana para actualizar el progreso
            caballo.setCarreraDeCaballos(this);
        }

        // Acción del botón "Iniciar"
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!C.getCarreraTerminada()) {
                    btnIniciar.setText("Iniciar Carrera");
                    iniciarCarrera();
                } else {
                    btnIniciar.setText("Reiniciar");
                    reiniciarCarrera();
                }
            }
        });
    }

    public static void main(String[] args) {
        // Inicializar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            Interfaz ventana = new Interfaz();
            ventana.setVisible(true);
        });
    }

    // Metodo para actualizar la posición de los caballos (mover las imágenes)
    public synchronized void actualizarProgreso(int indiceCaballo, int porcentaje) {
        // Calcular la posición horizontal en base al porcentaje de la carrera
        int xPos = (int) (porcentaje * 1.5);

        // Obtener el caballo (JLabel) y moverlo
        JLabel caballoLabel = caballosLabels.get(indiceCaballo);
        caballoLabel.setLocation(xPos, caballoLabel.getY());

        // Si un caballo llega a la meta, detenemos la carrera
        if (porcentaje >= 500 && !C.getCarreraTerminada()) {
            // Desactivar el botón de inicio
            btnIniciar.setEnabled(true);
            C.setCarreraTerminada(true);
            btnIniciar.setText("Reiniciar");

            mostrarResultados();
        }
    }

    // Iniciar la carrera
    private void iniciarCarrera() {
        for (Caballo caballo : caballos) {
            Thread hiloCaballo = new Thread(caballo);
            hiloCaballo.start();
        }

        // Desactivar el botón "Iniciar" después de que se presione
        btnIniciar.setEnabled(false);
    }

    // Mostrar los resultados de la carrera cuando termine
    private void mostrarResultados() {
        StringBuilder resultado = new StringBuilder("La carrera ha terminado. Resultados:\n");

        // Encontrar al caballo ganador
        Caballo ganador = null;
        int maxDistancia = 0;

        // Recorrer todos los caballos y mostrar su distancia final
        for (Caballo caballo : caballos) {
            resultado.append(caballo.getNombre())
                    .append(" ha recorrido ")
                    .append(caballo.getPorcentaje())
                    .append(" unidades.\n");

            // Determinar el caballo con mayor distancia recorrida (ganador)
            if (caballo.getPorcentaje() > maxDistancia) {
                maxDistancia = caballo.getPorcentaje();
                ganador = caballo;
            }
        }

        // Si encontramos un ganador, añadimos su nombre
        if (ganador != null) {
            resultado.append("\n¡El ganador es: ").append(ganador.getNombre()).append("!");
        }

        // Mostrar los resultados en un cuadro de diálogo
        int option = JOptionPane.showConfirmDialog(this, resultado.toString(), "Resultados", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    // Metodo para reiniciar la carrera
    private void reiniciarCarrera() {
        // Reiniciar los caballos y sus posiciones
        for (int i = 1; i <= caballos.size(); i++) {
            Caballo caballo = caballos.get(i);
            caballo.reiniciar();
            caballosLabels.get(i).setLocation(50, 50 + i * 50);  // Restablecer la posición inicial
            System.out.println("Caballo "+ (i + 1) +" recolocado");
        }

        // Habilitar nuevamente el botón de inicio y ponerlo en su estado original
        btnIniciar.setText("Iniciar Carrera");
        btnIniciar.setEnabled(true);

        // Resetear el estado de la carrera
        C.setCarreraTerminada(false);
    }
}
