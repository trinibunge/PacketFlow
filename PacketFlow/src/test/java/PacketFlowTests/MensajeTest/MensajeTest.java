package MensajeTest;

import PacketFlow.Mensaje;
import PacketFlow.Paquete;
import PacketFlow.Red;
import PacketFlow.Reconstructor;
import PacketFlow.EstadoPaquete;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;


public class MensajeTest extends TestCase {

    public void testFragmentacionCorrecta() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "hola", 1);
        Mensaje msj = red.buscarMensaje(1);

        //control de cantidad de paquetes
        assertEquals(2, msj.getPaquetes().size());

        //validamos que la cantidad de disponibles sea la adecuada
        int espacioDisponible = red.getCapacidadMax() - red.getEnTransito().size();

        assertEquals(8, espacioDisponible);
        //control del largo de cada paquete
        for (Paquete p : msj.getPaquetes()) {
            assertTrue(p.getContenido().length() <= red.getTamanioMaxPaquete());
        }

        //creamos otro mensaje
        red.crearMensaje(2, "todobien", 1);
        Mensaje newMsj = red.buscarMensaje(2);

        //volvemos a validar cantidad de paquetes que fue dividido el contenido del msj
        assertEquals(4, newMsj.getPaquetes().size());
        assertTrue(espacioDisponible > 0);
        red.consultarEstado();
    }


    public void testTodosMismoIdUnSoloPaquete() {
        Red red = new Red(2, 10);
        red.crearMensaje(1, "test", 1);
        Mensaje msj = red.buscarMensaje(1);

        //nos aserguramos que todos los paquetes cuenten con el mismo id
        for (Paquete p : msj.getPaquetes()) {
            assertEquals(1, p.getIdMensaje());
        }
    }

    public void testMismoIdVariosPaquetes() {
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

    //CASOS BORDES


   /* caso en que cada caracter es un paquete
    verificar:
    -cantidad de paquetes = tamaño del mensaje
    -cada paquete tiene 1 carácter
    -todos mantienen el ID del mensaje*/
    public void testFragmentacionMaxima() {
        Red red = new Red(1, 10);
        red.crearMensaje(1, "paquetesd1", 1);
        Mensaje msj = red.buscarMensaje(1);
        assertEquals(10, msj.getPaquetes().size());
        for(Paquete p: msj.getPaquetes()){
            assertEquals(10,p.getCantPaquetes()); //cada paquete sabe que hay 10 paquetes
            assertEquals(1,p.getContenido().length()); //cada paquete de largo 1 (1 solo caracter)
            assertEquals(msj.getIdMensaje(),p.getIdMensaje()); //cada paquete tiene el mismo id que el id del mensaje
        }
    }
    /* caso en que el mensaje entra en un solo paquete
    verificar:
        -cantidad de paquetes = 1
        -el contenido del paquete = contenido original
     */
    public void testMensajeUnSoloPaquete() {
        Red red = new Red(10, 10);
        red.crearMensaje(1, "test", 1);
        Mensaje msj = red.buscarMensaje(1);
        assertEquals(1,msj.getPaquetes().size()); //cantidad de paquetes = 1
        assertEquals("test",msj.getPaquetes().peek().getContenido()); //contenido del paquete igual que el msj original
    }

    /* Caso normal:
     VERIFICAR:
     - cantidad correcta de paquetes
     - tamaño de cada paquete <= tamaño máximo
     - orden de los paquetes (1,2,3,...)
     - todos los paquetes tienen el mismo idMensaje
     - cada paquete conoce la cantidad total de paquetes
  */
    public void testFragmentacioYOrdenCorrecto(){
        Red red = new Red(3,30);
        red.crearMensaje(1,"TesteandoQueEstenEnOrden",1);
        Mensaje msj = red.buscarMensaje(1);
        int contador = 1;
        for (Paquete p: msj.getPaquetes()){
            assertEquals(contador,p.getNumero()); //orden de los paquetes (1,2,3,...)
            assertTrue(p.getContenido().length() <= red.getTamanioMaxPaquete() ); //tamaño de cada paquete <= tamaño máximo
            assertTrue(msj.getPaquetes().size() == p.getCantPaquetes()); //cada paquete conoce la cantidad total de paquetes
            assertEquals(msj.getIdMensaje(),p.getIdMensaje()); //todos los paquetes tienen el mismo idMensaje
            contador++;
        }
        assertTrue(contador - 1 == msj.getPaquetes().size()); //cantidad correcta de paquetes, ajustamos el valor del contador a -1 porque
                                                                        // ese valor es utilizado para la siguiente posicion del array, por ende siempre tiene 1 mas
    }

}
