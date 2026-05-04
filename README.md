# Proyecto Algoritmos y Estructuras de Datos - PacketFlow

## Equipo 1
[![Maximiliano López](https://img.shields.io/badge/GitHub-Maximiliano_López-B7E3FF?logo=github&logoColor=black)](https://github.com/maaxilopp) [![Juan Baldenegro](https://img.shields.io/badge/GitHub-Juan_Baldenegro-A8E6CF?logo=github&logoColor=black)](https://github.com/juanBalde2002) [![Trinidad Bunge](https://img.shields.io/badge/GitHub-Trinidad_Bunge-FFD966?logo=github&logoColor=black)](https://github.com/trinibun)

## Escenario
En las redes de computadoras modernas, la información no se transmite como una unidad completa, sino que se fragmenta en pequeñas partes llamadas paquetes. Estos paquetes viajan de forma independiente a través de la red, lo que implica que pueden llegar en distinto orden, con demoras variables o incluso no llegar a destino.
Este comportamiento introduce un desafío fundamental: reconstruir correctamente la información original a partir de fragmentos potencialmente
desordenados e incompletos. Para ello, los sistemas reales utilizan buffers de recepción, colas de transmisión y mecanismos de control que permiten ordenar, validar
y recomponer los datos.
La UCU ha convocado a la empresa estrella de incubación tecnológica, AED(Arquitectos de Entregas Dudosas), para desarrollar PacketFlow, un simulador de transmisión de datos en red. El sistema deberá modelar el envío de mensajes fragmentados en paquetes, su circulación a través de la red y su posterior reconstrucción en el destino.

## Objetivo del proyecto
Desarrollar un simulador por consola en Java que permita modelar la transmisión de mensajes a través de una red, incluyendo su fragmentación en paquetes, el envío, la
recepción potencialmente desordenada y la reconstrucción del mensaje original. Para la implementación del sistema se deberán utilizar exclusivamente estructuras
lineales (listas, colas y pilas). El objetivo principal es asegurar que, a partir de los paquetes recibidos, el sistema pueda determinar si un mensaje puede reconstruirse
correctamente y, en caso afirmativo, reconstruirlo respetando el orden original.

## Diagrama UML
<img width="2000" height="1400" alt="PacketFlow_UML_Diagram" src="https://github.com/user-attachments/assets/5c6b2667-b3e4-4914-94a7-6682a992508c" />




##  Casos de Prueba

### 1. Enfoque de Testing
Se realizaron pruebas en dos niveles:
- **Nivel developer**: tests iniciales para detectar errores básicos.
- **Nivel usuario**: simulación del uso real del sistema *PacketFlow*.

Luego de una primera etapa de testeo inicial, se ejecutaron los siguientes escenarios desde la perspectiva del usuario.

---

###  2. Inicialización de la red  
**6 tests (4 casos borde)**

- Ingresar tamaño máximo de paquete = 1 (mínimo válido)  
- [Caso borde] Tamaño = 0 → debe rechazar con error  
- [Caso borde] Tamaño = -5 → debe rechazar con error  
- [Caso borde] Ingresar letras → debe solicitar nuevamente el valor  
- Ingresar capacidad máxima = 1 (mínimo válido)  
- [Caso borde] Capacidad = 0 o negativa → debe rechazar  

---

### 3. Crear mensaje manualmente

- Crear mensaje con contenido corto → genera 1 paquete  
- [Caso borde] Contenido = tamaño máximo → genera 1 paquete  
- [Caso borde] Contenido = tamaño máximo + 1 → genera 2 paquetes  
- [Caso borde] ID duplicado → debe rechazar  
- [Caso borde] ID = 0 o negativo → debe rechazar  
- [Caso borde] Contenido vacío (solo espacios) → debe rechazar  
- Prioridad fuera de rango (0 o 4) → debe rechazar  
- [Caso borde] Llenar la red → siguiente mensaje debe rechazarse  

---

### 4. Envío de paquetes (Opciones 2 y 3)

- Crear mensajes con distinta prioridad → prioridad 1 se envía primero  
- [Caso borde] Misma prioridad → paquetes se agrupan por ID  
- [Caso borde] Sin mensajes → debe mostrar aviso  
- Enviar por ID válido → solo se envían esos paquetes  
- [Caso borde] ID inexistente → error  
- [Caso borde] Mensaje ya enviado → indicar que no hay paquetes  
- [Caso borde] Cola vacía → indicar correctamente  

---

### 5. Reconstrucción de mensajes (Opciones 4 y 5)

- Enviar todos y reconstruir → devuelve el de mayor prioridad  
- [Caso borde] Misma prioridad → devuelve el más antiguo  
- [Caso borde] Reconstruir sin enviar → mensaje incompleto  
- Enviar un solo mensaje → solo ese se reconstruye  
- Reconstrucción por ID → contenido idéntico al original  
- [Caso borde] ID inexistente → error  
- [Caso borde] Mensaje ya reconstruido → indicar que no existe  

---

### 6. Consulta de estado (Opción 6)

- Verificar que los mensajes aparecen ordenados por prioridad  
- [Caso borde] Espacio disponible:  
  - Disminuye al crear mensajes  
  - Aumenta al reconstruir  
- [Caso borde] Red vacía → debe indicar que no hay mensajes  

---

### 7. Carga desde CSV (Opción 7)

- Cargar archivo válido → todos los mensajes correctos se agregan  

**Casos borde:**
- Encabezado → debe ignorarse  
- Línea vacía → ignorar  
- ID duplicado → ignorar  
- Prioridad inválida → ignorar con aviso  
- Contenido vacío → ignorar con aviso  
- Datos no numéricos → ignorar  
- Ruta inexistente → error de lectura  
- Exceso de mensajes → los últimos se rechazan  

---

> Estos casos de prueba validan tanto el funcionamiento esperado del sistema como su comportamiento frente a situaciones límite.

