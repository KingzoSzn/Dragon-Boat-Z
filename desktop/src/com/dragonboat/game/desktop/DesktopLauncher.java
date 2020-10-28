package com.dragonboat.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dragonboat.game.DragonBoatGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1080;
		config.height = 720;
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		new LwjglApplication(new DragonBoatGame(), config);
	}
}
