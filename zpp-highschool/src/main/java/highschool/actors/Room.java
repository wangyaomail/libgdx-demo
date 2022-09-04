package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import highschool.Config;
import highschool.stages.StageGame;
import highschool.tools.FreePaint;
import highschool.tools.Type;

import java.util.Comparator;

import static highschool.Game.game;
import static highschool.ai.StudentState.START;

/**
 * 房间
 */
public class Room extends Group {
    public Type type;
    public Array<Vector2> seats = new Array<>();//学生的位置
    public Array<Student> students = new Array<>();//进入到教室的学生
    public Array<Student> queues = new Array<>();//排队打算进教室的学生
    public GameObject link;//该房间内部对应的大地图上的房子
    private Image bg, mask, football, bowl, door;
    private Button btnClose;
    private FreePaint paint = new FreePaint(15);
    public Label labNumber, labQueue;
    private int max;//最多能容纳的学生数
    public String buildName;//建筑物的名字
    public Vector2 startPoint;//进入教室的第一个点

    //创建一个提示消息
    public void showMessage(Group point, String msg, Color color) {
        Image imgMsg = game.getImageText(msg, paint);
        imgMsg.setTouchable(Touchable.disabled);
        imgMsg.setColor(color);
        imgMsg.setPosition(point.getWidth(), point.getHeight(), Align.bottom);
        point.addActor(imgMsg);
        //加一个自动消失的动画
        imgMsg.addAction(Actions.sequence(Actions.alpha(0), Actions.alpha(1, 0.1f)
                , Actions.delay(1), Actions.alpha(0, 3), Actions.removeActor()));
        imgMsg.addAction(Actions.moveBy(0, 100, 4));
    }

    private String[] teacherSays = {"老师:在这个问题上,你可以当老师了!", "老师:为中华民族伟大复兴而读书", "老师:你的想法很有创意，看来你是认真思考了"
            , "老师:你的进步可真大，老师为你感到高兴", "老师:你真行！一次比一次有进步！", "老师:瞧！问题在你面前害怕了", "老师:我为你今天的表现感到骄傲"
            , "老师:你的自学能力真让我惊讶", "老师:我真喜欢你努力钻研的精神", "老师:你们班是整个楼道最吵的", "老师:你们是我带过的最差一届"
            , "老师:不要以为你在下面干什么我都看不见", "老师:是你在教还是我在教？来来来,你来讲", "老师:谁告诉你们上大学就自由了?", "老师:你们可以讲话,但不要出声."};

