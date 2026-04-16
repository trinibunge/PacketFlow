import java.util.Queue;
import java.util.ArrayDeque;


public class Mensaje {
    private int idMensaje;
    private Queue <Paquete> paquetes;
    private int prioridad;

    public Mensaje(int idMensaje,String contenido, int prioridad) {
        this.idMensaje = idMensaje;
        this.prioridad = prioridad;
        this.paquetes = new ArrayDeque<>();
    }
    // Getters
    public int getIdMensaje() {
        return idMensaje;
    }
    public int getPrioridad () {
        return prioridad;
    }
    public Queue<Paquete> getPaquetes() {
        return paquetes;
    }

    // Setters
    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;}
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    // metodo para separar el mensaje en paquetes
    public void Fragmentar(String contenido, int tamanio){
        // calculo paquetes necesarios para el mensaje, ceil redondea para arriba la division
        int CantPaquetes = (int) Math.ceil((double) contenido.length() / tamanio);
        // se encarga de separar el mensaje segun el tamaño
        for(int i = 0; i < CantPaquetes; i++){
            int principio = i * tamanio;
            int fin = Math.min(principio + tamanio, contenido.length());
            String frag = contenido.substring(principio, fin);

         Paquete paq = new Paquete(
                 idMensaje,
                 i + 1,
                 CantPaquetes,
                 frag,
                 EstadoPaquete.EN_TRANSITO,
                 this
         );
        paquetes.add(paq);
        }

    }

}