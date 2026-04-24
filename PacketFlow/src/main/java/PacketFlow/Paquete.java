package PacketFlow;

/**
 * Clase Paquete
 *
 * Es un fragmento de un mensaje original que contiene información de su posición,
 * estado actual y referencia al mensaje del que proviene.
 *
 * Implementa Comparable para permitir ordenamiento por prioridad del mensaje
 * cuando se utiliza en estructuras como PriorityQueue. El primer extra de la entrega.
 *
 * Entradas:
 * - ID del mensaje al que pertenece
 * - Número de paquete
 * - Cantidad total de paquetes del mensaje
 * - Contenido del paquete
 * - Estado actual del paquete
 * - Referencia al mensaje original
 *
 * Salidas:
 * - Información completa del paquete
 * - Comparación con otros paquetes basada en prioridad del mensaje
 */
public class Paquete implements Comparable<Paquete> {

    private int idMensaje;
    private int numero;
    private int cantPaquetes;
    private String contenido;
    private EstadoPaquete estado;
    private Mensaje mensaje;

    /**
     * Constructor de la clase Paquete.
     * Inicializa un paquete con toda la información necesaria para su identificación
     * y transmisión a través de la red.
     *
     * @param idMensaje identificador del mensaje al que pertenece este paquete
     * @param numero número secuencial del paquete dentro del mensaje (comenzando en 1)
     * @param cantPaquetes cantidad total de paquetes en que se fragmentó el mensaje
     * @param contenido contenido textual del paquete
     * @param estado estado actual del paquete en su ciclo de vida
     * @param mensaje referencia al mensaje original del cual proviene este paquete
     */
    public Paquete(int idMensaje, int numero, int cantPaquetes, String contenido,
                   EstadoPaquete estado, Mensaje mensaje) {
        this.idMensaje    = idMensaje;
        this.numero       = numero;
        this.cantPaquetes = cantPaquetes;
        this.contenido    = contenido;
        this.estado       = estado;
        this.mensaje      = mensaje;
    }

    /**
     * Obtiene el identificador del mensaje al que pertenece este paquete.
     * @return el ID del mensaje
     */
    public int getIdMensaje() {
        return idMensaje;
    }

    /**
     * Obtiene el número secuencial de este paquete dentro del mensaje.
     * @return el número del paquete
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Obtiene la cantidad total de paquetes en que se fragmentó el mensaje original.
     * @return la cantidad total de paquetes
     */
    public int getCantPaquetes() {
        return cantPaquetes;
    }

    /**
     * Obtiene el contenido textual de este paquete.
     * @return el fragmento de contenido del paquete
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Obtiene el estado actual de este paquete.
     * @return el estado del paquete
     */
    public EstadoPaquete getEstado() {
        return estado;
    }

    /**
     * Obtiene la referencia al mensaje original del cual proviene este paquete.
     * @return el mensaje al que pertenece este paquete
     */
    public Mensaje getMensaje() {
        return mensaje;
    }

    /**
     * Establece el identificador del mensaje.
     * @param idMensaje el nuevo ID del mensaje
     */
    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    /**
     * Establece el número secuencial del paquete.
     * @param numero el nuevo número del paquete
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * Establece la cantidad total de paquetes.
     * @param cantPaquetes la nueva cantidad total de paquetes
     */
    public void setCantPaquetes(int cantPaquetes) {
        this.cantPaquetes = cantPaquetes;
    }

    /**
     * Establece el contenido del paquete.
     * @param contenido el nuevo contenido del paquete
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    /**
     * Establece el estado actual del paquete.
     * @param estado el nuevo estado del paquete
     */
    public void setEstado(EstadoPaquete estado) {
        this.estado = estado;
    }

    /**
     * Establece la referencia al mensaje original.
     * @param mensaje el nuevo mensaje de referencia
     */
    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Compara este paquete con otro basándose en la prioridad de sus mensajes.
     * Permite ordenar paquetes en una PriorityQueue según la prioridad del mensaje.
     * Paquetes con mayor prioridad aparecerán primero en la cola.
     *
     * @param otro el paquete a comparar con este
     * @return un valor negativo si este paquete tiene mayor prioridad,
     *         positivo si tiene menor prioridad, o cero si tienen igual prioridad
     */
    @Override
    public int compareTo(Paquete otro) {
        // objetivo del metodo: ordenar paquetes por prioridad del mensaje
        return this.getMensaje().getPrioridad() - otro.getMensaje().getPrioridad();
    }
}