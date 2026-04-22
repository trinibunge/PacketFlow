package PacketFlowTests.PaqueteTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.EstadoPaquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase PaqueteTest
 *
 * tests para validar la correcta creación, almacenamiento e información de paquetes.
 * Verifica que los paquetes guarden correctamente sus atributos, que el cambio de estado funcione,
 * y que los IDs se manejen de forma independiente entre el mensaje y el paquete.
 *
 * Casos probados:
 * - Almacenamiento correcto de getters
 * - Cambio de estado durante el ciclo de vida del paquete
 * - Independencia entre ID de mensaje e ID de paquete
 */
public class PaqueteTest {

    private Mensaje msj;
    private Paquete paq;

    /**
     * Configuración previa para cada test.
     * Inicializa un mensaje y un paquete que se usan como base en los tests.
     */
    @BeforeEach
    void setUp() {
        msj = new Mensaje(1, "contenidomsj", 1);
        paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);
    }

    /**
     * Test: Guarda Información Correctamente
     *
     * Objetivo del test: verificar que todos los getters devuelven correctamente
     * la información almacenada en el paquete tras su creación.
     *
     * Validaciones:
     * - getIdMensaje() devuelve el ID correcto
     * - getNumero() devuelve el número de paquete correcto
     * - getCantPaquetes() devuelve la cantidad total correcta
     * - getContenido() devuelve el contenido textual correcto
     * - getEstado() devuelve el estado inicial correcto
     */
    @Test
    void testGuardaInfoCorrectamente() {
        // objetivo del test: verificar los getters
        Mensaje msj = new Mensaje(1, "contenidomsj", 1);
        Paquete paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);

        assertEquals(1, paq.getIdMensaje());
        assertEquals(34, paq.getNumero());
        assertEquals(3, paq.getCantPaquetes());
        assertEquals("cont", paq.getContenido());
        assertEquals(EstadoPaquete.EN_TRANSITO, paq.getEstado());
    }

    /**
     * Test: Cambio de Estado
     *
     * Objetivo del test: verificar que los métodos setter de estado funcionan correctamente
     * y que el paquete puede cambiar entre diferentes estados durante su ciclo de vida.
     *
     * Validaciones:
     * - El estado inicial es en transito
     * - El estado cambia correctamente a recibido
     * - El estado cambia correctamente a perdido
     * - Cada cambio de estado se refleja inmediatamente al consultar con getEstado()
     */
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

    /**
     * Test: ID Mensaje Difiere ID Paquete
     *
     * Objetivo del test: verificar que el ID del mensaje y el ID de paquete
     * son atributos completamente independientes y pueden tener valores diferentes.
     *
     * Validaciones:
     * - El ID del mensaje es diferente del ID del paquete
     * - Cada uno se gestiona de forma independiente sin confusión
     */
    @Test
    void testIdMsjDifiereIDPaquete() {
        Mensaje msj = new Mensaje(89, "5libertadores", 2);
        Paquete paq = new Paquete(1, 34, 3, "cont", EstadoPaquete.EN_TRANSITO, msj);

        assertNotEquals(msj.getIdMensaje(), paq.getIdMensaje());
    }
}