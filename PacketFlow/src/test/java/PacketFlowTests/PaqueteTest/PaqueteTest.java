package PacketFlowTests.PaqueteTest;
import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Red;
import PacketFlow.Reconstructor;
import PacketFlow.EstadoPaquete;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;

public class PaqueteTest extends TestCase {

    public void testGuardaInfoCorrectamente(){
        Mensaje msj = new Mensaje(1,"contenidomsj",1);
        Paquete paq = new Paquete(1,34,3,"cont", EstadoPaquete.EN_TRANSITO,msj);

        assertEquals(1, paq.getIdMensaje());
        assertEquals(34, paq.getNumero());
        assertEquals(3, paq.getCantPaquetes());
        assertEquals("cont", paq.getContenido());
        assertEquals(EstadoPaquete.EN_TRANSITO,paq.getEstado());
    }

    public void testCambioDeEstado(){
        Mensaje msj = new Mensaje(1,"contenidomsj",1);
        Paquete paq = new Paquete(1,34,3,"cont", EstadoPaquete.EN_TRANSITO,msj);

        assertEquals(EstadoPaquete.EN_TRANSITO,paq.getEstado());

        //se simula que el paquete fue enviado, por ende deberia cambiar su estado
        paq.setEstado(EstadoPaquete.RECIBIDO);
        assertEquals(EstadoPaquete.RECIBIDO,paq.getEstado());

        //se simula la perdida del paquete
        paq.setEstado(EstadoPaquete.PERDIDO);
        assertEquals(EstadoPaquete.PERDIDO,paq.getEstado());

    }

    public void testIdMsjDifiereIDPaquete(){
        Mensaje msj = new Mensaje(89,"5libertadores",2);
        Paquete paq = new Paquete(1,34,3,"cont", EstadoPaquete.EN_TRANSITO,msj);

        assertFalse(msj.getIdMensaje() == paq.getIdMensaje());
    }
}
