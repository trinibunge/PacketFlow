package PacketFlow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.LinkedList;
import static org.junit.jupiter.api.Assertions.*;

class ReconstructorTest {

    private Reconstructor reconstructor;
    private LinkedList<Mensaje> mensajes;

    @BeforeEach
    void setUp() {
        reconstructor = new Reconstructor();
        mensajes = new LinkedList<>();
    }

    // separarPorMensaje

    @Test
    void testSepararDevuelveSoloPaquetesRecibidos() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1, 4);
        // marcar solo los recibidos
        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        paquetes.get(0).setEstado(EstadoPaquete.RECIBIDO);
        paquetes.get(1).setEstado(EstadoPaquete.RECIBIDO);
        // el tercero queda EN_TRANSITO
        mensajes.add(m);

        LinkedList<Paquete> resultado = reconstructor.separarPorMensaje(1, mensajes);
        assertEquals(2, resultado.size());
    }

    @Test
    void testSepararIdInexistenteDevuelveVacio() {
        Mensaje m = new Mensaje(1, "Hola", 1, 4);
        mensajes.add(m);

        LinkedList<Paquete> resultado = reconstructor.separarPorMensaje(99, mensajes);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testSepararNoMezclaOtrosMensajes() {
        Mensaje m1 = new Mensaje(1, "Hola", 1, 4);
        Mensaje m2 = new Mensaje(2, "Mundo", 1, 4);
        for (Paquete p : m1.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);
        for (Paquete p : m2.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);
        mensajes.add(m1);
        mensajes.add(m2);

        LinkedList<Paquete> resultado = reconstructor.separarPorMensaje(1, mensajes);
        for (Paquete p : resultado)
            assertEquals(1, p.getIdMensaje());
    }

    // estaCompleto

    @Test
    void testEstaCompletoTodosRecibidos() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1, 4);
        for (Paquete p : m.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);
        mensajes.add(m);

        assertTrue(reconstructor.estaCompleto(1, mensajes));
    }

    @Test
    void testEstaCompletoFaltaUnPaquete() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1, 4);
        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        paquetes.get(0).setEstado(EstadoPaquete.RECIBIDO);
        paquetes.get(1).setEstado(EstadoPaquete.RECIBIDO);
        // paquete 3 queda EN_TRANSITO
        mensajes.add(m);

        assertFalse(reconstructor.estaCompleto(1, mensajes));
    }

    @Test
    void testEstaCompletoSinPaquetesRecibidos() {
        Mensaje m = new Mensaje(1, "Hola", 1, 4);
        // todos quedan EN_TRANSITO
        mensajes.add(m);

        assertFalse(reconstructor.estaCompleto(1, mensajes));
    }

    @Test
    void testEstaCompletoIdInexistente() {
        assertFalse(reconstructor.estaCompleto(99, mensajes));
    }

    // reconstruir

    @Test
    void testReconstruirOrdenCorrecto() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1, 4);
        for (Paquete p : m.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);
        mensajes.add(m);

        String resultado = reconstructor.reconstruir(1, mensajes);
        assertEquals("HolaMundo", resultado);
    }

    @Test
    void testReconstruirPaquetesDesordenados() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1, 4);
        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        // marcar recibidos pero agregar en orden inverso
        for (int i = paquetes.size() - 1; i >= 0; i--)
            paquetes.get(i).setEstado(EstadoPaquete.RECIBIDO);
        mensajes.add(m);

        String resultado = reconstructor.reconstruir(1, mensajes);
        assertEquals("HolaMundo", resultado);
    }

    @Test
    void testReconstruirIncompleto() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1, 4);
        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        paquetes.get(0).setEstado(EstadoPaquete.RECIBIDO);
        // faltan los demás
        mensajes.add(m);

        String resultado = reconstructor.reconstruir(1, mensajes);
        assertTrue(resultado.contains("No se puede reconstruir"));
    }

    @Test
    void testReconstruirMensajeVacio() {
        String resultado = reconstructor.reconstruir(99, mensajes);
        assertTrue(resultado.contains("No se puede reconstruir"));
    }

    @Test
    void testReconstruirUnSoloPaquete() {
        Mensaje m = new Mensaje(1, "Hola", 1, 100);
        for (Paquete p : m.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);
        mensajes.add(m);

        assertEquals("Hola", reconstructor.reconstruir(1, mensajes));
    }
}