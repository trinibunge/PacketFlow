package PacketFlow.App;

import PacketFlow.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase Main
 *
 * Proporciona la interfaz de usuario para el simulador de red "Packetflow".
 * Implementa un menú interactivo que permite:
 * - Crear mensajes manualmente o cargar desde CSV
 * - Enviar paquetes (todos, de un mensaje específico, específicos por número, o de mayor prioridad)
 * - Reconstruir mensajes (siguiente automático, o por ID)
 * - Consultar estado de la red
 *
 * Reglas de validación:
 * - Tamaño máximo de paquete y capacidad de red deben ser un integer mayor que 0
 * - IDs de mensajes deben ser mayores que 0 y únicos
 * - Prioridad debe estar entre 1 y 3 (1 es más prioritario, 3 es menos prioritario)
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

        inicializarRed();

        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1: crearMensajeManual(); break;
                case 2: enviarPaquetes(); break;
                case 3: enviarPaquetesDeMensaje(); break;
                case 4: enviarPaquetesEspecificos(); break;
                case 5: enviarPaquetesMayorPrioridad(); break;
                case 6: reconstruirSiguiente(); break;
                case 7: reconstruirPorId(); break;
                case 8: listarEstado(); break;
                case 9: cargarCSV(); break;
                case 10:
                    System.out.println("\nGracias por elegir PacketFlow, ¡Nos vemos pronto!\n");
                    break;
                default:
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        } while (opcion != 10);

        sc.close();
    }

    /**
     * Inicializa la red solicitando capacidad y tamaño máximo de paquete.
     */
    private static void inicializarRed() {
        System.out.println("\n¡Bienvenido a PacketFlow! Necesitamos que inicialize la red para comenzar la prueba.\n");

        int tamanioMax = leerEnteroPositivo(
                "Primero, seleccione la capacidad máxima de caracteres que puede contener cada paquete: ",
                "El tamaño debe ser un número entero mayor a 0.");

        int capacidadMax = leerEnteroPositivo(
                "Ahora, seleccione la capacidad maxima de paquetes de la red: ",
                "La capacidad debe ser un número entero mayor a 0.");

        red = new Red(tamanioMax, capacidadMax);
        System.out.println("\n¡Felicitaciones! La red fue implementada correctamente.\n");
    }

    /**
     * Muestra el menú principal.
     */
    private static void mostrarMenu() {
        System.out.println("\n MENÚ PRINCIPAL DE LA RED \n");
        System.out.println("1. Crear mensaje manualmente");
        System.out.println("2. Enviar todos los paquetes");
        System.out.println("3. Enviar paquetes de un mensaje específico");
        System.out.println("4. Enviar paquetes específicos de un mensaje");
        System.out.println("5. Enviar paquetes de mayor prioridad");
        System.out.println("6. Reconstruir el mensaje de mayor prioridad y antiguedad en la red");
        System.out.println("7. Reconstruir mensaje por ID");
        System.out.println("8. Consultar estado de la red");
        System.out.println("9. Cargar mensajes desde CSV (hay un archivo llamado mensajes.csv de ejemplo)");
        System.out.println("10. Salir");
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
    private static int leerEnteroPositivo(String prompt, String mensajeError) {
        int valor;
        while (true) {
            System.out.print(prompt);
            try {
                valor = sc.nextInt();
                sc.nextLine();
                if (valor > 0) {
                    return valor;
                }
                System.out.println("Error: " + mensajeError);
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido.");
                sc.nextLine();
            }
        }
    }

    /**
     * Lee un entero dentro de un rango.
     */
    private static int leerEnteroEnRango(String prompt, int min, int max) {
        int valor;
        while (true) {
            System.out.print(prompt);
            try {
                valor = sc.nextInt();
                sc.nextLine();
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("Error: El valor debe estar entre " + min + " y " + max + ".");
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido.");
                sc.nextLine();
            }
        }
    }

    /**
     * Envía una colección de paquetes, marca como RECIBIDO los que estén EN_TRANSITO,
     * imprime un reporte por cada uno, y hace una única limpieza de enTransito al final.
     *
     *
     * @param paquetes colección de paquetes candidatos a enviar
     * @return cantidad de paquetes efectivamente enviados
     */
    private static int enviarColeccion(Iterable<Paquete> paquetes) {
        int enviados = 0;
        for (Paquete p : paquetes) {
            if (p.getEstado() == EstadoPaquete.EN_TRANSITO) {
                p.setEstado(EstadoPaquete.RECIBIDO);
                System.out.printf("Enviado: Mensaje %d | Paquete %d/%d%n",
                        p.getIdMensaje(), p.getNumero(), p.getCantPaquetes());
                enviados++;
            }
        }
        if (enviados > 0) {
            red.getEnTransito().removeIf(p -> p.getEstado() != EstadoPaquete.EN_TRANSITO);
        }
        return enviados;
    }

    /**
     * Crea un mensaje manualmente solicitando datos al usuario.
     */
    private static void crearMensajeManual() {
        System.out.println("\nCrear Mensaje\n");

        int id;
        while (true) {
            id = leerEnteroPositivo("ID del mensaje: ",
                    "El ID debe ser un número entero mayor a 0.");
            if (red.buscarMensaje(id) != null) {
                System.out.println("Error: Ya existe un mensaje con ID " + id + ". Use otro ID.");
            } else {
                break;
            }
        }
        int prioridad = leerEnteroEnRango(
                "Prioridad (recuerda que 1 es lo más prioritario, 3 es lo menos prioritario): ",
                PRIORIDAD_MIN, PRIORIDAD_MAX);

        String contenido;
        while (true) {
            System.out.print("Contenido del mensaje: ");
            contenido = sc.nextLine();
            if (!contenido.trim().isEmpty()) {
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
            System.out.println("  Paquetes generados: " + red.buscarMensaje(id).getPaquetes().size());
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
    private static void enviarPaquetesDeMensaje() {
        System.out.println("\nEnviar paquetes de un mensaje\n");

        if (red.getMensajes().isEmpty()) {
            System.out.println("Error: No hay mensajes en la red.");
            return;
        }

        int id = leerEnteroPositivo("ID del mensaje: ",
                "El ID debe ser un número entero mayor a 0.");

        Mensaje m = red.buscarMensaje(id);
        if (m == null) {
            System.out.println("Error: Mensaje con ID " + id + " no encontrado.");
            return;
        }

        System.out.println("\nEnviando paquetes del mensaje " + id + "...");
        int enviados = enviarColeccion(m.getPaquetes());

        if (enviados == 0) {
            System.out.println("No habían paquetes en tránsito de este mensaje.");
        } else {
            System.out.println("Total enviados: " + enviados + " paquetes.\n");
        }
    }

    /**
     * Envía paquetes específicos por numero de paquete de un mensaje dado.
     */
    private static void enviarPaquetesEspecificos() {
        System.out.println("\nEnviar paquetes específicos de un mensaje\n");

        if (red.getMensajes().isEmpty()) {
            System.out.println("Error: No hay mensajes en la red.");
            return;
        }

        int id = leerEnteroPositivo("ID del mensaje: ",
                "El ID debe ser un número entero mayor a 0.");

        Mensaje m = red.buscarMensaje(id);
        if (m == null) {
            System.out.println("Error, no hay mensajes con el id: " + id);
            return;
        }

        System.out.println("\nPaquetes del mensaje " + id + ":");
        for (Paquete p : m.getPaquetes()) {
            System.out.printf("  Paquete %d/%d | Estado: %s | Contenido: \"%s\"%n",
                    p.getNumero(), p.getCantPaquetes(), p.getEstado(), p.getContenido());
        }

        int totalPaquetes = m.getPaquetes().size();

        System.out.print("\nIngrese los números de paquete a enviar (separados por comas): ");
        String entrada = sc.nextLine();

        if (entrada.trim().isEmpty()) {
            System.out.println("No se seleccionó ningún paquete.");
            return;
        }

        // Recolectar paquetes válidos (validando input y avisando errores)
        List<Paquete> aEnviar = new ArrayList<>();
        String[] numeros = entrada.split(",");

        for (String numStr : numeros) {
            String trimmed = numStr.trim();
            if (trimmed.isEmpty()) continue;

            int num;
            try {
                num = Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                System.out.println("Error: '" + trimmed + "' no es un número válido.");
                continue;
            }

            if (num < 1 || num > totalPaquetes) {
                System.out.println("Error, no existe ese paquete en el mensaje seleccionado (número: " + num + ")");
                continue;
            }

            Paquete paquete = m.getPaquetes().get(num - 1);
            if (paquete.getEstado() == EstadoPaquete.EN_TRANSITO) {
                aEnviar.add(paquete);
            } else {
                System.out.printf("Paquete %d ya fue enviado (estado: %s).%n",
                        num, paquete.getEstado());
            }
        }

        int enviados = enviarColeccion(aEnviar);
        System.out.println("\nTotal enviados: " + enviados + " paquetes.");
    }

    /**
     * Envía todos los paquetes en tránsito que pertenezcan al mensaje con
     * la prioridad más alta actualmente presente en la red.
     */
    private static void enviarPaquetesMayorPrioridad() {
        System.out.println("\nEnviar paquetes de mayor prioridad\n");

        if (red.getEnTransito().isEmpty()) {
            System.out.println("No hay paquetes en tránsito.");
            return;
        }

        int prioridadMax = Integer.MAX_VALUE;
        for (Paquete p : red.getEnTransito()) {
            if (p.getMensaje().getPrioridad() < prioridadMax) {
                prioridadMax = p.getMensaje().getPrioridad();
            }
        }

        System.out.println("Enviando paquetes con prioridad " + prioridadMax + "...\n");

        // Filtrar los que coinciden con la prioridad más alta
        List<Paquete> aEnviar = new ArrayList<>();
        for (Paquete p : red.getEnTransito()) {
            if (p.getMensaje().getPrioridad() == prioridadMax) {
                aEnviar.add(p);
            }
        }

        int enviados = enviarColeccion(aEnviar);
        System.out.println("\nTotal enviados: " + enviados + " paquetes.");
    }

    /**
     * Reconstruye el mensaje de mayor prioridad y antiguedad de la red y lo elimina.
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
    private static void reconstruirPorId() {
        System.out.println("\nReconstruir mensaje por ID\n");

        if (red.getMensajes().isEmpty()) {
            System.out.println("Error: No hay mensajes en la red.");
            return;
        }

        int id = leerEnteroPositivo("ID del mensaje a reconstruir: ",
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
     * Formato esperado del archivo: id, contenido, prioridad
     */
    private static void cargarCSV() {
        System.out.println("\nCargar archivo\n");
        System.out.print("Ruta del archivo CSV: ");
        String rutaArchivo = sc.nextLine();

        if (rutaArchivo.trim().isEmpty()) {
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