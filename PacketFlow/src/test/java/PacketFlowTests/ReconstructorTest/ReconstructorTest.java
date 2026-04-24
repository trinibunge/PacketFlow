package PacketFlowTests.ReconstructorTest;

import PacketFlow.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase ReconstructorTest
 *
 * Tests para validar el funcionamiento correcto de la clase Reconstructor.
 * Verifica la separación de paquetes, la validación de completitud, la reconstrucción
 * de mensajes por ID y siguiente automático y el ordenamiento por prioridad.
 *
 * Métodos testados:
 * - separarPorMensaje: filtra paquetes RECIBIDOS de un mensaje específico
 * - estaCompleto: verifica si todos los paquetes fueron recibidos
 * - reconstruir: reconstruye un mensaje y lo elimina de la red
 * - reconstruirSiguiente: reconstruye el mensaje de mayor prioridad y antigüedad
 * - ordenarPorPrioridad: ordena mensajes por prioridad
 */
public class ReconstructorTest {

    /**
     * Helper: marca todos los paquetes de un mensaje como RECIBIDO.
     */
    private void recibirTodos(Red red, int idMensaje) {
        Mensaje m = red.buscarMensaje(idMensaje);
        for (Paquete p : m.getPaquetes()) {
            p.setEstado(EstadoPaquete.RECIBIDO);
            red.getEnTransito().remove(p);
        }
    }

