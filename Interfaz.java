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
    private JPanel background;  // Panel que contiene los caballos y las líneas

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

        // Crear background (panel donde estarán los caballos y las líneas)
        background = new JPanel();
        background.setBounds(0, 0, 900, 250);  // Dimensiones del fondo (ajustadas a tu tamaño de ventana)
        background.setBackground(new Color(0, 130, 4));  // Color de fondo
        background.setLayout(null);  // Usamos un layout absoluto para manejar la posición de los caballos
        background.setOpaque(true);  // Hacer que el JPanel sea opaco para mostrar el color de fondo
        add(background);

        // Crear los caballos y las imágenes (JLabels)
        for (int i = 1; i <= 4; i++) {
            Caballo caballo = new Caballo("Caballo " + i, 500);  // Distancia de la carrera es 500
            caballos.add(caballo);

            caballo.setCarreraDeCaballos(this);
        }



        // Crear las líneas de fondo y agregar los caballos
        for (int i = 0; i < caballos.size(); i++) {

            // Crear el JLabel para mostrar el número del caballo al principio de la pista
            JLabel numberLabel = new JLabel(""+(i+1));  // Usamos i + 1 para mostrar "Caballo 1", "Caballo 2", etc.
            numberLabel.setForeground(Color.WHITE);  // Color del texto
            numberLabel.setBounds(10, 50 + i * 50, 100, 30);  // Posición del número en la pista
            background.add(numberLabel);  // Añadir el número al panel de fondo

            // Cargar la imagen original del caballo
            ImageIcon caballoIcon = new ImageIcon("resources/Horse.png");
            Image imagenEscalada = caballoIcon.getImage().getScaledInstance(50, 30, Image.SCALE_SMOOTH);

            // Crear el JLabel con la imagen escalada
            JLabel caballoLabel = new JLabel(new ImageIcon(imagenEscalada));
            caballoLabel.setBounds(50, 50 + i * 50, 50, 30);  // Posición inicial de cada caballo
            caballosLabels.add(caballoLabel);

            // Agregar la etiqueta de los caballos al fondo (JPanel)
            background.add(caballoLabel);

        }

        //Crea la Meta
        ImageIcon metaIcon = new ImageIcon("resources/Meta.jpg");
        Image imagenMetaEscalada = metaIcon.getImage().getScaledInstance(30, 200, Image.SCALE_SMOOTH);
        JLabel metaLabel = new JLabel(new ImageIcon(imagenMetaEscalada));
        metaLabel.setBounds(820,50,30,200);
        background.add(metaLabel);

        for (int i = 0; i < caballos.size(); i++) {
            // Crear la línea de fondo para cada caballo
            JLabel backgroundLabel = new JLabel();
            backgroundLabel.setBackground(new Color(0, 180, 5));
            backgroundLabel.setOpaque(true);  // Aseguramos que el fondo sea visible
            backgroundLabel.setBounds(0, 50 + i * 50, 900, 30);  // Posición de la línea (ajustamos el tamaño)
            background.add(backgroundLabel);  // Añadir la línea al panel de fondo
        }

        //Crea las Gradas
        ImageIcon gradasIcon = new ImageIcon("resources/Gradas.jpg");
        Image imagenGradasEscalada = gradasIcon.getImage().getScaledInstance(900, 50, Image.SCALE_SMOOTH);
        JLabel gradasLabel = new JLabel(new ImageIcon(imagenGradasEscalada));
        gradasLabel.setBounds(0,0,900,50);
        background.add(gradasLabel);

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
        int xPos = 50 + (int) (porcentaje * 1.5);  // Sumar 50 para tener en cuenta el desplazamiento inicial

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
        for (int i = 0; i < caballos.size(); i++) {
            Caballo caballo = caballos.get(i);
            caballo.reiniciar();

            // Restablecer la posición inicial (X y Y)
            caballosLabels.get(i).setLocation(50, 50 + i * 50);  // Asegúrate de que Y está correcto aquí
            caballosLabels.get(i).repaint();  // Fuerza el repaint después de mover
        }

        // Habilitar nuevamente el botón de inicio y ponerlo en su estado original
        btnIniciar.setText("Iniciar Carrera");
        btnIniciar.setEnabled(true);

        // Resetear el estado de la carrera
        C.setCarreraTerminada(false);
    }
}
