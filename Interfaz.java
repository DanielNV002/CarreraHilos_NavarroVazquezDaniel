import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Interfaz extends JFrame {
    Caballo C = new Caballo();
    private final List<JProgressBar> barrasDeProgreso;
    private final List<Caballo> caballos;
    private final JButton btnIniciar;
    private static boolean carreraTerminada = false;  // Variable para saber si la carrera terminó

    public Interfaz() {
        setTitle("Simulador de Carrera de Caballos");
        setLayout(new GridLayout(5, 1));  // Añadimos una fila más para los caballos y un botón
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);

        barrasDeProgreso = new ArrayList<>();
        caballos = new ArrayList<>();

        // Crear los caballos y las barras de progreso individuales
        for (int i = 1; i <= 4; i++) {
            Caballo caballo = new Caballo("Caballo " + i, 500);  // Distancia de la carrera es 500
            caballos.add(caballo);

            // Crear la barra de progreso para cada caballo
            JProgressBar barra = new JProgressBar(0, 500);  // La barra va de 0 a 500
            barra.setStringPainted(true);
            barra.setString("Caballo " + i);
            barrasDeProgreso.add(barra);

            // Agregar las barras de los caballos a la ventana
            add(barra);

            // Establecer la referencia a la ventana para actualizar el progreso
            caballo.setCarreraDeCaballos(this);
        }

        // Crear el botón "Iniciar" para comenzar la carrera
        btnIniciar = new JButton("Iniciar Carrera");
        add(btnIniciar);

        // Acción del botón "Iniciar"
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!C.getCarreraTerminada()) {
                    btnIniciar.setText("Iniciar Carrera");
                    iniciarCarrera();
                }else{
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

    // Metodo para actualizar las barras de progreso de los caballos
    public synchronized void actualizarProgreso(int indiceCaballo, int porcentaje) {
        // Actualizar la barra de progreso correspondiente
        JProgressBar barra = barrasDeProgreso.get(indiceCaballo);
        barra.setValue(porcentaje);
        barra.setString("Caballo " + (indiceCaballo + 1));

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
            resultado.append("\n¡El ganador es: ").append(ganador.getNombre() + "!");
        }

        // Mostrar los resultados en un cuadro de diálogo
        int option = JOptionPane.showConfirmDialog(this, resultado.toString(), "Resultados", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }


    // Metodo para reiniciar la carrera
    private void reiniciarCarrera() {
        // Reiniciar los caballos y las barras de progreso
        for (int i = 0; i < caballos.size(); i++) {
            Caballo caballo = caballos.get(i);
            caballo.reiniciar();
            barrasDeProgreso.get(i).setValue(0);
            barrasDeProgreso.get(i).setString("Caballo " + (i + 1) + " - 0%");
        }

        // Habilitar nuevamente el botón de inicio y ponerlo en su estado original
        btnIniciar.setText("Iniciar Carrera");
        btnIniciar.setEnabled(true);

        // Resetear el estado de la carrera
        C.setCarreraTerminada(false);
    }
}
