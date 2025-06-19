package com.proyectofinal.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.proyectofinal.RPGGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new RPGGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("ProyectoFinal_ProgramacionII");
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // Ajusta el tamaño de ventana a tu preferencia:
        config.setWindowedMode(1280, 720);
        config.setResizable(false);

        // Iconos
        config.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return config;
    }
}
