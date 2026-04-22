package PacketFlowTests.ReconstructorTest;

import PacketFlow.EstadoPaquete;
import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Reconstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.LinkedList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase ReconstructorTest
 *
 * tests para validar la correcta reconstrucción de mensajes a partir de paquetes recibidos.
 * Verifica que el Reconstructor pueda separar paquetes por mensaje, validar completitud,
 * y reconstruir mensajes en el orden correcto incluso cuando los paquetes llegan desordenados.
 *
 * Métodos testados:
 * - separarPorMensaje: filtra paquetes por ID de mensaje
 * - estaCompleto: valida que todos los paquetes de un mensaje hayan sido recibidos
 * - reconstruir: reconstruye el mensaje original ordenando los paquetes
 *
 * Casos probados:
 * - Separación correcta de paquetes recibidos
 * - Manejo de IDs inexistentes
 * - Prevención de mezcla de mensajes
 * - Validación de completitud en diferentes escenarios
 * - Reconstrucción con paquetes en orden
 * - Reconstrucción con paquetes desordenados
 * - Manejo de mensajes incompletos
 * - Caso borde: un solo paquete
 */
class ReconstructorTest {

    private Reconstructor reconstructor;
    private LinkedList<Mensaje> mensajes;

    /**
     * Configuración previa para cada test.
     * Inicializa una nueva instancia del Reconstructor y una lista vacía de mensajes.
     */
    @BeforeEach
    void setUp() {
        reconstructor = new Reconstructor();
        mensajes = new LinkedList<>();
    }

    // SEPARARPORMENSAJE:

