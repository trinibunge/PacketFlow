package PacketFlow.App;

import PacketFlow.*;

import java.util.PriorityQueue;
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
 * - Enviar paquetes (todos o de un mensaje específico)
 * - Reconstruir mensajes (siguiente automático, o por ID)
 * - Consultar estado de la red
 *
 * Reglas de validación:
 * - Tamaño máximo de paquete y capacidad de red deben ser > 0
 * - IDs de mensajes deben ser > 0 y únicos
 * - Prioridad debe estar entre 1 y 3 (1 = más prioritario, 3 = menos prioritario)
 * - Contenido no puede estar vacío
 * - La red no puede saturarse
 */
public class Main {
    private static Red red;
    private static Scanner sc;
    private static Reconstructor reconstructor;

    private static final int PRIORIDAD_MIN = 1;
    private static final int PRIORIDAD_MAX = 3;

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        reconstructor = new Reconstructor();

        inicializarRed(sc);

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
                    reconstruirSiguiente();
                    break;
                case 5:
                    reconstruirPorId(sc);
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
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        } while (opcion != 8);

        sc.close();
    }

    /**
     * Inicializa la red solicitando capacidad y tamaño máximo de paquete.
     */
    private static void inicializarRed(Scanner scanner) {
        System.out.println("\n!Bienvenido a PacketFlow¡ Necesitamos que inicialize la red para comenzar la prueba.\n");

        int tamanioMax = leerEnteroPositivo(scanner,
                "Primero, seleccione la capacidad máxima de caracteres que puede contener cada paquete: ",
                "El tamaño debe ser un número entero mayor a 0.");

        int capacidadMax = leerEnteroPositivo(scanner,
                "Ahora, seleccione la capacidad maxima de paquetes de la red: ",
                "La capacidad debe ser un número entero mayor a 0.");

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
        System.out.println("4. Reconstruir el mensaje de mayor prioridad y antiguedad en la red");
        System.out.println("5. Reconstruir mensaje por ID");
        System.out.println("6. Consultar estado de la red");
        System.out.println("7. Cargar mensajes desde CSV (existe el archivo mensajes.csv de prueba)");
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
     * Lee un entero positivo del scanner.
     */
    private static int leerEnteroPositivo(Scanner scanner, String prompt, String mensajeError) {
        int valor;
        while (true) {
            System.out.print(prompt);
            try {
                valor = scanner.nextInt();
                scanner.nextLine();
                if (valor > 0) {
                    return valor;
                }
                System.out.println("Error: " + mensajeError);
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Lee un entero dentro de un rango.
     */
    private static int leerEnteroEnRango(Scanner scanner, String prompt, int min, int max) {
        int valor;
        while (true) {
            System.out.print(prompt);
            try {
                valor = scanner.nextInt();
                scanner.nextLine();
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("Error: El valor debe estar entre " + min + " y " + max + ".");
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Crea un mensaje manualmente solicitando datos al usuario.
     */
    private static void crearMensajeManual(Scanner scanner) {
        System.out.println("\nCrear Mensaje\n");

        int id;
        while (true) {
            id = leerEnteroPositivo(scanner, "ID del mensaje: ",
                    "El ID debe ser un número entero mayor a 0.");
            if (red.buscarMensaje(id) != null) {
                System.out.println("Error: Ya existe un mensaje con ID " + id + ". Use otro ID.");
            } else {
                break;
            }
        }
        int prioridad = leerEnteroEnRango(scanner,
                "Prioridad (recuerda que 1 es lo más prioritario, 3 es lo menos prioritario): ",
                PRIORIDAD_MIN, PRIORIDAD_MAX);

        String contenido;
        while (true) {
            System.out.print("Contenido del mensaje: ");
            contenido = scanner.nextLine();
            if (contenido != null && !contenido.trim().isEmpty()) {
                break;
            }
            System.out.println("Error: El contenido no puede estar vacío.");
        }

        try {
            boolean creado = red.crearMensaje(id, contenido, prioridad);
            if (!creado) {
                int paquetesNecesarios = (int) Math.ceil((double) contenido.length() / red.getTamanioMaxPaquete());
                System.out.println("Error: La red no tiene capacidad suficiente.");
                System.out.println("  Paquetes necesarios: " + paquetesNecesarios);
                System.out.println("  Espacio disponible:  " + red.espacioDisponible());
                return;
            }
            System.out.println("Mensaje creado y fragmentado correctamente.");
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
        if (red.getEnTransito().isEmpty()) {
            System.out.println("No hay paquetes en tránsito para enviar.");
            return;
        }
        red.enviarPaquetes();
    }

    /**
     * Envía paquetes de un mensaje específico.
     */
    private static void enviarPaquetesDeMensaje(Scanner scanner) {
        System.out.println("\nEnviar paquetes de un mensaje\n");

        if (red.getMensajes().isEmpty()) {
            System.out.println("Error: No hay mensajes en la red.");
            return;
        }

        int id = leerEnteroPositivo(scanner, "ID del mensaje: ",
                "El ID debe ser un número entero mayor a 0.");

        Mensaje m = red.buscarMensaje(id);
        if (m == null) {
            System.out.println("Error: Mensaje con ID " + id + " no encontrado.");
            return;
        }

        System.out.println("\nEnviando paquetes del mensaje " + id + "...");
        int enviados = 0;
        for (Paquete p : m.getPaquetes()) {
            if (p.getEstado() == EstadoPaquete.EN_TRANSITO) {
                p.setEstado(EstadoPaquete.RECIBIDO);
                red.getEnTransito().remove(p);
                System.out.printf("Enviado: Paquete %d/%d%n", p.getNumero(), p.getCantPaquetes());
                enviados++;
            }
        }

        if (enviados == 0) {
            System.out.println("No habían paquetes en tránsito de este mensaje.");
        } else {
            System.out.println("Total enviados: " + enviados + " paquetes.\n");
        }
    }

    /**
     * Reconstruye el siguiente mensaje (mayor prioridad + más antiguo) y lo elimina.
     */
    private static void reconstruirSiguiente() {
        System.out.println("\nReconstruir siguiente mensaje\n");

        if (red.getMensajes().isEmpty()) {
            System.out.println("No hay mensajes en la red.");
            return;
        }

        String resultado = reconstructor.reconstruirSiguiente(red);

        if (resultado == null) {
            System.out.println("No hay mensajes completos disponibles para reconstruir.");
        } else {
            System.out.println(resultado);
        }
    }

    /**
     * Reconstruye un mensaje específico por ID y lo elimina de la red.
     */
    private static void reconstruirPorId(Scanner scanner) {
        System.out.println("\nReconstruir mensaje por ID\n");

        if (red.getMensajes().isEmpty()) {
            System.out.println("Error: No hay mensajes en la red.");
            return;
        }

        int id = leerEnteroPositivo(scanner, "ID del mensaje a reconstruir: ",
                "El ID es invalido, ingrese un número entero mayor a 0 la proxima.");

        if (red.buscarMensaje(id) == null) {
            System.out.println("Error: Mensaje con ID " + id + " no encontrado.");
            return;
        }

        String resultado = reconstructor.reconstruir(id, red);

        if (resultado == null) {
            System.out.println("No se puede reconstruir el mensaje " + id + ": está incompleto.");
        } else {
            System.out.println("\nMensaje reconstruido:");
            System.out.println(resultado);
        }
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

        PriorityQueue<Mensaje> ordenados = reconstructor.ordenarPorPrioridad(mensajes);
        while (!ordenados.isEmpty()) {
            Mensaje m = ordenados.poll();
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
     */
    private static void cargarCSV(Scanner scanner) {
        System.out.println("\nCargar archivo\n");
        System.out.print("Ruta del archivo CSV: ");
        String rutaArchivo = scanner.nextLine();

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            System.out.println("Error: Debe ingresar una ruta válida.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int contadorCargados = 0;
            int contadorIgnorados = 0;
            int numeroLinea = 0;

            while ((linea = br.readLine()) != null) {
                numeroLinea++;

                if (linea.trim().isEmpty() || linea.startsWith("ID")) {
                    continue;
                }

                try {
                    String[] partes = linea.split(",", 3);
                    if (partes.length != 3) {
                        System.out.println("Línea " + numeroLinea + " ignorada, tiene formato no valido: " + linea);
                        contadorIgnorados++;
                        continue;
                    }

                    int id = Integer.parseInt(partes[0].trim());
                    String contenido = partes[1].trim();
                    int prioridad = Integer.parseInt(partes[2].trim());

                    if (id <= 0) {
                        System.out.println("Línea " + numeroLinea + " ignorada ID invalido: " + linea);
                        contadorIgnorados++;
                        continue;
                    }
                    if (red.buscarMensaje(id) != null) {
                        System.out.println("Línea " + numeroLinea + " ignorada el ID " + id + " ya existe con otro mensaje en la red: " + linea);
                        contadorIgnorados++;
                        continue;
                    }
                    if (contenido.isEmpty()) {
                        System.out.println("Línea " + numeroLinea + " ignorada motivo: contenido vacio : " + linea);
                        contadorIgnorados++;
                        continue;
                    }
                    if (prioridad < PRIORIDAD_MIN || prioridad > PRIORIDAD_MAX) {
                        System.out.println("Línea " + numeroLinea + " ignorada motivo: la prioridad debe estar entre  "
                                + PRIORIDAD_MIN + " y " + PRIORIDAD_MAX + "): " + linea);
                        contadorIgnorados++;
                        continue;
                    }

                    boolean creado = red.crearMensaje(id, contenido, prioridad);
                    if (!creado) {
                        System.out.println("Línea " + numeroLinea + " ignorada motivo: red sin capacidad suficiente: " + linea);
                        contadorIgnorados++;
                        continue;
                    }
                    contadorCargados++;

                } catch (NumberFormatException e) {
                    System.out.println("Línea " + numeroLinea + " ignorada, motivo: ID o prioridad no son números: " + linea);
                    contadorIgnorados++;
                }
            }

            System.out.println("\nResumen:");
            System.out.println("  Mensajes cargados: " + contadorCargados);
            System.out.println("  Líneas ignoradas: " + contadorIgnorados);

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}