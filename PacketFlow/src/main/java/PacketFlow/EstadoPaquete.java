package PacketFlow;

public enum EstadoPaquete {
    // enum para representar los estados de paquete, decidimos
    // implementarlo asi para evitar errores de typos, ademas los
    // valores permitidos son un cojunto finito y predefinido (solo los tres que participan),
    // por ende lo vimos como una forma de implementar una buena practica.
    EN_TRANSITO,
    RECIBIDO,
    PERDIDO,
}
