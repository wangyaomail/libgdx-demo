package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import highschool.Game;
import highschool.stages.StageGame;
import highschool.tools.FreePaint;

import java.util.Comparator;

import static highschool.Game.game;

public class Rank extends Group {
    private Table table = new Table();
    private Array<Student> students = new Array<>();
    private FreePaint paint = new FreePaint(25);
    private SortType sortType = SortType.Score;//排序方式
    private Image imgTarget;//用来标记当前的排序类型
    private ScrollPane scrollPane;

    enum SortType {
        Score, Energy, Hunger
    }

    public Rank() {
        Image imgBg = game.getImage(Color.DARK_GRAY);
        imgBg.setColor(0, 0, 0, 0.6f);
        imgBg.setSize(150, Game.HEIGHT - 60);
        addActor(imgBg);
        setSize(imgBg.getWidth(), imgBg.getHeight());

        imgTarget = game.getImage(Color.WHITE);
        imgTarget.setSize(30, 15);

        scrollPane = game.getScrollPane(table, Color.CLEAR);
        scrollPane.setSize(getWidth(), getHeight());
        addActor(scrollPane);
        //scrollPane.debug();
    }

    //按分数降序
    Comparator<Student> compScore = new Comparator<Student>() {
        public int compare(Student t1, Student t2) {
            if (t1.intScore > t2.intScore) return -1;
            if (t1.intScore < t2.intScore) return 1;
            return 0;
        }
    };

    //按体力升序
    Comparator<Student> compEnergy = new Comparator<Student>() {
        public int compare(Student t1, Student t2) {
            if (t1.intEnergy > t2.intEnergy) return 1;
            if (t1.intEnergy < t2.intEnergy) return -1;
            return 0;
        }
    };

    //按饥饿升序
    Comparator<Student> compHunger = new Comparator<Student>() {
        public int compare(Student t1, Student t2) {
            if (t1.intHunger > t2.intHunger) return 1;
            if (t1.intHunger < t2.intHunger) return -1;
            return 0;
        }
    };

    private Image prefItemBg;

    public void refush(final Array<Student> students) {
        this.students = students;
        table.clearChildren();
        switch (sortType) {
            case Score:
                students.sort(compScore);
                imgTarget.setPosition(40, 10, Align.left);
                break;
            case Hunger:
                students.sort(compHunger);
                imgTarget.setPosition(78, 10, Align.left);
                break;
            case Energy:
                students.sort(compEnergy);
                imgTarget.setPosition(118, 10, Align.left);
                break;
        }

        final int allHeight = students.size * 20;

        for (int i = 0; i < students.size; i++) {
            if (i == 0) {//title
                final Group item0 = new Group();
                item0.setSize(getWidth(), 20);
                final Image itemBg0 = game.getImage(new Color(0, 0, 0, 0.8f));
                itemBg0.setSize(item0.getWidth(), item0.getHeight() - 2);
                item0.addActor(itemBg0);
                table.add(item0);
                table.row();//换行

                item0.addActor(imgTarget);

                Image imgRank = game.getImageText("名次", paint);
                imgRank.setSize(imgRank.getWidth() * 0.5f, imgRank.getHeight() * 0.5f);
                imgRank.setPosition(5, item0.getHeight() / 2, Align.left);
                item0.addActor(imgRank);
                imgRank.addListener(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        sortType = SortType.Score;
                        refush(students);
                    }
                });

                Image imgScore = game.getImageText("学分", paint);
                imgScore.setSize(imgScore.getWidth() * 0.5f, imgScore.getHeight() * 0.5f);
                imgScore.setColor(Color.CYAN);
                imgScore.setPosition(40, item0.getHeight() / 2, Align.left);
                item0.addActor(imgScore);
                imgScore.addListener(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        sortType = SortType.Score;
                        refush(students);
                    }
                });

                Image imgHunger = game.getImageText("饥饿", paint);
                imgHunger.setSize(imgHunger.getWidth() * 0.5f, imgHunger.getHeight() * 0.5f);
                imgHunger.setColor(Color.ORANGE);
                imgHunger.setPosition(78, item0.getHeight() / 2, Align.left);
                item0.addActor(imgHunger);
                imgHunger.addListener(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        sortType = SortType.Hunger;
                        refush(students);
                    }
                });

                Image imgEnergy = game.getImageText("体力", paint);
                imgEnergy.setSize(imgEnergy.getWidth() * 0.5f, imgEnergy.getHeight() * 0.5f);
                imgEnergy.setColor(Color.GREEN);
                imgEnergy.setPosition(118, item0.getHeight() / 2, Align.left);
                item0.addActor(imgEnergy);
                imgEnergy.addListener(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        sortType = SortType.Energy;
                        refush(students);
                    }
                });
            }

            final Student student = students.get(i);
            final Group item = new Group();
            item.setSize(getWidth(), 20);
            final Image itemBg = game.getImage(new Color(0, 0, 0, 0.8f));
            itemBg.setSize(item.getWidth(), item.getHeight() - 2);
            item.addActor(itemBg);
            table.add(item);
            table.row();//换行

            Label labNumber = game.getLabel("100");
            //labNumber.setColor(Color.BLACK);
            labNumber.setFontScale(0.8f);
            labNumber.setAlignment(Align.center);
            labNumber.setPosition(7, Align.center);
            item.addActor(labNumber);
            labNumber.setText("" + (i + 1));

            Label labScore = game.getLabel("100");
            labScore.setColor(Color.CYAN);
            labScore.setFontScale(0.8f);
            labScore.setAlignment(Align.center);
            labScore.setPosition(42, Align.center);
            item.addActor(labScore);
            labScore.setText("" + student.intScore);

            Label labHunger = game.getLabel("100");
            labHunger.setColor(Color.ORANGE);
            labHunger.setFontScale(0.8f);
            labHunger.setAlignment(Align.center);
            labHunger.setPosition(80, Align.center);
            item.addActor(labHunger);
            labHunger.setText("" + student.intHunger);

            Label labEnergy = game.getLabel("100");
            labEnergy.setColor(Color.GREEN);
            labEnergy.setFontScale(0.8f);
            labEnergy.setAlignment(Align.center);
            labEnergy.setPosition(118, Align.center);
            item.addActor(labEnergy);
            labEnergy.setText("" + student.intEnergy);

            item.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    for (int m = 0; m < students.size; m++) {//把其他学生的绿框框都关掉
                        Student st = students.get(m);
                        st.setDebug(false);
                        st.isStop = false;
                    }
                    student.debug();
                    itemBg.setColor(Color.GOLD);
                    if (prefItemBg != null) prefItemBg.setColor(new Color(0, 0, 0, 0.8f));
                    prefItemBg = itemBg;

                    StageGame.studentMenu.student = student;
                    StageGame.studentMenu.setPosition(0, event.getStageY(), Align.topRight);
                    addActor(StageGame.studentMenu);
                }
            });
        }
    }

    public void draw() {

    }

}
