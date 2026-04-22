package PacketFlow;

import java.util.Queue;
import java.util.ArrayDeque;

/**
 * Clase Mensaje
 *
 * Representa un mensaje completo que va a ser enviado a través de la red.
 * Se encarga de fragmentar el mensaje en múltiples paquetes según el tamaño máximo permitido.
 * Cada paquete mantiene referencia al mensaje original y conoce su posición en el conjunto.
 *
 * Entradas:
 * - ID del mensaje
 * - Contenido del mensaje
 * - Prioridad del mensaje
 *
 * Salidas:
 * - Cola de paquetes generados tras la fragmentación
 * - Información del mensaje
 */
public class Mensaje {

    private int idMensaje;
    private Queue<Paquete> paquetes;
    private int prioridad;
    private String contenido;

    /**
     * Constructor de la clase Mensaje.
     * Inicializa un mensaje con su ID, contenido y prioridad.
     *
     * @param idMensaje identificador único del mensaje
     * @param contenido contenido textual del mensaje
     * @param prioridad nivel de prioridad del mensaje
     */
    public Mensaje(int idMensaje, String contenido, int prioridad) {
        this.idMensaje = idMensaje;
        this.prioridad = prioridad;
        this.paquetes = new ArrayDeque<>();
        this.contenido = contenido;
    }

    /**
     * Obtiene el identificador único del mensaje.
     * @return el ID del mensaje
     */
    public int getIdMensaje() {
        return idMensaje;
    }

    /**
     * Obtiene el nivel de prioridad del mensaje.
     * @return el valor de prioridad
     */
    public int getPrioridad() {
        return prioridad;
    }

    /**
     * Obtiene la cola de paquetes del mensaje.
     * @return Queue con todos los paquetes generados
     */
    public Queue<Paquete> getPaquetes() {
        return paquetes;
    }

    /**
     * Obtiene el contenido original del mensaje.
     * @return el contenido textual del mensaje
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Establece el identificador del mensaje.
     * @param idMensaje el nuevo ID del mensaje
     */
    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    /**
     * Establece el nivel de prioridad del mensaje.
     * @param prioridad el nuevo valor de prioridad
     */
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Fragmenta el mensaje en múltiples paquetes según el tamaño máximo permitido.
     * Calcula la cantidad de paquetes necesarios y divide el contenido de forma equitativa.
     * Cada paquete creado se agrega a la cola y mantiene referencia al mensaje original.
     *
     * @param contenido el contenido textual a fragmentar
     * @param tamanio el tamaño máximo permitido para cada paquete en caracteres.
     */
    public void fragmentar(String contenido, int tamanio) {
        // Calcula la cantidad de paquetes necesarios redondeando hacia arriba
        int cantPaquetes = (int) Math.ceil((double) contenido.length() / tamanio);

        // Separa el contenido en fragmentos y crea un paquete para cada uno
        for (int i = 0; i < cantPaquetes; i++) {
            // Calcula los índices de inicio y fin del fragmento
            int principio = i * tamanio;
            int fin = Math.min(principio + tamanio, contenido.length());

            // Extrae el fragmento del contenido
            String frag = contenido.substring(principio, fin);

            // Crea el paquete con toda la información necesaria
            Paquete paq = new Paquete(
                    idMensaje,           // ID del mensaje
                    i + 1,               // Número de paquete (comenzando en 1)
                    cantPaquetes,        // Cantidad total de paquetes
                    frag,                // Contenido del fragmento
                    EstadoPaquete.EN_TRANSITO, // Estado inicial
                    this                 // Referencia al mensaje original
            );

            // Agrega el paquete a la cola
            paquetes.add(paq);
        }
    }
}