# Sistema de Colisiones y Ataques

## Introducción

Este documento explica cómo funcionan los sistemas de colisiones y ataques en el juego, y cómo han sido mejorados para asegurar que los personajes puedan atacar correctamente a los enemigos.

## Estructura del Sistema

### 1. Detección de Colisiones

La detección de colisiones se realiza principalmente utilizando la clase `Rectangle` de LibGDX. Se han implementado mejoras para hacer la detección más robusta:

- **Hitboxes ampliados**: Se utiliza un rectángulo ligeramente más grande para los enemigos en las comprobaciones de colisiones.
- **Tolerancia adicional**: En algunos casos se añade una tolerancia para facilitar la detección.
- **Utilidades de depuración**: La clase `DebugUtils` permite visualizar las hitboxes durante el desarrollo.

### 2. Tipos de Ataques

El juego implementa varios tipos de ataques:

- **Ataques cuerpo a cuerpo** (Caballero): Detecta enemigos en un área frontal.
- **Ataques a distancia** (Arquero): Lanza flechas que colisionan con enemigos.
- **Ataques mágicos** (Mago): Lanza hechizos que pueden atravesar o no a múltiples enemigos.

## Implementación de los Ataques

### Ataque del Caballero

```java
public void atacar(List<? extends Enemigo> enemigos) {
    // Verificar si puede atacar
    if (atacando || !puedeAtacar()) return;

    // Iniciar animación
    atacando = true;
    tiempoAtaque = 0f;
    registrarAtaque();

    // Aplicar daño a enemigos en rango
    for (Enemigo e : enemigos) {
        if (e.estaVivo() && estaEnRango(e)) {
            e.recibirDanio(9999); // Daño letal
        }
    }
}
```

### Proyectiles (Flechas y Hechizos)

Los proyectiles comprueban colisiones en cada frame:

```java
public void comprobarColisiones(List<? extends Enemigo> enemigos) {
    if (enemigos == null || impactando || finalizado) return;

    for (Enemigo e : enemigos) {
        if (!e.estaVivo()) continue;

        if (hitbox.overlaps(e.getHitbox())) {
            e.recibirDanio(9999); // Daño letal
            // Configurar impacto...
            break;
        }
    }
}
```

## Sistema de Daño y Muerte de Enemigos

Cuando un enemigo recibe daño:

1. Se actualiza su vida: `vida -= cantidad`
2. Cambia al estado `HIT` temporalmente
3. Si la vida llega a 0 o menos:
   - Cambia al estado `DYING`
   - Reproduce la animación de muerte
   - Cuando la animación termina, se marca para eliminarse
   - Después de un breve retraso, el `GestorEnemigos` lo elimina

## Integración en el Ciclo de Juego

Para que el sistema funcione correctamente, es necesario:

1. Llamar a `comprobarColisionesProyectiles()` en cada frame.
2. Usar el `GestorEnemigos` para gestionar los enemigos.
3. Asegurar que las actualizaciones se hagan en el orden correcto.

## Ejemplo de Uso en Pantalla de Juego

```java
@Override
public void render(float delta) {
    // Actualizar jugador
    jugador.actualizar(delta);

    // Actualizar enemigos
    gestorEnemigos.actualizarEnemigos(delta, jugador.getX(), jugador.getY());

    // Comprobar colisiones de proyectiles
    playerActor.comprobarColisionesProyectiles(gestorEnemigos.getEnemigosComoLista());

    // Renderizar
    batch.begin();
    gestorEnemigos.renderizarEnemigos(batch);
    playerActor.draw(batch, 1f);
    batch.end();
}
```

## Depuración

Si los ataques siguen sin funcionar correctamente:

1. Activa la visualización de hitboxes: `DebugUtils.setDebugEnabled(true)`
2. Revisa los mensajes de depuración en la consola
3. Verifica que los enemigos estén realmente en el rango de ataque
4. Comprueba que el método `comprobarColisionesProyectiles()` se esté llamando

## Notas Adicionales

- Los proyectiles se eliminan automáticamente cuando salen de los límites de la pantalla
- El ataque del caballero tiene un cooldown para evitar ataques continuos
- Los hechizos pueden configurarse para atravesar enemigos o no