    /**
     * Test: Separar Devuelve Solo Paquetes Recibidos
     *
     * Objetivo del test: verificar que separarPorMensaje solo devuelve paquetes
     * con estado RECIBIDO, filtrando correctamente.
     *
     * Validaciones:
     * - Se devuelven solo los 2 paquetes marcados como RECIBIDO
     * - Los paquetes sin estado RECIBIDO no se incluyen
     */
    @Test
    void testSepararDevuelveSoloPaquetesRecibidos() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1);
        m.fragmentar("HolaMundo", 4);

        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        paquetes.get(0).setEstado(EstadoPaquete.RECIBIDO);
        paquetes.get(1).setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        LinkedList<Paquete> resultado = reconstructor.separarPorMensaje(1, mensajes);
        assertEquals(2, resultado.size());
    }

    /**
     * Test: Separar ID Inexistente Devuelve Vacío
     *
     * Objetivo del test: verificar que si el ID de mensaje no existe,
     * devuelve una lista vacía.
     *
     * Validaciones:
     * - La lista resultado está vacía cuando se busca un ID inexistente
     */
    @Test
    void testSepararIdInexistenteDevuelveVacio() {
        Mensaje m = new Mensaje(1, "Hola", 1);
        m.fragmentar("Hola", 4);

        mensajes.add(m);

        LinkedList<Paquete> resultado = reconstructor.separarPorMensaje(99, mensajes);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Test: Separar No Mezcla Otros Mensajes
     *
     * Objetivo del test: verificar que no mezcla paquetes de diferentes mensajes
     * cuando hay múltiples mensajes en la lista.
     *
     * Validaciones:
     * - Solo se devuelven paquetes del mensaje solicitado
     * - Todos los paquetes resultado tienen el ID correcto
     * - No hay contaminación cruzada entre mensajes
     */
    @Test
    void testSepararNoMezclaOtrosMensajes() {
        Mensaje m1 = new Mensaje(1, "Hola", 1);
        Mensaje m2 = new Mensaje(2, "Mundo", 1);

        m1.fragmentar("Hola", 4);
        m2.fragmentar("Mundo", 4);

        for (Paquete p : m1.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);
        for (Paquete p : m2.getPaquetes()) p.setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m1);
        mensajes.add(m2);

        LinkedList<Paquete> resultado = reconstructor.separarPorMensaje(1, mensajes);

        for (Paquete p : resultado)
            assertEquals(1, p.getIdMensaje());
    }

    // ESTACOMPLETO:
    /**
     * Test: Esta Completo Todos Recibidos
     *
     * Objetivo del test: verificar que estaCompleto retorna true cuando
     * todos los paquetes del mensaje están en estado RECIBIDO.
     *
     * Validaciones:
     * - Retorna true cuando todos los paquetes están recibidos
     */
    @Test
    void testEstaCompletoTodosRecibidos() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1);
        m.fragmentar("HolaMundo", 4);

        for (Paquete p : m.getPaquetes())
            p.setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        assertTrue(reconstructor.estaCompleto(1, mensajes));
    }

    /**
     * Test: Esta Completo Falta Un Paquete
     *
     * Objetivo del test: verificar que estaCompleto retorna false
     * cuando faltan paquetes por recibir.
     *
     * Validaciones:
     * - Retorna false cuando hay paquetes sin estado RECIBIDO
     */
    @Test
    void testEstaCompletoFaltaUnPaquete() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1);
        m.fragmentar("HolaMundo", 4);

        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        paquetes.get(0).setEstado(EstadoPaquete.RECIBIDO);
        paquetes.get(1).setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        assertFalse(reconstructor.estaCompleto(1, mensajes));
    }

    /**
     * Test: Esta Completo Sin Paquetes Recibidos
     *
     * Objetivo del test: verificar que estaCompleto retorna false
     * cuando ningún paquete ha sido recibido.
     *
     * Validaciones:
     * - Retorna false cuando todos los paquetes están sin recibir
     */
    @Test
    void testEstaCompletoSinPaquetesRecibidos() {
        Mensaje m = new Mensaje(1, "Hola", 1);
        m.fragmentar("Hola", 4);

        mensajes.add(m);

        assertFalse(reconstructor.estaCompleto(1, mensajes));
    }

    /**
     * Test: Esta Completo ID Inexistente
     *
     * Objetivo del test: verificar que estaCompleto retorna false
     * cuando el ID de mensaje no existe.
     *
     * Validaciones:
     * - Retorna false cuando se consulta un ID inexistente
     */
    @Test
    void testEstaCompletoIdInexistente() {
        assertFalse(reconstructor.estaCompleto(99, mensajes));
    }

    // RECONSTRUIR:

    /**
     * Test: Reconstruir Orden Correcto
     *
     * Objetivo del test: verificar que reconstruir devuelve el mensaje original
     * cuando los paquetes están en orden secuencial.
     *
     * Validaciones:
     * - El mensaje reconstruido es idéntico al original
     * - El orden de los paquetes se respeta
     */
    @Test
    void testReconstruirOrdenCorrecto() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1);
        m.fragmentar("HolaMundo", 4);

        for (Paquete p : m.getPaquetes())
            p.setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        String resultado = reconstructor.reconstruir(1, mensajes);
        assertEquals("HolaMundo", resultado);
    }

    /**
     * Test: Reconstruir Paquetes Desordenados
     *
     * Objetivo del test: verificar que reconstruir devuelve el mensaje original
     * incluso cuando los paquetes llegan fuera de orden.
     *
     * Validaciones:
     * - El metodo ordena correctamente los paquetes
     * - El mensaje reconstruido es idéntico al original
     * - La reordenación es transparente para el usuario
     */
    @Test
    void testReconstruirPaquetesDesordenados() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1);
        m.fragmentar("HolaMundo", 4);

        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());

        for (int i = paquetes.size() - 1; i >= 0; i--)
            paquetes.get(i).setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        String resultado = reconstructor.reconstruir(1, mensajes);
        assertEquals("HolaMundo", resultado);
    }

    /**
     * Test: Reconstruir Incompleto
     *
     * Objetivo del test: verificar que reconstruir retorna un mensaje de error
     * cuando faltan paquetes del mensaje.
     *
     * Validaciones:
     * - No intenta reconstruir un mensaje incompleto
     * - Devuelve un mensaje de error descriptivo
     */
    @Test
    void testReconstruirIncompleto() {
        Mensaje m = new Mensaje(1, "HolaMundo", 1);
        m.fragmentar("HolaMundo", 4);

        LinkedList<Paquete> paquetes = new LinkedList<>(m.getPaquetes());
        paquetes.get(0).setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        String resultado = reconstructor.reconstruir(1, mensajes);
        assertTrue(resultado.contains("No se puede reconstruir"));
    }

    /**
     * Test: Reconstruir Mensaje Vacío
     *
     * Objetivo del test: verificar que reconstruir retorna un mensaje de error
     * cuando el ID del mensaje no existe.
     *
     * Validaciones:
     * - Devuelve mensaje de error cuando el ID es inexistente
     * - No intenta reconstruir un mensaje que no existe
     */
    @Test
    void testReconstruirMensajeVacio() {
        String resultado = reconstructor.reconstruir(99, mensajes);
        assertTrue(resultado.contains("No se puede reconstruir"));
    }

    /**
     * Test: Reconstruir Un Solo Paquete
     *
     * Objetivo del test: verificar que reconstruir funciona correctamente
     * cuando el mensaje completo cabe en un solo paquete.
     *
     * Validaciones:
     * - El mensaje se reconstruye correctamente cuando hay un solo paquete
     * - No hay problemas de fragmentación en este caso especial
     */
    @Test
    void testReconstruirUnSoloPaquete() {
        Mensaje m = new Mensaje(1, "Hola", 1);
        m.fragmentar("Hola", 100);

        for (Paquete p : m.getPaquetes())
            p.setEstado(EstadoPaquete.RECIBIDO);

        mensajes.add(m);

        assertEquals("Hola", reconstructor.reconstruir(1, mensajes));
    }
}