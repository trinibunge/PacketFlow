package PacketFlow;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Clase Reconstructor
 *
 * Responsable de reconstruir mensajes originales a partir de paquetes recibidos tras pasar por la red.
 * Verifica integridad, completitud y ordena los paquetes para recrear el mensaje original.
 *
 * Política de reconstrucción:
 * - Solo se reconstruyen mensajes COMPLETOS (todos sus paquetes recibidos).
 * - Al reconstruir exitosamente, el mensaje se elimina de la red automáticamente.
 *
 * Salidas:
 * - Mensaje reconstruido o null si no se pudo
 * - Validación de si un mensaje está completo
 * - Mensajes ordenados por prioridad
 */
public class Reconstructor {

    /**
     * Separa los paquetes recibidos de un mensaje específico.
     * Solo devuelve paquetes que tienen estado RECIBIDO.
     *
     * @param id el ID del mensaje a filtrar
     * @param mensajes la lista de mensajes que contienen los paquetes
     * @return lista de paquetes recibidos del mensaje especificado
     */
    public LinkedList<Paquete> separarPorMensaje(int id, LinkedList<Mensaje> mensajes) {
        LinkedList<Paquete> resultado = new LinkedList<>();

        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje men = mensajes.get(i);
            if (men.getIdMensaje() == id) {
                for (Paquete paq : men.getPaquetes()) {
                    if (paq.getEstado() == EstadoPaquete.RECIBIDO) {
                        resultado.add(paq);
                    }
                }
            }
        }

        return resultado;
    }

    /**
     * Verifica si todos los paquetes de un mensaje han sido recibidos.
     *
     * @param id el ID del mensaje a verificar
     * @param mensajes la lista de mensajes
     * @return true si todos los paquetes están recibidos, false en caso contrario
     */
    public boolean estaCompleto(int id, LinkedList<Mensaje> mensajes) {
        LinkedList<Paquete> resultado = separarPorMensaje(id, mensajes);
        return !resultado.isEmpty() && resultado.size() == resultado.get(0).getCantPaquetes();
    }


    /**
     * Devuelve los mensajes ordenados por prioridad (1 primero, 3 último).
     *
     * @param mensajes la lista de mensajes a ordenar
     * @return PriorityQueue con los mensajes ordenados por prioridad
     */
    public PriorityQueue<Mensaje> ordenarPorPrioridad(LinkedList<Mensaje> mensajes) {
        PriorityQueue<Mensaje> ordenados = new PriorityQueue<>(
                (a, b) -> a.getPrioridad() - b.getPrioridad()
        );
        ordenados.addAll(mensajes);
        return ordenados;
    }

    /**
     * Reconstruye el mensaje con el ID indicado y lo elimina de la red.
     * Solo reconstruye si el mensaje está completo.
     *
     * @param id el ID del mensaje a reconstruir
     * @param red la red de la cual extraer y eliminar el mensaje
     * @return el contenido del mensaje reconstruido, o null si no está completo o no existe
     */
    public String reconstruir(int id, Red red) {
        LinkedList<Mensaje> mensajes = red.getMensajes();

        if (!estaCompleto(id, mensajes)) {
            return null;
        }

        LinkedList<Paquete> resultado = separarPorMensaje(id, mensajes);

        // Ordena los paquetes por número usando una PriorityQueue
        PriorityQueue<Paquete> ordenado = new PriorityQueue<>((a, b) -> a.getNumero() - b.getNumero());
        for (int x = 0; x < resultado.size(); x++) {
            ordenado.add(resultado.get(x));
        }

        // Reconstruye el mensaje concatenando el contenido de cada paquete en orden
        StringBuilder rec = new StringBuilder();
        while (ordenado.size() > 0) {
            rec.append(ordenado.poll().getContenido());
        }

        // Eliminar el mensaje de la red tras reconstruirlo
        red.eliminarMensaje(id);

        return rec.toString();
    }

    /**
     * Reconstruye el mensaje completo con prioridad más alta que esté hace más tiempo en la red.
     * Si hay varios mensajes con la misma prioridad, gana el que se creó primero.
     * Una vez reconstruido, el mensaje se elimina de la red.
     *
     * @param red la red de la cual extraer y reconstruir el siguiente mensaje
     * @return texto formateado con el mensaje reconstruido, o null si no hay completos
     */
    public String reconstruirSiguiente(Red red) {
        LinkedList<Mensaje> mensajes = red.getMensajes();
        if (mensajes.isEmpty()) {
            return null;
        }

        // Buscar mensaje completo con mayor prioridad, priorizando el más antiguo
        Mensaje elegido = null;
        for (Mensaje m : mensajes) {
            if (estaCompleto(m.getIdMensaje(), mensajes)) {
                if (elegido == null || m.getPrioridad() < elegido.getPrioridad()) {
                    elegido = m;
                }
            }
        }

        if (elegido == null) {
            return null;
        }

        int id = elegido.getIdMensaje();
        int prioridad = elegido.getPrioridad();
        String contenido = reconstruir(id, red);

        return "Mensaje: " + id + " | Prioridad: " + prioridad + "\n" + contenido;
    }
}