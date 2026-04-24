package PacketFlowTests.RedTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Red;
import PacketFlow.EstadoPaquete;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase RedTest
 *
 * Tests para validar el funcionamiento correcto de la clase Red.
 * Verifica la gestión de mensajes y paquetes, cambios de estado durante la transmisión,
 * validación de capacidad y manejo de casos excepcionales.
 *
 * Métodos testados:
 * - crearMensaje: crea mensajes y valida capacidad
 * - eliminarMensaje: elimina un mensaje y sus paquetes asociados
 * - recibirPaquete: recibe un paquete de la cola y cambia su estado
 * - enviarPaquetes: envía todos los paquetes en tránsito
 * - buscarMensaje: busca un mensaje por ID
 * - espacioDisponible: calcula el espacio libre en la red
 * - getEnTransito: obtiene la cola de paquetes en tránsito
 */
public class RedTest {

    // TESTS DE CREACIÓN DE MENSAJES

    /**
     * Test: Crear Mensaje Exitosamente
     * <p>
     * Objetivo: verificar que crearMensaje retorna true cuando hay capacidad
     * y que el mensaje se agrega correctamente a la red.
     */
    @Test
    void testCrearMensajeExitoso() {
        Red red = new Red(3, 10);
        boolean creado = red.crearMensaje(1, "Hola Mundo", 1);
        assertTrue(creado);
        assertNotNull(red.buscarMensaje(1));
        assertEquals(1, red.getMensajes().size());
    }

    /**
     * Test: Crear Mensaje Fragmenta Correctamente
     * <p>
     * Objetivo: verificar que el contenido se fragmenta en la cantidad correcta
     * de paquetes según el tamaño máximo definido.
     * <p>
     * "Hola Mundo" tiene 10 caracteres, con tamaño 3 => 4 paquetes (3+3+3+1)
     */
    @Test
    void testCrearMensajeFragmentaCorrectamente() {
        Red red = new Red(3, 10);
        red.crearMensaje(1, "Hola Mundo", 1);
        Mensaje m = red.buscarMensaje(1);
        assertEquals(4, m.getPaquetes().size());
    }

    /**
     * Test: Crear Mensaje Falla Cuando No Hay Capacidad
     * <p>
     * Objetivo: verificar que crearMensaje retorna false cuando la cantidad
     * de paquetes necesarios excede la capacidad disponible de la red.
     * <p>
     * Capacidad 2, tamaño 1, contenido "abcde" => 5 paquetes => no entra
     */
    @Test
    void testCrearMensajeFallaSinCapacidad() {
        Red red = new Red(1, 2);
        boolean creado = red.crearMensaje(1, "abcde", 1);
        assertFalse(creado);
        assertNull(red.buscarMensaje(1));
        assertEquals(0, red.getEnTransito().size());
    }

    /**
     * Test: Crear Mensaje Que Llena Exactamente La Red
     * <p>
     * Objetivo: verificar que se puede crear un mensaje cuyos paquetes ocupen
     * exactamente toda la capacidad de la red (caso límite).
     */
    @Test
    void testCrearMensajeLlenaExactamenteLaRed() {
        Red red = new Red(2, 3);
        boolean creado = red.crearMensaje(1, "abcdef", 1); // 6 chars / 2 = 3 paquetes
        assertTrue(creado);
        assertEquals(3, red.getEnTransito().size());
        assertEquals(0, red.espacioDisponible());
    }

    /**
     * Test: Segundo Mensaje Es Rechazado Si Excede Capacidad
     * <p>
     * Objetivo: verificar que después de cargar un mensaje que casi llena la red,
     * un segundo mensaje que la satura es rechazado.
     */
    @Test
    void testSegundoMensajeRechazadoPorCapacidad() {
        Red red = new Red(2, 3);
        red.crearMensaje(1, "abcd", 1); // 2 paquetes
        boolean creado = red.crearMensaje(2, "efgh", 2); // necesita 2, hay 1 libre
        assertFalse(creado);
        assertNull(red.buscarMensaje(2));
    }

    // TESTS DE BÚSQUEDA

    /**
     * Test: Buscar Mensaje Existente
     */
    @Test
    void testBuscarMensajeExistente() {
        Red red = new Red(5, 10);
        red.crearMensaje(7, "test", 1);
        Mensaje m = red.buscarMensaje(7);
        assertNotNull(m);
        assertEquals(7, m.getIdMensaje());
    }

    /**
     * Test: Buscar Mensaje Inexistente Retorna Null
     */
    @Test
    void testBuscarMensajeInexistente() {
        Red red = new Red(5, 10);
        assertNull(red.buscarMensaje(99));
    }

    // TESTS DE ELIMINACIÓN

