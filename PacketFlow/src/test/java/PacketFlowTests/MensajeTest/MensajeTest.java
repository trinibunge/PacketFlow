package PacketFlowTests.MensajeTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Red;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MensajeTest {

    @Test
    void testFragmentacionCorrecta() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "hola", 1);
        Mensaje msj = red.buscarMensaje(1);

        // control de cantidad de paquetes
        assertEquals(2, msj.getPaquetes().size());

        // validamos que la cantidad de disponibles sea la adecuada
        int espacioDisponible = red.getCapacidadMax() - red.getEnTransito().size();

        assertEquals(8, espacioDisponible);

        // control del largo de cada paquete
        for (Paquete p : msj.getPaquetes()) {
            assertTrue(p.getContenido().length() <= red.getTamanioMaxPaquete());
        }

        // creamos otro mensaje
        red.crearMensaje(2, "todobien", 1);
        Mensaje newMsj = red.buscarMensaje(2);

        // volvemos a validar cantidad de paquetes que fue dividido el contenido del msj
        assertEquals(4, newMsj.getPaquetes().size());
        assertTrue(espacioDisponible > 0);
        red.consultarEstado();
    }

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

    @Test
    void testFragmentacionMaxima() {
        // caso en que cada caracter es un paquete
        // verificar:
        // -cantidad de paquetes = tamaño del mensaje
        // -cada paquete tiene 1 carácter
        // -todos mantienen el ID del mensaje

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

    @Test
    void testMensajeUnSoloPaquete() {
        // caso en que el mensaje entra en un solo paquete
        // verificar:
        // -cantidad de paquetes = 1
        // -el contenido del paquete = contenido original

        Red red = new Red(10, 10);
        red.crearMensaje(1, "test", 1);
        Mensaje msj = red.buscarMensaje(1);
        assertEquals(1, msj.getPaquetes().size()); // cantidad de paquetes = 1
        assertEquals("test", msj.getPaquetes().peek().getContenido()); // contenido del paquete igual que el msj original
    }

    @Test
    void testFragmentacionYOrdenCorrecto() {
        // Caso normal:
        // VERIFICAR:
        // - cantidad correcta de paquetes
        // - tamaño de cada paquete <= tamaño máximo
        // - orden de los paquetes (1,2,3,...)
        // - todos los paquetes tienen el mismo idMensaje
        // - cada paquete conoce la cantidad total de paquetes

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