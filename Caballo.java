import java.util.Random;

public class Caballo implements Runnable {
    private String nombre;
    private int porcentaje;
    private int distanciaMaxima;
    private Interfaz interfaz;
    private static boolean carreraTerminada = false;


    public Caballo(){

    }

    public Caballo(String nombre, int distanciaMaxima) {
        this.nombre = nombre;
        this.porcentaje = 0;
        this.distanciaMaxima = distanciaMaxima;
    }

    @Override
    public void run() {
        while (porcentaje < distanciaMaxima && !carreraTerminada) {
            // Avanzar aleatoriamente entre 1 y 5 unidades
            int avance = generaNumAleatorio(0, 10);
            porcentaje += avance;

            if (porcentaje > distanciaMaxima) {
                porcentaje = distanciaMaxima;  // Asegurarnos de que no pase del 100%
            }

            // Simulamos un pequeño retraso para hacer la carrera visible
            try {
                Thread.sleep(100);  // Cada caballo avanza en su carrera cada 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Actualizamos el progreso de la barra de este caballo
            if (interfaz != null) {
                interfaz.actualizarProgreso(Integer.parseInt(nombre.split(" ")[1]) - 1, porcentaje);
            }
        }
    }

    public void setCarreraDeCaballos(Interfaz interfaz) {
        this.interfaz = interfaz;
    }

    // Genera un número aleatorio entre min y max (inclusive)
    private int generaNumAleatorio(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    // Getter para obtener el porcentaje (distancia recorrida) del caballo
    public int getPorcentaje() {
        return porcentaje;
    }

    // Getter para el nombre del caballo
    public String getNombre() {
        return nombre;
    }

    // Metodo para reiniciar el caballo (ponerlo en su posición inicial)
    public void reiniciar() {
        this.porcentaje = 0;
    }

    public boolean getCarreraTerminada(){
        return carreraTerminada;
    }

    public void setCarreraTerminada(boolean opcion){
        carreraTerminada = opcion;
    }
}
