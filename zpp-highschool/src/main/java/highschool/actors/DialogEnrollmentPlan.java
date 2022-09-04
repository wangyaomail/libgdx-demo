package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import highschool.Config;
import highschool.Game;
import highschool.stages.StageGame;
import highschool.tools.FreePaint;

import static highschool.Game.game;
import static highschool.stages.StageGame.students;


/**
 * 招生对话框
 */

public class DialogEnrollmentPlan extends Group {
    public Image img_bg;
    private Button btnStart;
    private Label labNumber, labNowStudentNumber;
    private int student = 400;
    private FreePaint freePaint = new FreePaint(20);

    public DialogEnrollmentPlan() {
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
                game.removeDialog(DialogEnrollmentPlan.this);
            }
        });


        Image img_txt = game.getImageText("拖动滑块调整新学年计划招生数量");
        img_txt.setColor(Color.BLACK);
        img_txt.setPosition(getWidth() / 2, 250, Align.center);
        addActor(img_txt);

        Image imgNowStudentNumber = game.getImageText("当前在校生人数:", freePaint);
        imgNowStudentNumber.setColor(Color.BLACK);
        imgNowStudentNumber.setPosition(80, img_txt.getY() - 10, Align.topLeft);
        addActor(imgNowStudentNumber);
        labNowStudentNumber = game.getLabel("" + students.size);
        labNowStudentNumber.setFontScale(1.2f);
        labNowStudentNumber.setColor(Color.BLACK);
        labNowStudentNumber.setPosition(imgNowStudentNumber.getRight() + 5, imgNowStudentNumber.getY(Align.center), Align.left);
        addActor(labNowStudentNumber);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(game.getRectColorDrawable(1, 20, Color.GREEN)
                , game.getRectColorDrawable(10, 20, Color.BLACK));
        final Slider slider = new Slider(0, 100, 1, false, sliderStyle);
        slider.setWidth(400);
        slider.setPosition(getWidth() / 2, getHeight() / 2, Align.center);
        addActor(slider);
        slider.setValue(70);

        int  minnum = 0;
        if(Config.intLevel <= 2){
            minnum = (int) (Config.intLevel * student * 0.5f);
        }else{
            minnum = 800;
        }
        final int min = minnum;
        Label labMin = game.getLabel("" + min);
        labMin.setColor(Color.BLACK);
        labMin.setPosition(slider.getX() - 5, slider.getY(Align.center), Align.right);
        labMin.setAlignment(Align.right);
        addActor(labMin);
        int maxnum = 0;
        if(Config.intLevel <= 2){
            maxnum = Config.intLevel * student * 2;
        }else{
            maxnum = 1600;
        }
        final int max = maxnum;

        Label labMax = game.getLabel("" + max);
        labMax.setColor(Color.BLACK);
        labMax.setPosition(slider.getRight() + 3, slider.getY(Align.center), Align.left);
        labMax.setAlignment(Align.left);
        addActor(labMax);

        labNumber = game.getLabel("" + (int) (min + (max - min) * 0.7f));
        labNumber.setColor(Color.ORANGE);
        labNumber.setPosition(getWidth() / 2, slider.getTop() + 10, Align.center);
        labNumber.setAlignment(Align.center);
        addActor(labNumber);

        slider.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                labNumber.setText("" + (min + (int) (slider.getValue() / slider.getMaxValue() * (max - min))));
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        //开始学年
        btnStart = game.getButton(150, 40, 10);
        btnStart.setColor(Color.valueOf("5ba730ff"));
        btnStart.setPosition(getWidth() / 2, 60, Align.center);
        Image imgStart = game.getImageText("开始新学年");
        imgStart.setSize(imgStart.getWidth() * 0.8f, imgStart.getHeight() * 0.8f);
        imgStart.setPosition(btnStart.getWidth() / 2, btnStart.getHeight() / 2, Align.center);
        btnStart.addActor(imgStart);
        addActor(btnStart);
        btnStart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                game.removeDialog(DialogEnrollmentPlan.this);
                Config.intStudent = (min + (int) (slider.getValue() / slider.getMaxValue() * (max - min)));
                StageGame stageGame = (StageGame) game.stage;
                Config.isNewSemestering = true;
                stageGame.startNewSchoolYear();//开始新学年
            }
        });
    }
}
