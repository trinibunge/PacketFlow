package PacketFlow;

import java.util.LinkedList;
import java.util.PriorityQueue;


public class Red {

    private int tamanioMaxPaquete;
    private int capacidadMax;
    private PriorityQueue<Paquete> paquetesEnTransito;
    private LinkedList<Mensaje> mensajes;

    public Red(int tamanioMaxPaquete, int capacidadMax) {
        this.tamanioMaxPaquete   = tamanioMaxPaquete;
        this.capacidadMax        = capacidadMax;
        this.paquetesEnTransito  = new PriorityQueue<>();
        this.mensajes            = new LinkedList<>();
    }


    public void crearMensaje(int id, String contenido, int prioridad) {
        Mensaje m = new Mensaje(id, contenido, prioridad);
        m.fragmentar(contenido, tamanioMaxPaquete);
        mensajes.add(m);
        paquetesEnTransito.addAll(m.getPaquetes());
    }


    public Mensaje buscarMensaje(int id) {
        for (Mensaje m : mensajes)
            if (m.getIdMensaje() == id) return m;
        return null;
    }

    public boolean eliminarMensaje(int id) {
        Mensaje m = buscarMensaje(id);
        if (m == null) return false;
        paquetesEnTransito.removeIf(p -> p.getIdMensaje() == id);
        mensajes.remove(m);
        return true;
    }


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
    }





    public void enviarPaquetes() {
        System.out.println("=== Enviando paquetes ===");
        while (!paquetesEnTransito.isEmpty()) {
            Paquete p = paquetesEnTransito.poll();
            p.setEstado(EstadoPaquete.RECIBIDO);
            System.out.printf("Enviado: PacketFlow.Mensaje %d | PacketFlow.Paquete %d/%d%n",
                    p.getIdMensaje(), p.getNumero(), p.getCantPaquetes());
        }
    }

    public void recibirPaquete() {
        if (paquetesEnTransito.isEmpty()) {
            System.out.println("No hay paquetes en tránsito.");
            return;
        }
        Paquete p = paquetesEnTransito.poll();
        p.setEstado(EstadoPaquete.RECIBIDO);
        System.out.printf("Recibido: PacketFlow.Mensaje %d | PacketFlow.Paquete %d/%d%n",
                p.getIdMensaje(), p.getNumero(), p.getCantPaquetes());
    }


    public int getTamanioMaxPaquete()              { return tamanioMaxPaquete; }
    public int getCapacidadMax()                   { return capacidadMax; }
    public PriorityQueue<Paquete> getEnTransito()  { return paquetesEnTransito; }
    public LinkedList<Mensaje> getMensajes()        { return mensajes; }
}