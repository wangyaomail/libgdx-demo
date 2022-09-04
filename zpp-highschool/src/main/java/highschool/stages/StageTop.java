package highschool.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import highschool.Game;

public class StageTop extends Stage {


    public StageTop() {
        super(new ScalingViewport(Scaling.stretch, Game.game.WIDTH, Game.game.HEIGHT));//设置舞台适配模式
    }

}
