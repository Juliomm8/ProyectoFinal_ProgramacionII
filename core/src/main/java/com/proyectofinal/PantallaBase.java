package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Clase base abstracta para todas las pantallas del juego.
 * Define una estructura comun que maneja Stage, Skin y logica de UI.
 */
public abstract class PantallaBase extends ScreenAdapter {
    protected Stage stage;  // Contenedor principal de elementos visuales
    protected Skin  skin;   // Apariencia visual para los componentes UI

    public PantallaBase() { }

    /**
     * Se ejecuta cuando la pantalla se muestra. Inicializa Stage, Skin e input.
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport()); // Viewport que adapta a la resolucion
        Gdx.input.setInputProcessor(stage);      // Asigna el input a esta pantalla
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json")); // Carga el skin visual
        initUI(); // Llama a la implementacion concreta de la UI
    }

    /**
     * Crea una tabla centrada que ocupa toda la pantalla y la agrega al stage.
     * Util para alinear botones u otros elementos.
     */
    protected Table crearTabla() {
        Table table = new Table(skin);
        table.setFillParent(true); // Hace que la tabla use todo el stage
        table.center();            // Centra el contenido
        stage.addActor(table);    // Agrega al stage para renderizado
        return table;
    }

    /**
     * Metodo abstracto para inicializar los elementos UI especificos de cada pantalla.
     */
    protected abstract void initUI();

    /**
     * Limpia la pantalla, actualiza el estado del stage y lo dibuja.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f,0f,0f,1f); // Color de fondo negro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Limpia pantalla
        stage.act(delta); // Actualiza actores
        stage.draw();     // Dibuja la escena
    }

    /**
     * Actualiza el viewport al redimensionar la ventana.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Libera recursos del stage y skin.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