    public Room(Type type, final GameObject link) {
        this.type = type;
        this.link = link;
        bg = game.getImage("map/room.png");
        mask = game.getImage(new Color(0, 0, 0, 0.5f));
        mask.setSize(bg.getWidth() + 20, bg.getHeight() + 30 + 50);
        addActor(mask);
        setSize(mask.getWidth(), mask.getHeight());
        bg.setPosition(getWidth() / 2, getHeight() / 2 - 30, Align.center);
        addActor(bg);
        door = game.getImage("map/door.png");
        door.setPosition(getWidth() / 2, 9, Align.bottom);
        addActor(door);

        switch (type) {
            case ClassRoom://教室
                max = 96;
                //max = 120;
                buildName = "教室";
                createVaules(buildName + "1号楼");
                //加上黑板
                Image imgBlackbBoard = game.getImage("map/blackboard.png");
                imgBlackbBoard.setPosition(getWidth() / 2, bg.getTop(), Align.top);
                addActor(imgBlackbBoard);
                //加上老师
                final Button imgTeacher = game.getButton("map/teacherboy.png");
                imgTeacher.setSize(19, 45);
                imgTeacher.setPosition(getWidth() / 2, bg.getTop() - 20, Align.top);
                addActor(imgTeacher);
                imgTeacher.addListener(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        String say = teacherSays[MathUtils.random(teacherSays.length - 1)];//随机一句话
                        showMessage(imgTeacher, say, Color.GREEN);
                    }
                });
                //课桌
                for (int n = 0; n < 8; n++) {
                    for (int m = 0; m < 6; m++) {
                        Image table = game.getImage("map/table.png");//课桌
                        float y = bg.getTop() - 95 - n * 30;
                        float dy = y - 10;
                        if (n == 7 && m == 5) {
                            startPoint = new Vector2(getWidth() / 2, dy);
                        }
                        if (m < 2) {
                            float x = 35 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else if (m < 4) {
                            float x = 48 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else {
                            float x = 61 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        }
                        addActor(table);
                    }
                }
                break;
            case Library://图书馆
                max = 72;
                buildName = "图书馆";
                createVaules(buildName + "1号楼");
                //加上书
                Image books = game.getImage("map/books.png");
                books.setPosition(getWidth() / 2, bg.getTop() - 10, Align.top);
                addActor(books);
                //加上桌子
                for (int n = 0; n < 6; n++) {
                    for (int m = 0; m < 6; m++) {
                        Image table = game.getImage("map/table.png");//课桌
                        float y = bg.getTop() - 150 - n * 30;
                        float dy = y - 10;
                        if (n == 5 && m == 5) {
                            startPoint = new Vector2(getWidth() / 2, dy);
                        }
                        if (m < 2) {
                            float x = 35 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else if (m < 4) {
                            float x = 48 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else {
                            float x = 61 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        }
                        addActor(table);
                    }
                }
                break;
            case Canteen:
                max = Config.intLevel * 24;//一个等级能同时容纳24人吃饭
                buildName = "食堂";
                createVaules(buildName);
                //食物
                bowl = game.getImage("map/bowl.png");
                bowl.setPosition(getWidth() / 2, 40, Align.bottom);
                addActor(bowl);
                //桌子
                int ranks = 0;
                if(Config.intLevel <=4){
                    ranks = Config.intLevel;
                }else{
                    ranks = 4;
                }
                for (int n = 0; n < ranks * 2; n++) {//食堂的桌子数跟学校等级有关，1个等级12张桌子
                    for (int m = 0; m < 6; m++) {
                        Image table = game.getImage("map/tablefood.png");//桌
                        float y = bg.getTop() - 100 - n * 30;
                        float dy = y - 10;
                        if (n == Config.intLevel * 2 - 1 && m == 5) {
                            startPoint = new Vector2(getWidth() / 2, dy);
                        }
                        if (m < 2) {
                            float x = 35 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else if (m < 4) {
                            float x = 48 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else {
                            float x = 61 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        }
                        addActor(table);
                    }
                }
                break;
            case BedRoom:
                max = 54;
                buildName = "宿舍";
                createVaules(buildName + "1号楼");
                //宿舍
                for (int n = 0; n < 9; n++) {
                    for (int m = 0; m < 6; m++) {
                        Image table = game.getImage("map/bed.png");//床
                        float y = bg.getTop() - 65 - n * 30;
                        float dy = y - 10;
                        if (n == 8 && m == 5) {
                            startPoint = new Vector2(getWidth() / 2, dy);
                        }
                        if (m < 2) {
                            float x = 35 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                        } else if (m < 4) {
                            float x = 48 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                        } else {
                            float x = 61 + m * 45;
                            table.setPosition(x, y);
                            seats.add(new Vector2(x + 2, dy));
                        }
                        addActor(table);
                    }
                }
                break;
            case Stadium:
                max = 96;
                buildName = "体育馆";
                createVaules(buildName);
                //体育馆
                football = game.getImage("map/football.png");
                football.setPosition(getWidth() / 2, getHeight() / 2 - 30, Align.center);
                addActor(football);
                for (int n = 0; n < 8; n++) {
                    for (int m = 0; m < 6; m++) {
                        float y = bg.getTop() - 95 - n * 30;
                        float dy = y - 10;
                        if (n == 7 && m == 5) {
                            startPoint = new Vector2(getWidth() / 2, dy);
                        }
                        if (m < 2) {
                            float x = 35 + m * 45;
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else if (m < 4) {
                            float x = 48 + m * 45;
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        } else {
                            float x = 61 + m * 45;
                            seats.add(new Vector2(x + 2, dy));
                            seats.add(new Vector2(x + 23, dy));
                        }
                    }
                }
                break;
        }

        //关闭按钮
        btnClose = game.getCloseButton();
        btnClose.setPosition(getWidth(), getHeight(), Align.topRight);
        addActor(btnClose);
        btnClose.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                setVisible(false);
            }
        });

        //遣散队伍
        final Button btnGoHouse = game.getButton(80, 20, 8);
        Image imgGoHouse = game.getImageText("驱散队伍", paint);
        imgGoHouse.setColor(Color.BLACK);
        btnGoHouse.add(imgGoHouse);
        btnGoHouse.setPosition(labQueue.getRight() + 50, labQueue.getY(Align.center), Align.left);
        addActor(btnGoHouse);
        btnGoHouse.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                StageGame.studentMenu.room = Room.this;
                StageGame.studentMenu.student = null;
                StageGame.studentMenu.setPosition(btnGoHouse.getRight(), btnGoHouse.getTop(), Align.topLeft);
                Room.this.addActor(StageGame.studentMenu);
            }
        });
    }

    //房间信息
    private Image imgName;

    private void createVaules(String name) {
        imgName = game.getImageText("房间:" + name, paint);
        imgName.setPosition(5, getHeight() - 2, Align.topLeft);
        addActor(imgName);
        Image imgNumber = game.getImageText("人数:", paint);
        imgNumber.setPosition(5, imgName.getY() - 2, Align.topLeft);
        addActor(imgNumber);
        labNumber = game.getLabel("0/" + max);
        labNumber.setPosition(imgNumber.getRight(), imgNumber.getY(Align.center), Align.left);
        addActor(labNumber);
        Image imgQueue = game.getImageText("排队:", paint);
        imgQueue.setPosition(5, imgNumber.getY() - 2, Align.topLeft);
        addActor(imgQueue);
        labQueue = game.getLabel("0");
        labQueue.setPosition(imgQueue.getRight(), imgQueue.getY(Align.center), Align.left);
        addActor(labQueue);
    }

    //刷新名字
    public void refushName(String name) {
        imgName.setDrawable(game.getImageText("房间:" + buildName + name, paint).getDrawable());
    }

    //返回一个空座位
    public Vector2 getNullSeat() {
        if (seats.size == 0) return null;
        return seats.removeIndex(0);
    }

    //增加一个学生
    public void addStudent(final Student student) {
        if (students.size >= max || queues.size > 0) {//没有空位了/或者前面有人排队，则进入排队系统
            student.isAi = false;//将ai暂停了
            queues.add(student);//添加到排队队伍中
            labQueue.setText("" + queues.size);
            return;
        }
        Vector2 point = getNullSeat();
        addStu(student, point);
    }

    public void addStu(final Student student, Vector2 seat) {
        student.isAi = false;//将ai暂停了
        student.setPosition(seat.x, seat.y);
        student.seat = seat;
        students.add(student);
        labNumber.setText(students.size + "/" + max);
        addActor(student);
        refushDepth();

        student.setPosition(getWidth() / 2 + 15, 0, Align.bottom);
        //从门口移动到座位的动画
        SequenceAction seq = Actions.sequence();
        seq.addAction(getMoveAction(student, student.getX(), student.getY(), startPoint.x - student.getWidth() / 2 + 15, startPoint.y));
        if (Math.abs(startPoint.y - seat.y) < 1) {//如果终点在最后一排，那么直接去终点
            seq.addAction(getMoveAction(student, startPoint.x - student.getWidth() / 2, startPoint.y, seat.x, seat.y));
        } else {//否则拐两下再到终点
            Vector2 point2 = new Vector2((seat.x < getWidth() / 2 ? startPoint.x - 50 : startPoint.x + 50) - student.getWidth() / 2, startPoint.y);
            seq.addAction(getMoveAction(student, startPoint.x - student.getWidth() / 2, startPoint.y, point2.x, point2.y));
            Vector2 point3 = new Vector2(point2.x, seat.y);
            seq.addAction(getMoveAction(student, point2.x, point2.y, point3.x, point3.y));
            seq.addAction(getMoveAction(student, point3.x, point3.y, seat.x, seat.y));
        }

        seq.addAction(Actions.run(new Runnable() {
            public void run() {
                float inTime = 0;
                switch (type) {
                    case ClassRoom:
                        inTime = Config.hourTime * 8;//上课8小时
                        student.direction = Align.top;
                        break;
                    case Library:
                        inTime = Config.hourTime * 4;//图书馆4小时
                        student.direction = Align.top;
                        break;
                    case Canteen:
                        inTime = Config.hourTime * 0.5f;//吃饭0.5小时
                        student.direction = Align.top;
                        break;
                    case BedRoom:
                        inTime = Config.hourTime * 7;//睡觉7小时
                        student.direction = Align.center;
                        student.moveBy(10, 5);
                        break;
                    case Stadium:
                        inTime = Config.hourTime * 2;//锻炼2小时
                        student.direction = Align.bottom;
                        break;
                }
                //进入建筑之后，计时做不同的事情
                student.addAction(Actions.sequence(Actions.delay(inTime), Actions.run(new Runnable() {
                    public void run() {
                        switch (type) {
                            case ClassRoom:
                                student.addScore(8);
                                break;
                            case Library:
                                student.addScore(3);
                                break;
                            case Canteen:
                                student.intHunger = student.intHungerMax;//饥饿恢复
                                break;
                            case BedRoom:
                                student.intEnergy = student.intEnergyMax;//精力恢复
                                break;
                            case Stadium:
                                student.addScore(1);
                                break;
                        }
                        studentLeave(student, null);
                    }
                })));
            }
        }));
        student.addAction(seq);
    }

    //学生离开教室
    public void studentLeave(final Student student, Runnable endRun) {
        //从座位移动到门口的动画
        student.setAngle(MathUtils.PI);
        SequenceAction seq = Actions.sequence();
        if (Math.abs(startPoint.y - student.getY()) < 1) {//如果起点在最后一排，那么直接出门
            seq.addAction(getMoveAction(student, student.getX(), student.getY(), startPoint.x - student.getWidth() / 2 - 15, startPoint.y));
        } else {//否则拐两下再出门
            Vector2 point2 = new Vector2((student.getX(Align.center) < getWidth() / 2 ? startPoint.x - 50 : startPoint.x + 50) - student.getWidth() / 2, student.getY());
            seq.addAction(getMoveAction(student, student.getX(), student.getY(), point2.x, point2.y));
            Vector2 point3 = new Vector2(point2.x, startPoint.y);
            seq.addAction(getMoveAction(student, point2.x, point2.y, point3.x, point3.y));
            seq.addAction(getMoveAction(student, point3.x, point3.y, startPoint.x - student.getWidth() / 2 - 15, startPoint.y));
        }
        seq.addAction(getMoveAction(student, startPoint.x - student.getWidth() / 2 - 15, startPoint.y, getWidth() / 2 - student.getWidth() / 2 - 15, 0));
        if (endRun != null) {
            seq.addAction(Actions.run(endRun));
        } else {
            seq.addAction(Actions.run(new Runnable() {
                public void run() {
                    //彻底出门
                    student.target = null;
                    student.setPosition(link.getX(Align.center), link.getY(Align.center), Align.center);
                    link.getParent().addActor(student);//把学生从教室内部返回大地图中
                    student.stateMachine.changeState(START);//切换初始状态
                    student.isAi = true;
                    nextStudent(student);
                }
            }));
        }
        student.addAction(seq);
    }


    private Action getMoveAction(Student student, float startX, float startY, float endX, float endY) {
        float time1 = Vector2.dst(startX, startY, endX, endY) / student.speed * 0.5f;//让学生在房间内部的移动速度快2倍
        return Actions.moveTo(endX, endY, time1);
    }

    //把空座位给下一个学生
    public void nextStudent(Student student) {
        //释放学生坐的座位
        students.removeValue(student, true);
        labNumber.setText(students.size + "/" + max);
        //看看有没有人排队，有人排队就让队伍第一个人进教室
        if (queues.size > 0) {
            Student que = queues.removeIndex(0);
            labQueue.setText("" + queues.size);
            addStu(que, student.seat);
            //student.seat = null;
        } else {
            seats.add(student.seat);
            //student.seat = null;
        }
    }

    //刷新深度
    public void refushDepth() {
        getChildren().sort(new Comparator<Actor>() {
            public int compare(Actor actor, Actor t1) {
                if (actor.getY() > t1.getY()) return -1;
                if (actor.getY() < t1.getY()) return 1;
                else return 0;
            }
        });
        if (football != null) football.toBack();
        if (bowl != null) bowl.toBack();
        bg.toBack();
        mask.toBack();
        btnClose.toFront();
    }

    public void act(float delta) {
        super.act(delta);
        if (isVisible()) refushDepth();
    }
}
