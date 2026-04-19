package RedTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Red;
import PacketFlow.Reconstructor;
import PacketFlow.EstadoPaquete;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;

public class RedTest extends TestCase {
    public void testEliminarMensajeEliminaMensajeyPaquetes(){
        // objetivo del test: verificar que el metodo "eliminarMensaje", saca el mensaje
        // de la lista y sus respectivos paquetes de la cola de paquetes en tranisto.
        Red red = new Red(2, 10);
        red.crearMensaje(1,"azucar", 1);
        red.eliminarMensaje(1);
        assertNull(red.buscarMensaje(1));
        assertEquals(0, red.getEnTransito().size());
    }
    public void testEliminarMensajeConIdInvalido(){
        // objetivo del test: verificar que no se puede eliminar un mensaje que no existe
        // en la red, y que para manejar el error devuelve false.
        Red red = new Red(2, 10);
        assertFalse(red.eliminarMensaje(1));

    }
    public void testCambioDeEstadoARecibido(){
        // objetivo del test: verificar que al recibir paquetes, el método cambia su estado
        // a recibido
        Red red = new Red(2, 10);
        red.crearMensaje(2, "Te amo", 2);
        Mensaje msj = red.buscarMensaje(2);
        red.recibirPaquete();
        Paquete paq = msj.getPaquetes().peek();
        assertEquals(EstadoPaquete.RECIBIDO, paq.getEstado());
    }
    public void testNoPasaNadaSiNoHayPaquetesRecibidosYLlamoAlMetodo(){
        // objetivo del test: verificar que si se llama a recibirPaquete con la cola
        // vacía, el código no explota
        Red red = new Red(2, 10);
        try {
            red.recibirPaquete();
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}