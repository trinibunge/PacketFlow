package PacketFlowTests.MensajeTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Red;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase MensajeTest
 *
 * tests para validar la correcta fragmentación de mensajes en paquetes.
 * Verifica que los mensajes se dividan correctamente según el tamaño máximo de paquete,
 * que cada paquete mantenga su ID de mensaje, y que se cumplan casos bordes.
 *
 * Casos probados:
 * - Fragmentación normal de mensajes
 * - Identificación correcta de paquetes
 * - Caso borde: fragmentación máxima
 * - Caso borde: un solo paquete
 * - Verificación de orden y propiedades de paquetes
 */
public class MensajeTest {

    /**
     * Test: Fragmentación Correcta
     *
     * Objetivo del test: verificar que la fragmentación divide correctamente
     * el mensaje en paquetes según el tamaño máximo permitido.
     *
     * Validaciones:
     * - Cantidad correcta de paquetes generados
     * - Espacio disponible en la red después de la fragmentación
     * - Cada paquete respeta el tamaño máximo
     * - Múltiples mensajes se fragmentan independientemente
     */
    @Test
    void testFragmentacionCorrecta() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "hola", 1);
        Mensaje msj = red.buscarMensaje(1);

        assertEquals(2, msj.getPaquetes().size());

        int espacioDisponible = red.getCapacidadMax() - red.getEnTransito().size();

        assertEquals(8, espacioDisponible);

        for (Paquete p : msj.getPaquetes()) {
            assertTrue(p.getContenido().length() <= red.getTamanioMaxPaquete());
        }

        red.crearMensaje(2, "todobien", 1);
        Mensaje newMsj = red.buscarMensaje(2);

        // volvemos a validar cantidad de paquetes que fue dividido el contenido del msj
        assertEquals(4, newMsj.getPaquetes().size());
        assertTrue(espacioDisponible > 0);
        red.consultarEstado();
    }

    /**
     * Test: Todos Mismo ID Un Solo Paquete
     *
     * Objetivo del test: verificar que todos los paquetes de un mensaje
     * tienen el mismo ID de mensaje.
     *
     * Validaciones:
     * - Cada paquete tiene el ID correcto del mensaje
     */
    @Test
    void testTodosMismoIdUnSoloPaquete() {

        Red red = new Red(2, 10);
        red.crearMensaje(1, "test", 1);
        Mensaje msj = red.buscarMensaje(1);

        // nos aseguramos que todos los paquetes cuenten con el mismo id
        for (Paquete p : msj.getPaquetes()) {
            assertEquals(1, p.getIdMensaje());
        }
    }

    /**
     * Test: Mismo ID Varios Paquetes
     *
     * Objetivo del test: verificar que múltiples mensajes mantienen su ID
     * en cada uno de sus paquetes de forma independiente.
     *
     * Validaciones:
     * - Cada paquete de cada mensaje tiene el ID correspondiente
     * - No hay confusión de IDs entre mensajes diferentes
     */
    @Test
    void testMismoIdVariosPaquetes() {
        Red red = new Red(2, 50);
        red.crearMensaje(1, "test", 1);
        red.crearMensaje(34, "comoestas", 1);
        red.crearMensaje(78, "juantesteando", 1);

        for (int i = 0; i < red.getMensajes().size(); i++) {
            Mensaje msj = red.getMensajes().get(i);
            System.out.println(red.getMensajes().get(i).getIdMensaje());
            for (Paquete p : msj.getPaquetes()) {
                assertEquals(msj.getIdMensaje(), p.getIdMensaje());
            }
        }
    }

    // CASOS BORDES

    /**
     * Test: Fragmentación Máxima
     *
     * Objetivo del test: verificar que cuando cada carácter es un paquete
     * la fragmentación se realiza correctamente.
     *
     * Validaciones:
     * - Cantidad de paquetes = tamaño del mensaje
     * - Cada paquete tiene exactamente 1 carácter
     * - Todos los paquetes mantienen el ID del mensaje
     * - Cada paquete conoce la cantidad total de paquetes
     */
    @Test
    void testFragmentacionMaxima() {

        Red red = new Red(1, 10);
        red.crearMensaje(1, "paquetesd1", 1);
        Mensaje msj = red.buscarMensaje(1);
        assertEquals(10, msj.getPaquetes().size());
        for (Paquete p : msj.getPaquetes()) {
            assertEquals(10, p.getCantPaquetes()); // cada paquete sabe que hay 10 paquetes
            assertEquals(1, p.getContenido().length()); // cada paquete de largo 1 (1 solo caracter)
            assertEquals(msj.getIdMensaje(), p.getIdMensaje()); // cada paquete tiene el mismo id que el id del mensaje
        }
    }

    /**
     * Test: Mensaje Un Solo Paquete
     *
     * Objetivo del test: verificar que cuando el mensaje cabe completamente
     * en un paquete, no se fragmenta y se mantiene intacto.
     *
     * Validaciones:
     * - Cantidad de paquetes = 1
     * - El contenido del paquete es igual al contenido original del mensaje
     */
    @Test
    void testMensajeUnSoloPaquete() {
        Red red = new Red(10, 10);
        red.crearMensaje(1, "test", 1);
        Mensaje msj = red.buscarMensaje(1);
        assertEquals(1, msj.getPaquetes().size()); // cantidad de paquetes = 1
        assertEquals("test", msj.getPaquetes().get(0).getContenido());
    }

    /**
     * Test: Fragmentación Y Orden Correcto
     *
     * Objetivo del test: verificar que la fragmentación mantiene el orden
     * correcto de los paquetes y todas sus propiedades son consistentes.
     *
     * Validaciones:
     * - Cantidad correcta de paquetes generados
     * - Tamaño de cada paquete <= tamaño máximo permitido
     * - Orden secuencial de paquetes (1, 2, 3, ...)
     * - Todos los paquetes tienen el mismo idMensaje
     * - Cada paquete conoce la cantidad total de paquetes del mensaje
     */
    @Test
    void testFragmentacionYOrdenCorrecto() {
        Red red = new Red(3, 30);
        red.crearMensaje(1, "TesteandoQueEstenEnOrden", 1);
        Mensaje msj = red.buscarMensaje(1);
        int contador = 1;
        for (Paquete p : msj.getPaquetes()) {
            assertEquals(contador, p.getNumero()); // orden de los paquetes (1,2,3,...)
            assertTrue(p.getContenido().length() <= red.getTamanioMaxPaquete()); // tamaño de cada paquete <= tamaño máximo
            assertTrue(msj.getPaquetes().size() == p.getCantPaquetes()); // cada paquete conoce la cantidad total de paquetes
            assertEquals(msj.getIdMensaje(), p.getIdMensaje()); // todos los paquetes tienen el mismo idMensaje
            contador++;
        }
        assertTrue(contador - 1 == msj.getPaquetes().size()); // cantidad correcta de paquetes
    }
}