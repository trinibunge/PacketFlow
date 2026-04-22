package PacketFlow;

public enum EstadoPaquete {

    /**
     * Enum EstadoPaquete
     *
     * Define los estados posibles que puede tener un paquete durante su ciclo de vida
     * en la red de comunicación.
     *
     * Utilizar un enum proporciona varias ventajas:
     * - Claridad: los estados válidos están explícitamente definidos
     * - Mantenibilidad: fácil de extender con nuevos estados si es necesario
     * - Comparación directa: uso de "==" en lugar de comparación de strings
     *
     * Estados definidos:
     * - EN_TRANSITO: El paquete está siendo transportado a través de la red
     * - RECIBIDO: El paquete llegó correctamente a su destino
     * - PERDIDO: El paquete se perdió durante la transmisión
     */
    EN_TRANSITO,
    RECIBIDO,
    PERDIDO,
}
