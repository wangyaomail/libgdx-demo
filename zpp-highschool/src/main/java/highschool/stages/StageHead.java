package highschool.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import highschool.Config;
import highschool.tools.FreePaint;

import static highschool.Game.game;

public class StageHead extends Stage {


    public StageHead() {
        super(new ScalingViewport(Scaling.stretch, game.WIDTH, game.HEIGHT));//设置舞台适配模式

        game.playMusic("sound/bg.mp3");

        Image img_background = game.getImage("images/background.png");//创建一张背景图
        img_background.setSize(game.WIDTH, game.HEIGHT);//设置尺寸为全屏
        addActor(img_background);//添加到舞台

        //标题
        FreePaint paint = new FreePaint(100);
        Image img_title_shadow = game.getImageText("高校模拟器", paint);
        img_title_shadow.setPosition(getWidth() / 2 + 1, 500 - 1, Align.center);
        img_title_shadow.setColor(Color.DARK_GRAY);
        addActor(img_title_shadow);
        Image img_title = game.getImageText("高校模拟器", paint);
        img_title.setPosition(getWidth() / 2, 500, Align.center);
        addActor(img_title);

        //开始按钮
        Button btn_dialog = game.getButton(300, 60, 20);
        btn_dialog.setPosition(getWidth() / 2, getHeight() / 2 - 100, Align.bottom);
        addActor(btn_dialog);
        Image img_play = game.getImageText("开始游戏");
        img_play.setColor(Color.BLACK);
        btn_dialog.add(img_play);
        btn_dialog.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    public void input(String text) {
                        String input = text.replaceAll("\\s+", "");//移除掉用户乱输入的空格等字符
                        if (!input.equals("")) Config.schoolName = input;
                        Gdx.app.postRunnable(new Runnable() {//新建的界面里面涉及到纹理创建，libgdx但凡涉及纹理创建的都需要套上这个libgdx线程，否则会报错
                            public void run() {
                                game.stopMusic();
                                game.playSound("sound/btnclicked.mp3");
                                game.setStage(new StageGame());
                            }
                        });
                    }

                    public void canceled() {
                        game.playSound("sound/btnclicked.mp3");
                    }
                }, "请给你的大学取一个好听的名字", "", "中原工学院");
            }
        });

    }

}
