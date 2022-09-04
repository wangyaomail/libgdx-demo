package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import highschool.Config;
import highschool.Game;
import highschool.stages.StageHead;
import highschool.tools.FreePaint;

import static highschool.Game.game;
import static highschool.stages.StageGame.students;


/**
 * 招生对话框
 */

public class DialogGameOver extends Group {
    public Image img_bg;
    private Button btnStart;
    private Label labScore;
    private FreePaint paint = new FreePaint(20);

    public DialogGameOver() {
        img_bg = game.getImage(new Color(1, 1, 1, 0.9f));
        img_bg.setSize(Game.WIDTH * 0.5f, 300);
        setSize(img_bg.getWidth(), img_bg.getHeight());
        addActor(img_bg);
        Button btn_close = game.getCloseButton();
        btn_close.setPosition(getWidth(), getHeight(), Align.center);
        addActor(btn_close);
        btn_close.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                game.removeDialog(DialogGameOver.this);
                game.setStage(new StageHead());
            }
        });


        Image img_txt = game.getImageText("连续3年教学质量和校园建设不合格");
        img_txt.setColor(Color.BLACK);
        img_txt.setPosition(getWidth() / 2, 250, Align.center);
        addActor(img_txt);

        Image imgNowStudentNumber = game.getImageText("学校宣布破产");
        imgNowStudentNumber.setColor(Color.BLACK);
        imgNowStudentNumber.setPosition(getWidth() / 2, img_txt.getY() - 10, Align.top);
        addActor(imgNowStudentNumber);

        Image imgScore = game.getImageText("游戏得分:", paint);
        imgScore.setColor(Color.BLACK);
        imgScore.setPosition(getWidth() / 2-30, imgNowStudentNumber.getY() - 30, Align.top);
        addActor(imgScore);
        labScore = game.getLabel("" + Config.totalNumberQualifiedGraduates);
        labScore.setColor(Color.BLACK);
        labScore.setFontScale(1.4f);
        labScore.setPosition(imgScore.getRight() + 5, imgScore.getY(Align.center), Align.left);
        addActor(labScore);

        //游戏结束
        btnStart = game.getButton(150, 40, 10);
        btnStart.setColor(Color.valueOf("5ba730ff"));
        btnStart.setPosition(getWidth() / 2, 60, Align.center);
        Image imgStart = game.getImageText("游戏结束");
        imgStart.setSize(imgStart.getWidth() * 0.8f, imgStart.getHeight() * 0.8f);
        imgStart.setPosition(btnStart.getWidth() / 2, btnStart.getHeight() / 2, Align.center);
        btnStart.addActor(imgStart);
        addActor(btnStart);
        btnStart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.removeDialog(DialogGameOver.this);
                game.setStage(new StageHead());
            }
        });

        //数据重置
        students.clear();
        Config.reStart();
    }
}
