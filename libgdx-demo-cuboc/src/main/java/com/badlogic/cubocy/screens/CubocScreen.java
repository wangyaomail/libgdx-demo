
package com.badlogic.cubocy.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public abstract class CubocScreen implements Screen {
    Game game;

    public CubocScreen(Game game) {
        this.game = game;
    }

    public void resize(int width, int height) {
    }

    public void show() {
    }

    public void hide() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }
}
