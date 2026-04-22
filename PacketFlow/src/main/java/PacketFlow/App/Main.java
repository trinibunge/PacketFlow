package PacketFlow.App;

import PacketFlow.*;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Clase Main
 *
 * Proporciona la interfaz de usuario para el simulador PacketFlow.
 * Implementa un menú interactivo que permite:
 * - Crear mensajes manualmente o cargar desde CSV
 * - Enviar y recibir paquetes
 * - Reconstruir mensajes
 * - Consultar estado de la red
 */
public class Main {
    private static Red red;
    private static Scanner sc;
    private static Reconstructor reconstructor;

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        reconstructor = new Reconstructor();

        // Inicializar la red
        inicializarRed(sc);

        // Menú principal
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    crearMensajeManual(sc);
                    break;
                case 2:
                    enviarPaquetes();
                    break;
                case 3:
                    enviarPaquetesDeMensaje(sc);
                    break;
                case 4:
                    recibirPaquete();
                    break;
                case 5:
                    reconstruirMensaje(sc);
                    break;
                case 6:
                    listarEstado();
                    break;
                case 7:
                    cargarCSV(sc);
                    break;
                case 8:
                    System.out.println("\nGracias por elegir PacketFlow, Nos vemos pronto!\n");
                    break;
                default:
                    System.out.println(" Opción inválida. Intenta de nuevo.");
            }
        } while (opcion != 8);

        sc.close();
    }

    /**
     * Inicializa la red solicitando capacidad y tamaño máximo de paquete.
     */
    private static void inicializarRed(Scanner scanner) {
        System.out.println("\n!Bienvenido a PacketFlow¡ Necesitamos que inicialize la red para comenzar la prueba. \n");
        System.out.print("Primero, seleccione la capacidad máxima de caracteres que puede contener cada paquete: ");
        int tamanioMax = scanner.nextInt();

        System.out.print("Ahora, seleccione la capacidad maxima de paquetes de la red: ");
        int capacidadMax = scanner.nextInt();

        scanner.nextLine();

        red = new Red(tamanioMax, capacidadMax);
        System.out.println("\n!Felicitaciones¡ La red fue implementada correctamente.\n");
    }

    /**
     * Muestra el menú principal.
     */
    private static void mostrarMenu() {
        System.out.println("\n MENÚ PRINCIPAL \n");
        System.out.println("1. Crear mensaje manualmente");
        System.out.println("2. Enviar todos los paquetes");
        System.out.println("3. Enviar paquetes de un mensaje específico");
        System.out.println("4. Enviar un paquete especifico");
        System.out.println("5. Reconstruir mensaje");
        System.out.println("6. Consultar estado de la red");
        System.out.println("7. Cargar mensajes desde CSV");
        System.out.println("8. Salir");
        System.out.print("\nSelecciona una opción: ");
    }

    /**
     * Lee y valida la opción del usuario.
     */
    private static int leerOpcion() {
        try {
            int opcion = sc.nextInt();
            sc.nextLine();
            return opcion;
        } catch (java.util.InputMismatchException e) {
            sc.nextLine();
            return -1;
        }
    }

    /**
     * Crea un mensaje manualmente solicitando datos al usuario.
     */
    private static void crearMensajeManual(Scanner scanner) {
        System.out.println("\nCrear Mensaje \n");
        System.out.print("ID del mensaje: ");
        int id = scanner.nextInt();

        System.out.print("Prioridad: ");
        int prioridad = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Contenido del mensaje: ");
        String contenido = scanner.nextLine();

        try {
            red.crearMensaje(id, contenido, prioridad);
            System.out.println(" Mensaje creado y fragmentado correctamente.");
            System.out.println("  Paquetes generados: " + red.getMensajes().getLast().getPaquetes().size());
        } catch (Exception e) {
            System.out.println("Error al crear el mensaje: " + e.getMessage());
        }
    }

    /**
     * Envía todos los paquetes en tránsito.
     */
    private static void enviarPaquetes() {
        System.out.println();
        red.enviarPaquetes();
    }

    /**
     * Envía paquetes de un mensaje específico.
     */
    private static void enviarPaquetesDeMensaje(Scanner scanner) {
        System.out.println("\nEnviar paquetes de un mensaje\n");
        System.out.print("ID del mensaje: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Mensaje m = red.buscarMensaje(id);
        if (m == null) {
            System.out.println("Error: Mensaje no encontrado.");
            return;
        }

        System.out.println("\nEnviando paquetes del mensaje " + id + "...");
        int enviados = 0;
        for (Paquete p : m.getPaquetes()) {
            if (p.getEstado() == EstadoPaquete.EN_TRANSITO) {
                p.setEstado(EstadoPaquete.RECIBIDO);
                System.out.printf("Enviado: Paquete %d/%d%n", p.getNumero(), p.getCantPaquetes());
                enviados++;
            }
        }
        System.out.println("Total enviados: " + enviados + " paquetes.\n");
    }

    /**
     * Recibe un paquete de la cola de tránsito.
     */
    private static void recibirPaquete() {
        System.out.println();
        red.recibirPaquete();
    }

    /**
     * Reconstruye un mensaje a partir de los paquetes recibidos.
     */
    private static void reconstruirMensaje(Scanner scanner) {
        System.out.println("\nReconstruir Mensaje\n");
        System.out.print("ID del mensaje a reconstruir: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String resultado = reconstructor.reconstruir(id, red.getMensajes());
        System.out.println("\nMensaje reconstruido:");
        System.out.println(resultado);
    }

    /**
     * Consulta y muestra el estado actual de la red.
     */
    private static void listarEstado() {
        System.out.println();
        red.consultarEstado();

        System.out.println("\nEstado de Mensajes\n");
        LinkedList<Mensaje> mensajes = red.getMensajes();

        if (mensajes.isEmpty()) {
            System.out.println("No hay mensajes en la red.");
            return;
        }

        for (Mensaje m : mensajes) {
            boolean completo = reconstructor.estaCompleto(m.getIdMensaje(), mensajes);
            String estado = completo ? "COMPLETO" : "INCOMPLETO";
            int recibidos = 0;

            for (Paquete p : m.getPaquetes()) {
                if (p.getEstado() == EstadoPaquete.RECIBIDO) {
                    recibidos++;
                }
            }

            System.out.printf("Mensaje %d | Prioridad: %d | %s | Paquetes recibidos: %d/%d%n",
                    m.getIdMensaje(), m.getPrioridad(), estado, recibidos, m.getPaquetes().size());
        }
    }

    /**
     * Carga mensajes desde un archivo CSV.
     * Formato esperado: ID,CONTENIDO,PRIORIDAD
     * Ejemplo:
     * 1,Hola mundo,1
     * 2,Comida,3
     */
    private static void cargarCSV(Scanner scanner) {
        System.out.println("\n Cargar archivo\n");
        System.out.print("Ruta del archivo CSV: ");
        String rutaArchivo = scanner.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int contadorCargados = 0;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().isEmpty() || linea.startsWith("ID")) {
                    continue;
                }

                try {

                    String[] partes = linea.split(",", 3); // máximo 3 partes
                    if (partes.length != 3) {
                        System.out.println("Línea ignorada: " + linea);
                        continue;
                    }

                    int id = Integer.parseInt(partes[0].trim());
                    String contenido = partes[1].trim();
                    int prioridad = Integer.parseInt(partes[2].trim());

                    red.crearMensaje(id, contenido, prioridad);
                    contadorCargados++;

                } catch (NumberFormatException e) {
                    System.out.println("Linea ignorada: " + linea);
                }
            }

            System.out.println("Se cargaron " + contadorCargados + " mensajes.");

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}