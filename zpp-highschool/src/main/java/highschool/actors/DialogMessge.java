package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import highschool.Game;

import static highschool.Game.game;


/**
 * 消息对话框
 */

public class DialogMessge extends Group{
    public Image img_bg;

    public DialogMessge(String msg) {
        img_bg = game.getImage(Color.WHITE);
        img_bg.setSize(Game.WIDTH * 0.5f, 60);
        setSize(img_bg.getWidth(), img_bg.getHeight());
        addActor(img_bg);
        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.removeDialog(DialogMessge.this);
            }
        });

        Image img_txt = game.getImageText(msg);
        img_txt.setColor(Color.BLACK);
        img_txt.setPosition(getWidth() / 2, getHeight() / 2, Align.center);
        addActor(img_txt);

        img_bg.addAction(Actions.delay(2, Actions.run(new Runnable() {
            public void run() {
                game.removeDialog(DialogMessge.this);
            }
        })));
    }
}
