package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import highschool.Game;

import static highschool.Game.game;

/**
 * 开始新学年失败弹窗
 */
public class DialogTips extends Group {
    public Image img_bg;

    public DialogTips() {
        img_bg = game.getImage(new Color(1, 1, 1, 0.9f));
        img_bg.setSize(Game.WIDTH * 0.5f, 70);
        img_bg.setPosition(getWidth() / 2,120);
        setSize(img_bg.getWidth(), img_bg.getHeight());
        addActor(img_bg);
        Button btn_close = game.getCloseButton();
        btn_close.setPosition(getWidth(), getHeight()+120, Align.center);
        addActor(btn_close);
        btn_close.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                game.removeDialog(DialogTips.this);
            }
        });


        Image img_txt = game.getImageText("校园建筑构造不合理，请重新构建校园！");
        img_txt.setColor(Color.BLACK);
        img_txt.setPosition(getWidth() / 2, 150, Align.center);
        addActor(img_txt);
    }
}
