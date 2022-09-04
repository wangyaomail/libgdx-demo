package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import highschool.Config;
import highschool.Game;
import highschool.stages.StageGame;
import highschool.tools.FreePaint;

import static highschool.Game.game;


/**
 * 期末总结
 */

public class DialogEndSummary extends Group {
    public Image img_bg;
    private Button btnStart;
    private Label labNumber, labPassNumber, labPassRate, labArea, labBuildArea, labBuildRate, labRate;
    private FreePaint paint = new FreePaint(20);

    public DialogEndSummary(Array<Student> students, int intArea) {
        game.playSound("sound/oend.mp3");
        img_bg = game.getImage(new Color(1, 1, 1, 0.9f));
        img_bg.setSize(Game.WIDTH * 0.5f, 300);
        setSize(img_bg.getWidth(), img_bg.getHeight());
        addActor(img_bg);
//        Button btn_close = game.getCloseButton();
//        btn_close.setPosition(getWidth(), getHeight(), Align.center);
//        addActor(btn_close);
//        btn_close.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y) {
//                game.removeDialog(DialogEndSummary.this);
//            }
//        });

        Image imgEnd = game.getImageText("学年结束");
        imgEnd.setColor(Color.BLACK);
        imgEnd.setPosition(getWidth() / 2, getHeight() - 30, Align.top);
        addActor(imgEnd);

        Image imgStudentNumber = game.getImageText("学生总数:", paint);
        imgStudentNumber.setColor(Color.BLACK);
        imgStudentNumber.setPosition(30, imgEnd.getY() - 10, Align.topLeft);
        addActor(imgStudentNumber);

        labNumber = game.getLabel("0");
        labNumber.setColor(Color.BLACK);
        labNumber.setPosition(imgStudentNumber.getRight(), imgStudentNumber.getY(Align.center), Align.left);
        addActor(labNumber);

        Image imgPassNumber = game.getImageText("毕业数:", paint);
        imgPassNumber.setColor(Color.BLACK);
        imgPassNumber.setPosition(labNumber.getRight() + 100, imgEnd.getY() - 10, Align.topLeft);
        addActor(imgPassNumber);

        labPassNumber = game.getLabel("0");
        labPassNumber.setColor(Color.BLACK);
        labPassNumber.setPosition(imgPassNumber.getRight(), imgPassNumber.getY(Align.center), Align.left);
        addActor(labPassNumber);

        Image imgPassRate = game.getImageText("合格率:", paint);
        imgPassRate.setColor(Color.BLACK);
        imgPassRate.setPosition(labPassNumber.getRight() + 100, imgEnd.getY() - 10, Align.topLeft);
        addActor(imgPassRate);

        labPassRate = game.getLabel("0");
        labPassRate.setColor(Color.BLACK);
        labPassRate.setPosition(imgPassRate.getRight(), imgPassRate.getY(Align.center), Align.left);
        addActor(labPassRate);

        Image imgArea = game.getImageText("学校面积:", paint);
        imgArea.setColor(Color.BLACK);
        imgArea.setPosition(30, imgStudentNumber.getY() - 10, Align.topLeft);
        addActor(imgArea);

        labArea = game.getLabel("0");
        labArea.setColor(Color.BLACK);
        labArea.setPosition(imgArea.getRight(), imgArea.getY(Align.center), Align.left);
        addActor(labArea);


        Image imgBuildArea = game.getImageText("建设面积:", paint);
        imgBuildArea.setColor(Color.BLACK);
        imgBuildArea.setPosition(labNumber.getRight() + 100, imgStudentNumber.getY() - 10, Align.topLeft);
        addActor(imgBuildArea);

        labBuildArea = game.getLabel("0");
        labBuildArea.setColor(Color.BLACK);
        labBuildArea.setPosition(imgBuildArea.getRight(), imgBuildArea.getY(Align.center), Align.left);
        addActor(labBuildArea);


        Image imgBuildRate = game.getImageText("建设率:", paint);
        imgBuildRate.setColor(Color.BLACK);
        imgBuildRate.setPosition(labPassNumber.getRight() + 100, imgStudentNumber.getY() - 10, Align.topLeft);
        addActor(imgBuildRate);

        labBuildRate = game.getLabel("0");
        labBuildRate.setColor(Color.BLACK);
        labBuildRate.setPosition(imgBuildRate.getRight(), imgBuildRate.getY(Align.center), Align.left);
        addActor(labBuildRate);


        Image imgRate = game.getImageText("学校评级:");
        imgRate.setSize(imgRate.getWidth() * 0.85f, imgRate.getHeight() * 0.85f);
        imgRate.setColor(Color.BLACK);
        imgRate.setPosition(getWidth() / 2 - 20, 140, Align.top);
        addActor(imgRate);

        labRate = game.getLabel("0");
        labRate.setFontScale(1.5f);
        labRate.setColor(Color.BLACK);
        labRate.setPosition(imgRate.getRight(), imgRate.getY(Align.center), Align.left);
        addActor(labRate);


        //成绩单确定
        btnStart = game.getButton(150, 40, 10);
        btnStart.setColor(Color.valueOf("5ba730ff"));
        btnStart.setPosition(getWidth() / 2, 60, Align.center);
        Image imgStart = game.getImageText("确定");
        imgStart.setSize(imgStart.getWidth() * 0.8f, imgStart.getHeight() * 0.8f);
        imgStart.setPosition(btnStart.getWidth() / 2, btnStart.getHeight() / 2, Align.center);
        btnStart.addActor(imgStart);
        addActor(btnStart);
        btnStart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                StageGame stageGame = (StageGame) game.stage;
                stageGame.reStart();//重新开始
                game.removeDialog(DialogEndSummary.this);
            }
        });

        labNumber.setText("" + students.size);//总学生人数
        Array<Student> arrGraduate = new Array();//毕业的学生
        int pass = 0;
        int num = 0;
        for (int i = 0; i < students.size; i++) {
            Student student = students.get(i);
            if (student.intGrade >= 4) {//完成4个学年的学生毕业
                arrGraduate.add(student);
                num++;
            }
            if (student.intScore >= Config.intSemesterCredit * student.intGrade)
                pass++;//学分合格的人数（年级越高，需要的学分越多）
        }
        students.removeAll(arrGraduate, true);//将毕业的学生移除

        float rate = ((int) ((float) pass / (students.size+num) * 1000f)) / 10f;//合格率
        num = 0;
        labPassRate.setText(rate + "%");

        int intAreaAll = intArea * intArea;//总面积
        labArea.setText("" + intAreaAll);

        int intBuildArea = 0;//建设的面积
        intBuildArea += intArea * 4 - 6;//围墙所占面积
        for (int i = 0; i < StageGame.userObjects.size; i++) {
            GameObject gameObject = StageGame.userObjects.get(i);
            if (gameObject.isBuiding()) {
                if (gameObject.isHouse()) {
                    intBuildArea += 9;
                } else intBuildArea += 1;
            }
        }
        labBuildArea.setText("" + intBuildArea);//建设的面积
        float buildRate = ((int) ((float) intBuildArea / intAreaAll * 1000f)) / 10f;//建设率
        labBuildRate.setText(buildRate + "%");//建设率
        //合格率计算方式
        float rateAll = (rate + buildRate) / 2;
        if (rateAll > 80) {//如果合格率和建设还不错，则学校评级上升，否则保持不变
            //合格
            Config.intLevel++;
            Config.ineligibleSchoolYear = 0;//如果合格，该参数归零，重新统计
            for (Student student : arrGraduate) {//遍历毕业生数组，累计其中学分合格的人数

                if (student.intScore > Config.intSemesterCredit * student.intGrade) {
                    Config.totalNumberQualifiedGraduates++;//累计合格的毕业生人数
                }
            }
        } else {//不合格
            Config.ineligibleSchoolYear++;
        }
        //labPassNumber.setText("" + arrGraduate.size);//毕业人数
        labPassNumber.setText("" + Config.totalNumberQualifiedGraduates);//毕业人数
        //Config.intLevel = (int) (rateAll / 25) + 1;
        labRate.setText("" + Config.intLevel);

        StageGame.labStudentNumber.setText("" + students.size);
    }
    /*
    * 破产评定
    * */
    public void isEnd() {
        System.out.println(Config.totalNumberQualifiedGraduates);
        if (Config.ineligibleSchoolYear >= 3) {
            //连续3年成绩很烂，游戏结束
           // Gdx.app.log("bbbbbbbb", "" + Config.ineligibleSchoolYear);
            game.removeDialog(this);
            game.showDialog(new DialogGameOver());
        } else if (Config.totalNumberQualifiedGraduates >= Config.targetTotalNumberQualifiedGraduates) {
            System.out.println(123);
            //达到了目标合格人数，游戏结束
            System.out.println("毕业人数："+Config.targetTotalNumberQualifiedGraduates);
            game.removeDialog(this);
            game.showDialog(new DialogWin());
        }
    }
}
