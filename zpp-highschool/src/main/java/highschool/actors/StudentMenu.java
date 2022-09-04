package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import highschool.stages.StageGame;
import highschool.tools.FreePaint;

import static highschool.Config.boxSize;
import static highschool.Game.game;
import static highschool.ai.StudentState.MOVESTEPS;
import static highschool.ai.StudentState.START;

//学生菜单，点击后出现，可选择控制该学生去什么地方
public class StudentMenu extends Group {
    private Image mask;
    private FreePaint paint = new FreePaint(15);
    private Array<GameObject> userObjects;
    public Student student;
    public Room room;

    public StudentMenu() {
        userObjects = StageGame.userObjects;
        mask = game.getImage(new Color(0, 0, 0, 0.6f));
        addActor(mask);
        Array<GameObject> houses = new Array<>();//所有玩家建筑的房子
        for (int m = 0; m < userObjects.size; m++) {
            GameObject house = userObjects.get(m);
            if (house.isHouse()) {
                houses.add(house);
            }
        }
        mask.setSize(110, 22 * (houses.size + 1));
        setSize(mask.getWidth(), mask.getHeight());
        Image imgAsk = game.getImageText("去哪儿呢？", paint);
        imgAsk.setPosition(2, getHeight() - 2, Align.topLeft);
        addActor(imgAsk);
        for (int m = 0; m < houses.size; m++) {
            final GameObject house = houses.get(m);
            Button btnGoHouse = game.getButton(getWidth() - 4, 20, 8);
            btnGoHouse.setPosition(2, imgAsk.getY() - 2 - 22 * m, Align.topLeft);
            addActor(btnGoHouse);
            Image imgText;
            switch (house.getType()) {
                case ClassRoom:
                    imgText = game.getImageText("去教学楼" + house.index + "号楼", paint);
                    imgText.setColor(Color.BLACK);
                    break;
                case BedRoom:
                    imgText = game.getImageText("去宿舍" + house.index + "号楼", paint);
                    imgText.setColor(Color.BLACK);
                    break;
                case Canteen:
                    imgText = game.getImageText("去食堂", paint);
                    imgText.setColor(Color.BLACK);
                    break;
                case Stadium:
                    imgText = game.getImageText("去体育馆", paint);
                    imgText.setColor(Color.BLACK);
                    break;
                case Library:
                    imgText = game.getImageText("去图书馆" + house.index + "号楼", paint);
                    imgText.setColor(Color.BLACK);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + house.getType());
            }
            btnGoHouse.add(imgText);
            btnGoHouse.addListener(new ClickListener() {//按钮监听
                public void clicked(InputEvent event, float x, float y) {
                    StudentMenu.this.remove();//移除该菜单栏
                    if (student != null) {//学生的菜单选项
                        final Student finalStudent = student;
                        final Group map = house.getParent();//地图容器
                        //判断此刻的学生是处于某个房间，还是处于大地图中？
                        if (student.getParent() == map) {//处于大地图中
                            startMove(house, student);//开始寻路移动
                        } else {//处于某个房间中
                            final Room room = (Room) finalStudent.getParent();
                            finalStudent.isAi = false;
                            finalStudent.clearActions();
                            finalStudent.isStop = false;
                            room.studentLeave(finalStudent, new Runnable() {
                                public void run() {
                                    startMove(house, room, map, finalStudent);
                                    room.nextStudent(finalStudent);//把空位交给下一个同学
                                }
                            });
                        }
                    } else {//驱散队伍的菜单选项
                        for (int i = 0; i < room.queues.size; i++) {//队伍
                            final Student student1 = room.queues.get(i);
                            //让每个学生出发有点时差避免重叠在一起移动
                            student1.addAction(Actions.delay(i * 0.1f, Actions.run(new Runnable() {
                                public void run() {
                                    startMove(house, student1);
                                }
                            })));
                        }
                        room.queues.clear();
                        room.labQueue.setText("" + room.queues.size);
                    }
                }
            });
        }

        Button btn_close = game.getCloseButton(20, Color.valueOf("d70015"), Color.WHITE);
        btn_close.setPosition(getWidth(), getHeight(), Align.topRight);
        addActor(btn_close);
        btn_close.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.removeDialog(StudentMenu.this);
                if (student != null) {
                    student.setDebug(false);
                    student.isStop = false;
                }
            }
        });
    }

    private void startMove(GameObject house, Room room, Group map, Student student) {
        student.setPosition(room.link.getX(Align.center), room.link.getY(Align.center), Align.center);
        map.addActor(student);//将学生移到大地图中
        startMove(house, student);//开始寻路移动
    }

    private void startMove(GameObject house, Student student) {
        student.createMovePaths((int) (house.getX(Align.center) / boxSize), (int) (house.getY(Align.center) / boxSize));//寻找路径
        student.clearActions();//将学生本来的动画清理掉
        student.isStop = false;//学生终止暂停
        student.isAi = true;//学生开启ai自动运行
        if (student.paths.size == 0) {//如果没有找到路，回到初始状态
            student.stateMachine.changeState(START);
        } else {//有路，奥利给
            student.stateMachine.changeState(MOVESTEPS);
            student.target = house;
        }
    }

    public void act(float delta) {
        super.act(delta);
        toFront();
    }
}
