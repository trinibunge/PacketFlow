package PacketFlow;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Clase Reconstructor
 *
 * Responsable de reconstruir mensajes originales a partir de paquetes recibidos tras pasar por la red.
 * Verifica integridad, completitud y ordena los paquetes para recrear el mensaje original.
 *
 * Entradas:
 * - ID del mensaje a reconstruir
 * - Lista de mensajes que contienen los paquetes recibidos
 *
 * Salidas:
 * - Mensaje reconstruido o mensaje de error si no pudo
 * - Validación de si el mensaje está completo (true) o no (false)
 * - Paquetes filtrados por ID de mensaje
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
        // objetivo del método: filtrar los paquetes de un mensaje en base a su ID
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

        if (resultado.isEmpty()) {
            // si no hay paquetes para el mensaje, no está completo
            return false;
        }
        else if (resultado.size() == resultado.get(0).getCantPaquetes()) {
            // si la cantidad de paquetes coincide con el atributo cantPaquetes, está completo
            return true;
        }
        else {
            // si no coincide, está incompleto
            return false;
        }
    }

    /**
     * Reconstruye un mensaje a partir de los paquetes recibidos.
     * Verifica que todos los paquetes estén presentes antes de reconstruir.
     *
     * @param id el ID del mensaje a reconstruir
     * @param mensajes la lista de mensajes que contienen los paquetes
     * @return el mensaje reconstruido en orden, o un mensaje de error si está incompleto
     */
    public String reconstruir(int id, LinkedList<Mensaje> mensajes) {
        LinkedList<Paquete> resultado = separarPorMensaje(id, mensajes);

        if (resultado.isEmpty() || resultado.size() != resultado.get(0).getCantPaquetes()) {
            return "No se puede reconstruir el mensaje, no estan todos los paquetes, mensaje: " + id;
        }

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

        return rec.toString();
    }
}