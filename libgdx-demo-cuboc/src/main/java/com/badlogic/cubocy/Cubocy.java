
package com.badlogic.cubocy;

import com.badlogic.cubocy.screens.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.cubocy.screens.GameScreen;

public class Cubocy extends Game {

    public void create() {
        setScreen(new MainMenu(this));
//        setScreen(new GameScreen(this));
    }
}
