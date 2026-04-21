package PacketFlowTests.PaqueteTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.EstadoPaquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaqueteTest {

    private Mensaje msj;
    private Paquete paq;

    @BeforeEach
    void setUp() {
        msj = new Mensaje(1, "contenidomsj", 1);
        paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);
    }

    @Test
    void testGuardaInfoCorrectamente() {
        Mensaje msj = new Mensaje(1, "contenidomsj", 1);
        Paquete paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);

        assertEquals(1, paq.getIdMensaje());
        assertEquals(34, paq.getNumero());
        assertEquals(3, paq.getCantPaquetes());
        assertEquals("cont", paq.getContenido());
        assertEquals(EstadoPaquete.EN_TRANSITO, paq.getEstado());
    }

    @Test
    void testCambioDeEstado() {
        Mensaje msj = new Mensaje(1, "contenidomsj", 1);
        Paquete paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);

        assertEquals(EstadoPaquete.EN_TRANSITO, paq.getEstado());

        // se simula que el paquete fue enviado, por ende deberia cambiar su estado
        paq.setEstado(EstadoPaquete.RECIBIDO);
        assertEquals(EstadoPaquete.RECIBIDO, paq.getEstado());

        // se simula la perdida del paquete
        paq.setEstado(EstadoPaquete.PERDIDO);
        assertEquals(EstadoPaquete.PERDIDO, paq.getEstado());
    }

    @Test
    void testIdMsjDifiereIDPaquete() {
        Mensaje msj = new Mensaje(89, "5libertadores", 2);
        Paquete paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);

        assertNotEquals(msj.getIdMensaje(), paq.getIdMensaje());
    }
}
