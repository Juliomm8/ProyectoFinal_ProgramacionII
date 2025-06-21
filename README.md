# Proyecto RPG Top-Down (Mazmorras Procedurales)

**Universidad de las Américas**  
Facultad de Ingeniería y Ciencias Aplicadas  
Ingeniería de Software – Programación II

---

## Equipo de trabajo

- **Alisson Armas**  
- **Samuel Cobo**  
- **Julio Mera**  
- **Amelia Povea**  
- **Jeremy Tomaselly**

---

## Descripción del proyecto

Este es un trabajo de la asignatura **Programación II** de la Universidad de las Américas. Nuestro objetivo es crear un **juego RPG top-down estilo dungeon**, combinando exploración de mazmorras generadas de forma procedural, combate en tiempo real y progresión de personaje.

Los jugadores deberán:

- **Explorar** mazmorras únicas en cada partida, llenas de enemigos, trampas y objetos.  
- **Combatir** usando ataques cuerpo a cuerpo o a distancia, gestionando salud y energía.  
- **Subir de nivel**, mejorando atributos y desbloqueando habilidades.  
- **Recolectar y usar pociones** de salud, experiencia y maná.  
- **Guardar el progreso** en puntos específicos.  
- **Interaccionar** con cofres, puertas y NPCs.  
- **Completar misiones**, obteniendo recompensas y logros.

---

## Características principales

1. **Mazmorras procedurales**  
   Cada nivel se genera dinámicamente a partir de una semilla, ofreciendo retos y recompensas distintos.

2. **Sistema de combate**  
   Clases de personajes (Arquero, Mago, Caballero) con ataques y habilidades únicas.  

3. **Progresión de personaje**  
   Puntos de experiencia, subida de nivel y asignación de atributos.  

4. **Inventario de pociones**  
   Consumibles para restaurar vida, maná o ganar experiencia instantánea.  

5. **Menú principal e interfaz**  
   Pantallas de menú, selección de personaje, opciones y mazmorra, construidas con libGDX Scene2D.  

6. **Diseño modular con POO**  
   Uso de clases abstractas e interfaces para mantener el código limpio y extensible.

---

## Estructura del repositorio
├── assets/ → recursos (skins, texturas, mapas)
├── core/ → lógica del juego (pantallas, clases, tests)
└── lwjgl3/ → launcher de escritorio con LWJGL3
