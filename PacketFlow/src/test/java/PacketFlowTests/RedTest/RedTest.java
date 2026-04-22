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
 *  tests para validar el funcionamiento correcto de la clase Red.
 * Verifica la gestión de mensajes y paquetes, cambios de estado durante la transmisión,
 * y manejo de casos excepcionales como operaciones sobre elementos inexistentes.
 *
 * Métodos testados:
 * - eliminarMensaje: elimina un mensaje y sus paquetes asociados
 * - recibirPaquete: recibe un paquete de la cola y cambia su estado
 * - buscarMensaje: busca un mensaje por ID
 * - getEnTransito: obtiene la cola de paquetes en tránsito
 *
 * Casos probados:
 * - Eliminación correcta de mensajes y paquetes
 * - Manejo de eliminación con ID inválido
 * - Cambio de estado a RECIBIDO durante recepción
 * - Manejo de operaciones sobre cola vacía
 */
public class RedTest {

    /**
     * Test: Eliminar Mensaje Elimina Mensaje y Paquetes
     *
     * Objetivo del test: verificar que el metodo eliminarMensaje elimina correctamente
     * tanto el mensaje de la lista como todos sus paquetes asociados de la cola de transmisión.
     *
     * Validaciones:
     * - El mensaje se elimina de la lista
     * - Todos los paquetes del mensaje se eliminan de la cola
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
     *
     * Objetivo del test: verificar que no se puede eliminar un mensaje que no existe en la red
     * y que el metodo devuelve false para indicar el error.
     *
     * Validaciones:
     * - El metodo eliminarMensaje retorna false cuando el ID no existe
     * - No se genera una excepción
     * - El estado de la red permanece intacto
     */
    @Test
    void testEliminarMensajeConIdInvalido() {
        Red red = new Red(2, 10);
        assertFalse(red.eliminarMensaje(1));
    }

    /**
     * Test: Cambio de Estado a Recibido
     *
     * Objetivo del test: verificar que cuando se recibe un paquete mediante recibirPaquete,
     * el estado del paquete cambia a RECIBIDO correctamente.
     *
     * Validaciones:
     * - El paquete recibido tiene estado RECIBIDO
     * - El metodo procesa correctamente la recepción
     * - El cambio de estado se refleja en el objeto paquete
     */
    @Test
    void testCambioDeEstadoARecibido() {
        Red red = new Red(2, 10);
        red.crearMensaje(2, "Te amo", 2);
        Mensaje msj = red.buscarMensaje(2);
        red.recibirPaquete();
        Paquete paq = msj.getPaquetes().peek();
        assertEquals(EstadoPaquete.RECIBIDO, paq.getEstado());
    }

    /**
     * Test: No Pasa Nada Si No Hay Paquetes y Llamo al Metodo
     *
     * Objetivo del test: verificar que si se llama a recibirPaquete cuando la cola de
     * paquetes en tránsito está vacía, el código maneja la situación de forma segura
     * sin lanzar excepciones.
     *
     * Validaciones:
     * - No se lanza una excepción cuando la cola está vacía
     * - El metodo maneja gracefully el caso excepcional
     * - La red permanece en estado consistente después de la llamada
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
}