    // TESTS DE estaCompleto()
    /**
     * Test: Mensaje Sin Paquetes Recibidos No Está Completo
     */
    @Test
    void testMensajeSinPaquetesRecibidosNoEstaCompleto() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "Hola", 1);
        Reconstructor r = new Reconstructor();
        assertFalse(r.estaCompleto(1, red.getMensajes()));
    }

    /**
     * Test: Mensaje Con Todos Los Paquetes Recibidos Está Completo
     */
    @Test
    void testMensajeCompletoEstaCompleto() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "Hola", 1);
        recibirTodos(red, 1);
        Reconstructor r = new Reconstructor();
        assertTrue(r.estaCompleto(1, red.getMensajes()));
    }

    /**
     * Test: Mensaje Con Algunos Paquetes Recibidos No Está Completo
     */
    @Test
    void testMensajeIncompletoNoEstaCompleto() {
        Red red = new Red(1, 10);
        red.crearMensaje(1, "abcd", 1); // 4 paquetes
        // Recibir solo 2
        Mensaje m = red.buscarMensaje(1);
        int contador = 0;
        for (Paquete p : m.getPaquetes()) {
            if (contador < 2) {
                p.setEstado(EstadoPaquete.RECIBIDO);
                contador++;
            }
        }
        Reconstructor r = new Reconstructor();
        assertFalse(r.estaCompleto(1, red.getMensajes()));
    }

    // TESTS DE separarPorMensaje()

    /**
     * Test: Separar Por Mensaje Devuelve Solo Paquetes Recibidos
     */
    @Test
    void testSepararPorMensajeSoloRecibidos() {
        Red red = new Red(1, 10);
        red.crearMensaje(1, "abc", 1); // 3 paquetes
        // Recibir solo 1
        Mensaje m = red.buscarMensaje(1);
        m.getPaquetes().get(0).setEstado(EstadoPaquete.RECIBIDO);

        Reconstructor r = new Reconstructor();
        LinkedList<Paquete> resultado = r.separarPorMensaje(1, red.getMensajes());
        assertEquals(1, resultado.size());
    }

    /**
     * Test: Separar Por Mensaje Inexistente Devuelve Lista Vacía
     */
    @Test
    void testSepararPorMensajeInexistenteVacio() {
        Red red = new Red(2, 10);
        Reconstructor r = new Reconstructor();
        LinkedList<Paquete> resultado = r.separarPorMensaje(99, red.getMensajes());
        assertTrue(resultado.isEmpty());
    }

    // TESTS DE reconstruir()

    /**
     * Test: Reconstruir Mensaje Completo Devuelve Contenido Original
     */
    @Test
    void testReconstruirMensajeCompleto() {
        Red red = new Red(3, 10);
        red.crearMensaje(1, "Hola Mundo", 1);
        recibirTodos(red, 1);

        Reconstructor r = new Reconstructor();
        String resultado = r.reconstruir(1, red);
        assertEquals("Hola Mundo", resultado);
    }

    /**
     * Test: Reconstruir Mensaje Elimina De La Red
     */
    @Test
    void testReconstruirEliminaDeRed() {
        Red red = new Red(3, 10);
        red.crearMensaje(1, "Hola", 1);
        recibirTodos(red, 1);

        Reconstructor r = new Reconstructor();
        r.reconstruir(1, red);
        assertNull(red.buscarMensaje(1));
    }

    /**
     * Test: Reconstruir Mensaje Incompleto Retorna Null
     */
    @Test
    void testReconstruirIncompletoRetornaNull() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "Hola Mundo", 1);
        // No recibir ningún paquete

        Reconstructor r = new Reconstructor();
        assertNull(r.reconstruir(1, red));
    }

    /**
     * Test: Reconstruir Mensaje Incompleto No Lo Elimina
     */
    @Test
    void testReconstruirIncompletoNoElimina() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "Hola Mundo", 1);

        Reconstructor r = new Reconstructor();
        r.reconstruir(1, red);
        assertNotNull(red.buscarMensaje(1));
    }

    /**
     * Test: Reconstruir Conserva El Orden De Los Paquetes
     *
     * Aunque internamente la PriorityQueue puede reordenar paquetes por prioridad,
     * la reconstrucción debe ordenarlos por número de paquete.
     */
    @Test
    void testReconstruirConservaOrden() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "ABCDEF", 1);
        recibirTodos(red, 1);

        Reconstructor r = new Reconstructor();
        String resultado = r.reconstruir(1, red);
        assertEquals("ABCDEF", resultado);
    }

    // TESTS DE reconstruirSiguiente()

    /**
     * Test: Reconstruir Siguiente Sin Mensajes Devuelve Null
     */
    @Test
    void testReconstruirSiguienteSinMensajesNull() {
        Red red = new Red(2, 10);
        Reconstructor r = new Reconstructor();
        assertNull(r.reconstruirSiguiente(red));
    }

    /**
     * Test: Reconstruir Siguiente Sin Mensajes Completos Devuelve Null
     */
    @Test
    void testReconstruirSiguienteSinCompletosNull() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "Hola", 1);
        red.crearMensaje(2, "Chau", 2);
        // No recibir ninguno

        Reconstructor r = new Reconstructor();
        assertNull(r.reconstruirSiguiente(red));
    }

    /**
     * Test: Reconstruir Siguiente Elige El De Mayor Prioridad
     *
     * Si hay un mensaje de prioridad 1 y otro de prioridad 3, ambos completos,
     * debe reconstruir primero el de prioridad 1.
     */
    @Test
    void testReconstruirSiguienteEligeMayorPrioridad() {
        Red red = new Red(3, 20);
        red.crearMensaje(1, "Baja", 3);
        red.crearMensaje(2, "Alta", 1);
        recibirTodos(red, 1);
        recibirTodos(red, 2);

        Reconstructor r = new Reconstructor();
        String resultado = r.reconstruirSiguiente(red);
        assertTrue(resultado.contains("Mensaje: 2"));
        assertTrue(resultado.contains("Alta"));
    }

    /**
     * Test: Reconstruir Siguiente Entre Iguales Prioridades Elige El Mas Antiguo
     */
    @Test
    void testReconstruirSiguienteEligeMasAntiguo() {
        Red red = new Red(3, 20);
        red.crearMensaje(1, "Primero", 2);
        red.crearMensaje(2, "Segundo", 2);
        recibirTodos(red, 1);
        recibirTodos(red, 2);

        Reconstructor r = new Reconstructor();
        String resultado = r.reconstruirSiguiente(red);
        assertTrue(resultado.contains("Mensaje: 1"));
        assertTrue(resultado.contains("Primero"));
    }

    /**
     * Test: Reconstruir Siguiente Elimina El Mensaje De La Red
     */
    @Test
    void testReconstruirSiguienteEliminaDeRed() {
        Red red = new Red(3, 10);
        red.crearMensaje(1, "Hola", 1);
        recibirTodos(red, 1);

        Reconstructor r = new Reconstructor();
        r.reconstruirSiguiente(red);
        assertNull(red.buscarMensaje(1));
    }

    /**
     * Test: Llamadas Sucesivas a Siguiente Procesan Por Prioridad
     */
    @Test
    void testReconstruirSiguienteSucesivo() {
        Red red = new Red(3, 30);
        red.crearMensaje(1, "Tres", 3);
        red.crearMensaje(2, "Uno", 1);
        red.crearMensaje(3, "Dos", 2);
        recibirTodos(red, 1);
        recibirTodos(red, 2);
        recibirTodos(red, 3);

        Reconstructor r = new Reconstructor();
        String r1 = r.reconstruirSiguiente(red);
        String r2 = r.reconstruirSiguiente(red);
        String r3 = r.reconstruirSiguiente(red);

        assertTrue(r1.contains("Uno"));   // prioridad 1
        assertTrue(r2.contains("Dos"));   // prioridad 2
        assertTrue(r3.contains("Tres"));  // prioridad 3
    }

    // TESTS DE ordenarPorPrioridad()

    /**
     * Test: Ordenar Por Prioridad Devuelve Mensajes en Orden Correcto
     */
    @Test
    void testOrdenarPorPrioridad() {
        Red red = new Red(3, 30);
        red.crearMensaje(1, "Baja", 3);
        red.crearMensaje(2, "Alta", 1);
        red.crearMensaje(3, "Media", 2);

        Reconstructor r = new Reconstructor();
        PriorityQueue<Mensaje> ordenados = r.ordenarPorPrioridad(red.getMensajes());

        assertEquals(1, ordenados.poll().getPrioridad());
        assertEquals(2, ordenados.poll().getPrioridad());
        assertEquals(3, ordenados.poll().getPrioridad());
    }

    /**
     * Test: Ordenar Por Prioridad Con Lista Vacía Devuelve Cola Vacía
     */
    @Test
    void testOrdenarPorPrioridadVacia() {
        Red red = new Red(2, 10);
        Reconstructor r = new Reconstructor();
        PriorityQueue<Mensaje> ordenados = r.ordenarPorPrioridad(red.getMensajes());
        assertTrue(ordenados.isEmpty());
    }
}