    /**
     * Test: Eliminar Mensaje Elimina Mensaje y Paquetes
     */
    @Test
    void testEliminarMensajeEliminaMensajeyPaquetes() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "azucar", 1);
        red.eliminarMensaje(1);
        assertNull(red.buscarMensaje(1));
        assertEquals(0, red.getEnTransito().size());
    }

    /**
     * Test: Eliminar Mensaje Con ID Inválido
     */
    @Test
    void testEliminarMensajeConIdInvalido() {
        Red red = new Red(2, 10);
        assertFalse(red.eliminarMensaje(1));
    }

    /**
     * Test: Eliminar Solo Afecta al Mensaje Indicado
     * <p>
     * Objetivo: verificar que al eliminar un mensaje, los demás mensajes
     * y sus paquetes permanecen intactos.
     */
    @Test
    void testEliminarMensajeNoAfectaOtros() {
        Red red = new Red(3, 20);
        red.crearMensaje(1, "primero", 1);
        red.crearMensaje(2, "segundo", 2);
        red.eliminarMensaje(1);
        assertNull(red.buscarMensaje(1));
        assertNotNull(red.buscarMensaje(2));
        assertTrue(red.getEnTransito().size() > 0);
    }
    // TESTS DE RECEPCIÓN

    /**
     * Test: Cambio de Estado a Recibido
     */
    @Test
    void testCambioDeEstadoARecibido() {
        Red red = new Red(10, 10);
        red.crearMensaje(2, "Te amo", 2);
        Mensaje msj = red.buscarMensaje(2);
        red.recibirPaquete();
        Paquete paq = msj.getPaquetes().get(0);
        assertEquals(EstadoPaquete.RECIBIDO, paq.getEstado());
    }
    /**
     * Test: No Pasa Nada Si No Hay Paquetes y Llamo al Metodo
     */
    @Test
    void testNoPasaNadaSiNoHayPaquetesRecibidosYLlamoAlMetodo() {
        Red red = new Red(2, 10);
        try {
            red.recibirPaquete();
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test: Recibir Paquete Reduce La Cola
     *
     * Objetivo: verificar que al recibir un paquete, la cola en tránsito
     * disminuye en uno.
     */
    @Test
    void testRecibirPaqueteReduceCola() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "abcd", 1); // 2 paquetes
        int antes = red.getEnTransito().size();
        red.recibirPaquete();
        assertEquals(antes - 1, red.getEnTransito().size());
    }
    // TESTS DE ENVÍO MASIVO

    /**
     * Test: Enviar Paquetes Vacía La Cola
     *
     * Objetivo: verificar que enviarPaquetes procesa todos los paquetes
     * en tránsito y deja la cola vacía.
     */
    @Test
    void testEnviarPaquetesVaciaLaCola() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "Hola Mundo", 1);
        red.crearMensaje(2, "Chau", 2);
        red.enviarPaquetes();
        assertEquals(0, red.getEnTransito().size());
    }

    /**
     * Test: Enviar Paquetes Marca Todos Como Recibidos
     *
     * Objetivo: verificar que después de enviar todos los paquetes,
     * todos quedan en estado RECIBIDO.
     */
    @Test
    void testEnviarPaquetesMarcaTodosRecibidos() {
        Red red = new Red(3, 10);
        red.crearMensaje(1, "abcdefghi", 1);
        red.enviarPaquetes();
        Mensaje m = red.buscarMensaje(1);
        for (Paquete p : m.getPaquetes()) {
            assertEquals(EstadoPaquete.RECIBIDO, p.getEstado());
        }
    }

    // TESTS DE ESPACIO DISPONIBLE
    /**
     * Test: Espacio Disponible Inicial Es Capacidad Maxima
     */
    @Test
    void testEspacioDisponibleInicial() {
        Red red = new Red(5, 20);
        assertEquals(20, red.espacioDisponible());
    }

    /**
     * Test: Espacio Disponible Disminuye Al Crear Mensaje
     */
    @Test
    void testEspacioDisponibleDisminuye() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "abcd", 1); // 2 paquetes
        assertEquals(8, red.espacioDisponible());
    }

    /**
     * Test: Espacio Disponible Aumenta Al Recibir Paquetes
     */
    @Test
    void testEspacioDisponibleAumentaAlRecibir() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "abcd", 1); // 2 paquetes
        red.recibirPaquete();
        assertEquals(9, red.espacioDisponible());
    }

    // TEST DE GETTERS
    /**
     * Test: Getters Retornan Los Valores Del Constructor
     */
    @Test
    void testGetters() {
        Red red = new Red(5, 50);
        assertEquals(5, red.getTamanioMaxPaquete());
        assertEquals(50, red.getCapacidadMax());
        assertNotNull(red.getEnTransito());
        assertNotNull(red.getMensajes());
        assertTrue(red.getMensajes().isEmpty());
    }
}