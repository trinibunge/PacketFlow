package PacketFlow;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Clase Red
 *
 * Representa la red de comunicación que gestiona el envío de mensajes.
 * Se encarga de fragmentar mensajes en paquetes, mantener una cola de transmisión,
 * y simular el envío y recepción de paquetes a través de la red.
 *
 * Incluye un sistema de pérdida probabilística: cada paquete enviado puede
 * perderse en tránsito según un porcentaje configurable. Los paquetes perdidos
 * quedan en estado PERDIDO de forma definitiva y hacen que el mensaje al que
 * pertenecen sea irreconstruible.
 *
 * Entradas:
 * - Tamaño máximo de paquete en caracteres
 * - Capacidad máxima de la red
 * - Porcentaje de pérdida (0.0 a 1.0)
 * - Mensajes a enviar
 *
 * Salidas:
 * - Cola de prioridad con paquetes en tránsito
 * - Lista de mensajes creados
 * - Estado de la red
 * - Simulación del envío y recepción de paquetes (con pérdida probabilística)
 */
public class Red {

    // Atributos de red
    private int tamanioMaxPaquete;
    private int capacidadMax;
    private double porcentajePerdida;
    private PriorityQueue<Paquete> paquetesEnTransito;
    private LinkedList<Mensaje> mensajes;

    /**
     * Constructor sin pérdida (0%).
     * Útil para tests y escenarios donde se quiere comportamiento determinístico.
     *
     * @param tamanioMaxPaquete tamaño máximo que puede tener cada paquete en caracteres
     * @param capacidadMax capacidad máxima de la red
     */
    public Red(int tamanioMaxPaquete, int capacidadMax) {
        this(tamanioMaxPaquete, capacidadMax, 0.0);
    }

    /**
     * Constructor con porcentaje de pérdida configurable.
     *
     * @param tamanioMaxPaquete tamaño máximo que puede tener cada paquete en caracteres
     * @param capacidadMax capacidad máxima de la red
     * @param porcentajePerdida probabilidad de que un paquete se pierda al enviarse (0.0 a 1.0)
     */
    public Red(int tamanioMaxPaquete, int capacidadMax, double porcentajePerdida) {
        this.tamanioMaxPaquete   = tamanioMaxPaquete;
        this.capacidadMax        = capacidadMax;
        this.porcentajePerdida   = porcentajePerdida;
        this.paquetesEnTransito  = new PriorityQueue<>();
        this.mensajes            = new LinkedList<>();
    }

    /**
     * Procesa el envío de un paquete tirando un dado para decidir si llega
     * o se pierde según el porcentaje de pérdida configurado.
     * Cambia el estado del paquete a RECIBIDO o PERDIDO de forma definitiva.
     *
     * @param p el paquete a procesar
     * @return el estado final asignado al paquete (RECIBIDO o PERDIDO)
     */
    public EstadoPaquete procesarEnvio(Paquete p) {
        if (Math.random() < porcentajePerdida) {
            p.setEstado(EstadoPaquete.PERDIDO);
        } else {
            p.setEstado(EstadoPaquete.RECIBIDO);
        }
        return p.getEstado();
    }

    /**
     * Crea un nuevo mensaje, lo fragmenta en paquetes según el tamaño máximo permitido,
     * y agrega los paquetes a la cola de transmisión respetando el orden de prioridad.
     * Valida que la red tenga capacidad suficiente antes de crear el mensaje.
     *
     * Nota: se usa add() individual en vez de addAll() para que el heap reordene
     * correctamente después de cada inserción y respete la prioridad del mensaje.
     *
     * @param id identificador único del mensaje
     * @param contenido contenido textual del mensaje a fragmentar
     * @param prioridad nivel de prioridad del mensaje
     * @return true si el mensaje fue creado exitosamente, false si no hay capacidad
     */
    public boolean crearMensaje(int id, String contenido, int prioridad) {
        int paquetesNecesarios = (int) Math.ceil((double) contenido.length() / tamanioMaxPaquete);

        if (paquetesEnTransito.size() + paquetesNecesarios > capacidadMax) {
            return false;
        }

        Mensaje m = new Mensaje(id, contenido, prioridad);
        m.fragmentar(tamanioMaxPaquete);
        mensajes.add(m);

        // add() individual para que el heap reordene por prioridad en cada inserción
        for (Paquete p : m.getPaquetes()) {
            paquetesEnTransito.add(p);
        }

        return true;
    }

    /**
     * Calcula el espacio disponible en la red (paquetes que aún se pueden agregar).
     * @return cantidad de paquetes que aún caben en la red
     */
    public int espacioDisponible() {
        return capacidadMax - paquetesEnTransito.size();
    }

