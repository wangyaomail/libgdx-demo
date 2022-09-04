package highschool.actors;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import highschool.ai.StudentState;
import highschool.tools.Type;
import highschool.ai.StudentState;

import static highschool.Config.boxSize;
import static highschool.Game.game;
import static highschool.stages.StageGame.astar;

/**
 * 学生类
 */
public class Student extends GameObject {
    private TextureRegion[] textureRegions = new TextureRegion[5];
    public int direction = Align.bottom;
    //血条
    public Image imgEnergyBg, imgHungerBg;
    public Image imgEnergyTop, imgHungerTop;
    //学分
    private Label labScore;
    //体力，体力最大值，饥饿，饥饿最大值，学分，年级
    public int intEnergy, intEnergyMax, intHunger, intHungerMax, intScore, intGrade;

    //ai部分
    public StateMachine<Student, StudentState> stateMachine;//AI
    public Vector2 moveTarget = new Vector2();//移动临时目标点
    public float moveTime;//移动到目的地的预测时间
    public GameObject target;//寻路的终点目标
    public Array<Vector2> paths = new Array<>();//移动路径
    public float speed = 50f;//移动速度
    public boolean isAi;
    public Vector2 seat;//该学生坐的座位

    public boolean isStop;//当学生被点中的时候暂停一切行动

    public Student(Type type) {
        this(type, true);
    }

    public Student(Type type, boolean isAi) {
        super(type);
        this.isAi = isAi;
        boolean isBoy = MathUtils.randomBoolean();
        for (int i = 0; i < 5; i++) {
            textureRegions[i] = game.getTextureRegion("map/student" + (isBoy ? "boy" : "girl") + i + ".png");
        }
        setSize(20, 38);
        //setDebug(true);

        imgEnergyBg = game.getImage(Color.BLACK);
        imgEnergyBg.setSize(20, 1);

        imgEnergyTop = game.getImage(Color.GREEN);
        imgEnergyTop.setSize(20, 1);

        imgHungerBg = game.getImage(Color.BLACK);
        imgHungerBg.setSize(20, 1);

        imgHungerTop = game.getImage(Color.ORANGE);
        imgHungerTop.setSize(20, 1);

        labScore = game.getLabel("0");
        labScore.setFontScale(0.4f);
        labScore.setSize(labScore.getPrefWidth(), labScore.getPrefHeight());
        labScore.setAlignment(Align.center);

        intHunger = intHungerMax = intEnergy = intEnergyMax = 10000;//赋值


        if (isAi) stateMachine = new DefaultStateMachine(this, StudentState.START);

    }

    public void reLife() {
        intEnergy = intEnergyMax;
        intHunger = intHungerMax;
    }

    public void setAngle(float angle) {
        if (isAlmost(angle, MathUtils.PI)) {
            direction = Align.bottom;
        } else if (isAlmost(angle, 0)) {
            direction = Align.top;
        } else if (isAlmost(angle, -MathUtils.PI / 2)) {
            direction = Align.left;
        } else if (isAlmost(angle, MathUtils.PI / 2)) {
            direction = Align.right;
        }
    }

    private boolean isAlmost(float a, float b) {
        return Math.abs(a - b) <= 0.001;
    }


    //给定目标的网格坐标,并生成路径
    public Array<Image> tests = new Array<>();//用于测试路径标记

    //给定目标的网格坐标,并生成路径
    public void createMovePaths(int endX, int endY) {
        for (int i = 0; i < tests.size; i++) {
            tests.get(i).remove();
        }
        tests.clear();

        int startX = (int) (getX(Align.center) / boxSize);
        int startY = (int) (getY() / boxSize);
        IntArray path = astar.getPath(startX, startY, endX, endY);
        paths.clear();
        for (int i = 0, n = path.size; i < n; i += 2) {
            float x = (path.get(i) + 0.5f) * boxSize;
            float y = (path.get(i + 1) + 0.5f) * boxSize;
            paths.add(new Vector2(x, y));

//            Image img_test = game.getImage(Color.RED);
//            img_test.setSize(5, 5);
//            img_test.setPosition(x, y, Align.center);
//            getParent().addActor(img_test);
//            tests.add(img_test);
        }
        if (paths.size > 0) {
            float x = (endX + 0.5f) * boxSize;
            float y = (endY + 0.5f) * boxSize;
            paths.insert(0, new Vector2(x, y));

//            Image img_test = game.getImage(Color.RED);
//            img_test.setSize(5, 5);
//            img_test.setPosition(x, y, Align.center);
//            getParent().addActor(img_test);
//            tests.add(img_test);
        }
    }


    public void act(float delta) {
        if (isStop) return;
        super.act(delta);
        if (isAi) stateMachine.update();
        moveTime -= delta;

        if (target != null) {//上课和去图书馆精力消耗速度不同
            switch (target.getType()) {
                case ClassRoom:
                    intEnergy -= 2;
                    intHunger -= 3;
                    break;
                case Library:
                    intEnergy--;
                    intHunger -= 3;
                    break;
                case Canteen:
                    intEnergy--;
                    break;
                case BedRoom:
                    intHunger--;
                    break;
                default:
                    intHunger--;
                    intEnergy--;
                    break;
            }
        }
        intEnergy = Math.max(0, intEnergy);
        intHunger = Math.max(0, intHunger);
        //刷新进度条的比例
        imgEnergyTop.setScaleX((float) intEnergy / intEnergyMax);
        imgHungerTop.setScaleX((float) intHunger / intHungerMax);
    }

    //增加分数
    public void addScore(int score) {
        intScore += score;
        labScore.setText("" + intScore);
    }

    public void draw(Batch batch, float a) {
        batch.setColor(Color.WHITE);
        switch (direction) {
            case Align.top:
                batch.draw(textureRegions[2], getX(), getY(), getWidth(), getHeight());
                break;
            case Align.bottom:
                batch.draw(textureRegions[0], getX(), getY(), getWidth(), getHeight());
                break;
            case Align.left:
                batch.draw(textureRegions[4], getX(), getY(), getWidth(), getHeight());
                break;
            case Align.right:
                batch.draw(textureRegions[3], getX(), getY(), getWidth(), getHeight());
                break;
            case Align.center://睡觉姿势
                batch.draw(textureRegions[1], getX(), getY(), getWidth() / 2, getHeight() / 2
                        , getWidth(), getHeight(), 1, 1, 90);
                break;
        }

        imgEnergyBg.setPosition(getX(Align.center), getTop() + 5, Align.bottom);
        imgEnergyBg.draw(batch, a);

        imgEnergyTop.setPosition(getX(Align.center), getTop() + 5, Align.bottom);
        imgEnergyTop.draw(batch, a);

        imgHungerBg.setPosition(getX(Align.center), getTop() + 7f, Align.bottom);
        imgHungerBg.draw(batch, a);

        imgHungerTop.setPosition(getX(Align.center), getTop() + 7f, Align.bottom);
        imgHungerTop.draw(batch, a);

        labScore.setPosition(getX(Align.center), getY() + 30, Align.bottom);
        labScore.draw(batch, a);

    }
}
