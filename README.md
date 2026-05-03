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
<img width="931" height="772" alt="uml proyecto drawio" src="https://github.com/user-attachments/assets/c222d644-8719-418a-ae57-3ffe47a8d1a3" />

## Casos de prueba
1.	Testing
a.	Nivel de developer
b.	Nivel de Usuario
Luego de hacer una primera instancia de testeo debil y detectar correcciones necesarias, se procede a realizar los siguientes testeos pero esta vez en modo de usuario sobre el sistema de PacketFlow.
Se simulan los siguientes escenarios:1. Inicializacion de la red 
6 tests — 4 casos borde
 
1.	Ingresar tamaño máximo de paquete = 1 (mínimo válido)
2.	[caso borde] Ingresar tamanio = 0 → debe rechazarlo con error
3.	[caso borde] Ingresar tamaño = -5 → debe rechazarlo con error 
4.	[caso borde] Ingresar letras en lugar de número → debe pedir de nuevo
5.	Ingresar capacidad máxima = 1 (mínimo válido)
6.	[caso borde] Ingresar capacidad = 0 o negativa → debe rechazarlo
2. Crear mensaje manualmente
1.	 
2.	Crear mensaje con contenido corto (menor al tamanio max) → genera 1 paquete
3.	[caso borde] Crear mensaje con contenido exactamente igual al tamanio maximo → genera 1 paquete
4.	[caso borde] Crear mensaje con contenido = tamanio maximo + 1 → genera 2 paquetes
5.	[caso borde] Crear mensaje con ID duplicado → debe rechazarlo con error
6.	[caso borde] Crear mensaje con ID = 0 o negativo → debe rechazarlo
7.	[caso borde] Crear mensaje con contenido vacio (solo espacios) → debe rechazarlo [caso borde]
8.	Crear mensaje con prioridad fuera de rango (0 o 4) → debe rechazarlo
9.	[caso borde] Crear mensajes hasta llenar la red → el siguiente debe rechazarse por capacidad. Verificar con opcion 8 que la cantidad de paquetes generados es correcta
10.	3.  Enviar paquetes (opciones 2 y 3)
11.	Crear mensajes con distinta prioridad → verificar que prioridad 1 se envia primero
12.	[caso borde] Dos mensajes con misma prioridad → paquetes deben agruparse por ID, no mezclarse
13.	[caso borde] Intentar enviar sin mensajes creados → debe mostrar aviso
14.	Usar opcion 3 con ID valido → solo se envian los paquetes de ese mensaje
15.	[caso borde] Usar opcion 3 con ID inexistente → debe mostrar error
16.	[caso borde] Usar opcion 3 en mensaje ya enviado → debe indicar que no hay paquetes en transito
17.	[caso borde] Enviar todos y volver a intentar enviar → debe indicar cola vacia
4.  Reconstruir mensajes (opciones 4 y 5)
1.	Enviar todos los paquetes y reconstruir con opcion 4 → devuelve el de mayor prioridad
2.	[caso borde] Dos mensajes con misma prioridad → opcion 4 debe devolver el mas antiguo
3.	[caso borde] Intentar reconstruir antes de enviar paquetes → debe indicar mensaje incompleto
4.	Enviar paquetes de un solo mensaje y reconstruir → solo ese debe estar disponible
5.	Reconstruir por ID con opcion 5 → contenido reconstruido identico al original
6.	[caso borde] Reconstruir por ID inexistente → debe mostrar error 
7.	[caso borde] Reconstruir un mensaje ya reconstruido → debe indicar que no existe
5. Consultar estado (opcion 6)
Verificar que los mensajes aparecen ordenados por prioridad
1.	[caso borde] Verificar espacio disponible disminuye al crear y aumenta al reconstruir 
2.	[caso borde] Consultar estado con red vacia → debe indicar que no hay mensajes
3.	6.  Carga desde CSV (opcion 7) 
Cargar mensajes.csv de prueba → todos los mensajes validos se cargan 
1.	CSV con linea de encabezado (ID,CONTENIDO,PRIORIDAD) → debe ignorarse
2.	[caso borde] CSV con linea vacia → debe ignorarse
3.	[caso borde] CSV con ID duplicado respecto a uno ya creado → esa linea se ignora
4.	[caso borde] CSV con prioridad fuera de rango → linea ignorada con aviso
5.	[caso borde] CSV con contenido vacio → linea ignorada con aviso 
6.	[caso borde] CSV con ID o prioridad no numerico (ej: 'abc') → linea ignorada
7.	[caso borde] Ruta de archivo inexistente → debe mostrar error de lectura
8.	[caso borde] CSV con mas mensajes de los que caben en la red → los ultimos se rechazan
(flujo del sistema)