    /**
     * Busca un mensaje en la lista de mensajes por su ID.
     *
     * @param id el identificador del mensaje a buscar
     * @return el mensaje si existe, null en caso contrario
     */
    public Mensaje buscarMensaje(int id) {
        for (Mensaje m : mensajes)
            if (m.getIdMensaje() == id) return m;
        return null;
    }

    /**
     * Elimina un mensaje y todos sus paquetes asociados de la red.
     *
     * @param id el identificador del mensaje a eliminar
     * @return true si el mensaje fue eliminado exitosamente, false si no se encontró
     */
    public boolean eliminarMensaje(int id) {
        Mensaje m = buscarMensaje(id);
        if (m == null) return false;
        paquetesEnTransito.removeIf(p -> p.getIdMensaje() == id);
        mensajes.remove(m);
        return true;
    }

    /**
     * Muestra el estado actual de la red.
     * Imprime la cantidad de paquetes en tránsito, recibidos, perdidos y el espacio disponible.
     */
    public void consultarEstado() {
        int enTransito = 0, recibidos = 0, perdidos = 0;

        for (Mensaje m : mensajes) {
            for (Paquete p : m.getPaquetes()) {
                switch (p.getEstado()) {
                    case EN_TRANSITO -> enTransito++;
                    case RECIBIDO    -> recibidos++;
                    case PERDIDO     -> perdidos++;
                }
            }
        }

        System.out.println("=== Estado de la red ===");
        System.out.println("Paquetes en tránsito : " + enTransito);
        System.out.println("Paquetes recibidos   : " + recibidos);
        System.out.println("Paquetes perdidos    : " + perdidos);
        System.out.println("Capacidad máxima     : " + capacidadMax);
        System.out.println("Espacio disponible   : " + (capacidadMax - enTransito));
        System.out.println("% de pérdida         : " + (int)(porcentajePerdida * 100) + "%");
    }

    /**
     * Simula el envío de todos los paquetes en tránsito.
     * Cada paquete puede llegar (RECIBIDO) o perderse (PERDIDO) según la probabilidad.
     */
    public void enviarPaquetes() {
        System.out.println("=== Enviando paquetes ===");
        while (!paquetesEnTransito.isEmpty()) {
            Paquete p = paquetesEnTransito.poll();
            EstadoPaquete estado = procesarEnvio(p);
            String etiqueta = (estado == EstadoPaquete.RECIBIDO) ? "Enviado" : "PERDIDO";
            System.out.printf("%s: Mensaje %d | Paquete %d/%d%n",
                    etiqueta, p.getIdMensaje(), p.getNumero(), p.getCantPaquetes());
        }
    }

    /**
     * Simula la recepción de un paquete de la cola de transmisión.
     * El paquete puede llegar o perderse según la probabilidad configurada.
     */
    public void recibirPaquete() {
        if (paquetesEnTransito.isEmpty()) {
            System.out.println("No hay paquetes en tránsito.");
            return;
        }
        Paquete p = paquetesEnTransito.poll();
        EstadoPaquete estado = procesarEnvio(p);
        String etiqueta = (estado == EstadoPaquete.RECIBIDO) ? "Recibido" : "PERDIDO";
        System.out.printf("%s: Mensaje %d | Paquete %d/%d%n",
                etiqueta, p.getIdMensaje(), p.getNumero(), p.getCantPaquetes());
    }

    /**
     * Obtiene el tamaño máximo permitido para cada paquete.
     * @return el tamaño máximo de paquete en caracteres
     */
    public int getTamanioMaxPaquete() {
        return tamanioMaxPaquete;
    }

    /**
     * Obtiene la capacidad máxima de la red.
     * @return la capacidad máxima (número máximo de paquetes simultáneos)
     */
    public int getCapacidadMax() {
        return capacidadMax;
    }

    /**
     * Obtiene el porcentaje de pérdida actual de la red.
     * @return probabilidad de pérdida (0.0 a 1.0)
     */
    public double getPorcentajePerdida() {
        return porcentajePerdida;
    }

    /**
     * Establece el porcentaje de pérdida de la red.
     * @param porcentajePerdida nueva probabilidad de pérdida (0.0 a 1.0)
     */
    public void setPorcentajePerdida(double porcentajePerdida) {
        this.porcentajePerdida = porcentajePerdida;
    }

    /**
     * Obtiene la cola de paquetes en tránsito.
     * @return PriorityQueue con los paquetes esperando ser enviados
     */
    public PriorityQueue<Paquete> getEnTransito() {
        return paquetesEnTransito;
    }

    /**
     * Obtiene la lista de todos los mensajes creados en la red.
     * @return LinkedList con los mensajes y sus paquetes
     */
    public LinkedList<Mensaje> getMensajes() {
        return mensajes;
    }
}