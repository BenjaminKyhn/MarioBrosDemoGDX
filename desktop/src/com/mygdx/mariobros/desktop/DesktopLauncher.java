package com.mygdx.mariobros.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.mariobros.MarioBrosGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		new LwjglApplication(new MarioBrosGame(), config);

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Title";
		cfg.useGL30 = true;
		cfg.width = 800;
		cfg.height = 416;
		cfg.initialBackgroundColor.add(Color.BLACK);
		new LwjglApplication(new MarioBrosGame(), cfg);
	}
}
