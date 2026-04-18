import java.util.LinkedList;
import java.util.PriorityQueue;

public class Reconstructor {
    // Dado el ID del mensaje, revisa el mensaje que es y se queda con los paquetes que le pertenecen
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
    // verifica si esta completo en base a la cantidad de paquetes que llegaron a separarPorMensaje.
    public boolean estaCompleto(int id,  LinkedList<Mensaje> mensajes) {
        LinkedList<Paquete> resultado = separarPorMensaje(id, mensajes); // el mensaje a comparar (aun sin separar)
        if (resultado.isEmpty()) { // si no hay paquetes para el mensaje no esta completo
            return false;
        }
        else if(resultado.size()== resultado.get(0).getCantPaquetes()){ // si coincide la cantidad de paquetes con elatributo cantPaquetes de cualquier paquete de resultado, es cierto
            return true;
        }
        else{
            return false; // si no esta incompleto
        }
    }

    public String reconstruir(int id, LinkedList<Mensaje> mensajes) {
        LinkedList<Paquete> resultado = separarPorMensaje(id, mensajes);
        if (resultado.isEmpty() || resultado.size() != resultado.get(0).getCantPaquetes()) {
            return "No se puede reconstruir el mensaje, no estan todos los paquetes, mensaje: " + id;
        }
        PriorityQueue<Paquete> ordenado = new PriorityQueue<>((a, b) -> a.getNumero() - b.getNumero());
        for (int x = 0; x < resultado.size(); x++) {
            ordenado.add(resultado.get(x));
        }
        StringBuilder rec = new StringBuilder();
        while (ordenado.size() > 0) {
            rec.append(ordenado.poll().getContenido());
        }
        return rec.toString();
    }
}