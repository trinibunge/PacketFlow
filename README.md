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

