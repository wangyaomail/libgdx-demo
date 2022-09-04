package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import highschool.Game;


/**
 * 删除进度条
 */

public class DeletSlider extends Actor {

    private TextureRegion bg, top;
    private int step = 0;
    private Runnable endRun;

    public DeletSlider() {
        setTouchable(Touchable.disabled);
        bg = Game.game.getTextureRegion("images/game_delet_bg.png");
        top = Game.game.getTextureRegion("images/game_delet_top.png");
        setSize(bg.getRegionWidth(), bg.getRegionHeight());
    }


    public void play(Runnable endRun) {
        step = 0;
        this.endRun = endRun;
    }

    public void draw(Batch batch, float a) {
        //libGDx默认是每秒60帧
        batch.setColor(Color.WHITE);
        batch.draw(bg, getX(), getY());
        step++;
        float rate = step / 50f;
        batch.draw(top, getX(), getY(), getWidth() * rate, getHeight());
        //在此改变进度条速度
        if (step >= 50) {
            remove();
            endRun.run();
        }
    }
}
