package highschool.stages;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import highschool.Config;
import highschool.actors.*;
import highschool.ai.Astar;
import highschool.tools.FreePaint;
import highschool.tools.Point;
import highschool.tools.Type;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import static highschool.Config.*;
import static highschool.Game.game;
import static highschool.ai.StudentState.MOVESTEPS;
import static highschool.ai.StudentState.START;
import static highschool.tools.Type.*;

public class StageGame extends Stage {
    public Group parent;
    public static Group map;
    public Group top;
    public Group bottomBuiding;//地图父容器，地图容器,顶部容器,底部容器
    private GameObject gameObject, gate;//被选中的图元对象
    private TargetActor target = new TargetActor();
    private Label labLevel, labArea;
    public int intArea = 10;//学校面积
    //教室数量,教室上限，宿舍数量，宿舍上限,食堂数量,食堂学生数量，食堂容纳学生数上限，图书馆数量
    private int[] arrIntNumbers = new int[8], arrIntNumberMax = new int[8];
    private FreePaint font20 = new FreePaint(20);//字体设置
    private static FreePaint font18 = new FreePaint(18);//字体设置
    private DeletSlider deletSlider = new DeletSlider();//删除记时条
    private Array<Button> addButtons = new Array<>();//按钮集合
    private Array<Label> arrNumbers = new Array<>();//数字集合
    public static Array<GameObject> userObjects = new Array<>();//用来保存用户创建的对象
    public Array<GameObject> sandys = new Array<>();//系统创建的对象
    public static Array<Student> students = new Array<>();//用来保存所有学生
    private Image box;
    private Type nextType = ClassRoom;
    private int nextId = 0, intYear;//下一个建设的物体的id，学年
    private Button btnStart, btnPlay, btnRank, btnSkip;//开学,播放按钮，排行按钮
    private boolean isSemestering;//开学中
    public static Label labTime, labYear, labStudentNumber;//时间,学年,学生数量
    private Image imgHoliday, imgYear, imgStudentNumber;//假期,学年,学生数量
    private long longTime = 967766400000L;//时间2000年9月1日
    private SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");
    public static StudentMenu studentMenu;
    public int playSpeed = 1;//世界播放速度
    public Array<Button> playButtons = new Array<>();//控制播放的按钮
    public Rank rank;//排行榜容器


    public StageGame() {
        super(new ScalingViewport(Scaling.stretch, game.WIDTH, game.HEIGHT));//设置舞台适配模式

        Image imgBackground = game.getImage(Color.BLACK);//创建一张背景图
        imgBackground.setSize(game.WIDTH, game.HEIGHT);//设置尺寸为全屏
        addActor(imgBackground);//添加到舞台

        parent = new Group();
        //parent.debug();
        parent.setSize(boxSize * mapSizeX, boxSize * mapSizeY);
        parent.setOrigin(Align.center);//设置舞台原点
        parent.setPosition(getWidth() / 2, 0, Align.bottom);
        addActor(parent);

        map = new Group();//创建一个地图容器，方便我们对整个学校地图进行缩放拖动等操作
        //map.debug();
        map.setSize(boxSize * mapSizeX, boxSize * mapSizeY);
        map.setOrigin(Align.center);
        map.setPosition(parent.getWidth() / 2, parent.getHeight() / 2, Align.center);
        map.setOrigin(getWidth() / 2 - parent.getX(), getHeight() / 2 - parent.getY());
        parent.addActor(map);
        target.setSize(boxSize * 3, boxSize * 3);
        target.debug();
        target.setVisible(false);
        map.addActor(target);
        //对地图容器进行点击监听，实现对整个地图的拖动
        parent.addListener(new InputListener() {
            private boolean isDragged = false;
            private float startX, startY;

            private void refush() {
                refushLevel();
                refushAstar();
                refushLayerDepth();
            }

            //鼠标按下时执行
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //if (isSemestering) return false;//如果已经开学了
                //button值 0鼠标左键，1鼠标右键，2鼠标中键
                isDragged = button == 1;
                startX = x;
                startY = y;
                if (button == 0) {
                    if (event.getTarget() instanceof GameObject) {
                        gameObject = (GameObject) event.getTarget();
                        target.setPosition(event.getTarget().getX(Align.center), event.getTarget().getY(Align.center), Align.center);
                        target.setVisible(true);
                        if (gameObject instanceof Student) {//如果选中了学生
                            for (int m = 0; m < students.size; m++) {//把其他学生的绿框框都关掉
                                Student st = students.get(m);
                                st.setDebug(false);
                                st.isStop = false;
                            }
                            target.setVisible(false);
                            gameObject.setDebug(true);
                            studentMenu.setPosition(gameObject.getRight(), gameObject.getTop(), Align.topLeft);
                            gameObject.getParent().addActor(studentMenu);
                            Student student = (Student) gameObject;
                            student.isStop = true;
                            studentMenu.student = student;
                            game.playSound("sound/btnclicked.mp3");
                        } else if (gameObject.isBuiding()) {//如果选中了玩家建设的建筑
                            game.playSound("sound/btnclicked.mp3");
                            target.refushSize(gameObject.getWidth(), gameObject.getHeight());
                            target.setIsRight(true);
                            if (isSemestering && gameObject.room != null) {//判断是否已经开学且建筑为房屋，显示建筑内部细节
                                //将区域内的其他的都隐藏
                                for (int m = 0; m < userObjects.size; m++) {
                                    GameObject obj = userObjects.get(m);
                                    if (obj.isHouse()) {
                                        //其他屋子关闭监听点击事件
                                        obj.room.setVisible(false);
                                    }
                                }
                                gameObject.room.setPosition(gameObject.getRight(), gameObject.getTop(), Align.topLeft);
                                gameObject.room.setVisible(true);
                                gameObject.room.toFront();
                            }
                            if (isSemestering) return false;
                            gameObject.clearActions();
                            /*
                            校园场景内建筑删除操作。进度条
                            * */
                            gameObject.addAction(Actions.delay(0.2f, Actions.run(new Runnable() {
                                public void run() {
                                    deletSlider.setPosition(gameObject.getX(Align.center), gameObject.getTop() + 30, Align.bottom);
                                    deletSlider.play(new Runnable() {
                                        public void run() {
                                            gameObject.remove();
                                            userObjects.removeValue(gameObject, true);//从用户创建列表中移除
                                            switch (gameObject.getType()) {
                                                case ClassRoom:
                                                    arrIntNumbers[0]--;
                                                    break;
                                                case BedRoom:
                                                    arrIntNumbers[1]--;
                                                    break;
                                                case Canteen:
                                                    arrIntNumbers[2]--;
                                                    break;
                                                case Library:
                                                    arrIntNumbers[3]--;
                                                    break;
                                                case Floor:
                                                    arrIntNumbers[4]--;
                                                    break;
                                                case Fountain:
                                                    arrIntNumbers[5]--;
                                                    break;
                                                case Planter:
                                                    arrIntNumbers[6]--;
                                                    break;
                                                case Stadium:
                                                    arrIntNumbers[7]--;
                                                    break;
                                            }
                                            if (gameObject.getType() == ClassRoom || gameObject.getType() == BedRoom || gameObject.getType() == Library) {
                                                //重新刷新是几号楼
                                                int index = 1;
                                                for (int m = 0; m < userObjects.size; m++) {
                                                    GameObject obj = userObjects.get(m);
                                                    if (obj.getType() == gameObject.getType()) {
                                                        obj.refushIndex(index);
                                                        index++;
                                                    }
                                                }
                                            }
                                            refush();
                                        }
                                    });
                                    map.addActor(deletSlider);
                                }
                            })));
                        } else {//如果选中了不是房屋的建筑
                            if (isSemestering) return false;
                            if (nextType == Floor || nextType == Fountain || nextType == Planter) {//如果打算添加一个水泥路/喷泉/绿植
                                target.refushSize(boxSize, boxSize);
                                boolean isCanBuiding = gameObject.getType() == Sandy;
                                if (!isCanBuiding) {
                                    showMessage(target, "该区域无法建设", Color.RED);
                                    target.setIsRight(false);
                                    return true;
                                }
                                boolean isMax = arrIntNumbers[nextId] >= arrIntNumberMax[nextId];
                                if (isMax) {//判断建筑是否达到上限
                                    showMessage(target, "该建筑已达上限", Color.RED);
                                    target.setIsRight(false);
                                    return true;
                                }
                                game.playSound("sound/build.mp3");
                                target.setIsRight(true);
                                GameObject newGameObject = new GameObject(nextType);
                                userObjects.add(newGameObject);
                                newGameObject.setPosition(gameObject.getX(Align.center), gameObject.getY(Align.center), Align.center);
                                map.addActor(newGameObject);
                                gameObject = newGameObject;
                                arrIntNumbers[nextId]++;
                                refush();
                            } else {//如果打算添加楼房
                                if (!gameObject.isCanBuiding()) {
                                    showMessage(target, "该区域无法建设", Color.RED);
                                    target.setIsRight(false);
                                    return true;
                                }
                                //判断准备安放的建筑是否已达限制
                                boolean isMax = arrIntNumbers[nextId] >= arrIntNumberMax[nextId];
                                if (isMax) {
                                    showMessage(target, "该建筑已达上限", Color.RED);
                                    target.setIsRight(false);
                                    return true;
                                }
                                //判断是否能安放建筑
                                boolean isCanBuiding = true;
                                String msg = "";
                                for (Actor actor : map.getChildren()) {
                                    if (actor == target) continue;
                                    if (actor instanceof GameObject) {
                                        GameObject obj = (GameObject) actor;
                                        if (obj.isBuiding()) {
                                            if (obj.isHouse()) {
                                                target.refushSize(boxSize * 4, boxSize * 4);//扩大范围，以排除掉房子相邻的情况
                                                msg = "楼房之间需要间隙";
                                            } else {
                                                target.refushSize(boxSize * 3, boxSize * 3);
                                                msg = "楼房与道路喷泉绿植不能重叠";
                                            }
                                            if (game.isOverlaps(obj, target)) {
                                                isCanBuiding = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                target.refushSize(boxSize * 3, boxSize * 3);
                                if (!isCanBuiding) {
                                    showMessage(target, msg, Color.RED);
                                    target.setIsRight(false);
                                    return true;
                                }
                                //
                                target.setIsRight(isCanBuiding && gameObject.isCanBuiding() && !isMax);
                                if (target.isRight) {//添加一个建筑物
                                    game.playSound("sound/build.mp3");
                                    GameObject newGameObject = new GameObject(nextType);
                                    userObjects.add(newGameObject);
                                    //newGameObejct.setXYId(gameObejct.xid, gameObejct.yid);
                                    newGameObject.setPosition(gameObject.getX(Align.center), gameObject.getY(Align.center), Align.center);
                                    map.addActor(newGameObject);
                                    gameObject = newGameObject;
                                    arrIntNumbers[nextId]++;
                                    refush();
                                    if (gameObject.getType() == ClassRoom || gameObject.getType() == BedRoom || gameObject.getType() == Library) {
                                        gameObject.refushIndex(arrIntNumbers[nextId]);//刷新几号楼
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }

            //鼠标按下并拖动时执行
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                //if (isSemestering) return;//如果已经开学了
                if (isDragged) {
                    parent.moveBy((x - startX), (y - startY));
                    map.setOrigin(getWidth() / 2 - parent.getX(), getHeight() / 2 - parent.getY());
                }
            }

            //鼠标按键抬起时执行,移除房屋时鼠标抬起瞬间
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //if (isSemestering) return;//如果已经开学了
                isDragged = false;
                if (!isSemestering && gameObject != null) gameObject.clearActions();
                deletSlider.remove();
            }

            //鼠标未按下并移动时执行
            public boolean mouseMoved(InputEvent event, float x, float y) {
                //Gdx.app.log("aaaaaa","鼠标移动");
                return false;
            }

        });

        createDefateMap();//创建基本地图
        createAstar();//创建寻路算法
        // createStudents();//创建学生
        map.addActor(target);

        //顶部容器
        top = new Group();
        Image imgTopbg = game.getImage(new Color(0, 0, 0.5f, 0.5f));
        imgTopbg.setSize(getWidth(), 40);
        top.setSize(imgTopbg.getWidth(), imgTopbg.getHeight());
        top.setPosition(getWidth() / 2, getHeight(), Align.top);
        top.addActor(imgTopbg);
        addActor(top);

        //暂停
        final Button btnPause = game.getButton("images/btnpause.png");
        playButtons.add(btnPause);
        btnPause.setSize(btnPause.getWidth() * 0.3f, btnPause.getHeight() * 0.3f);
        btnPause.setPosition(700, top.getHeight() / 2, Align.left);
        top.addActor(btnPause);
        btnPause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                for (Button button : playButtons) {
                    button.setColor(Color.WHITE);
                }
                btnPause.setColor(Color.GREEN);
                playSpeed = 0;
            }
        });

        //播放
        btnPlay = game.getButton("images/btnplay.png");
        playButtons.add(btnPlay);
        btnPlay.setSize(btnPlay.getWidth() * 0.3f, btnPlay.getHeight() * 0.3f);
        btnPlay.setPosition(btnPause.getRight() + 20, top.getHeight() / 2, Align.left);
        top.addActor(btnPlay);
        btnPlay.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                for (Button button : playButtons) {
                    button.setColor(Color.WHITE);
                }
                btnPlay.setColor(Color.GREEN);
                playSpeed = 1;
            }
        });

        //10倍播放
        final Button btnPlay10 = game.getButton("images/btnplayx10.png");
        playButtons.add(btnPlay10);
        btnPlay10.setSize(btnPlay10.getWidth() * 0.3f, btnPlay10.getHeight() * 0.3f);
        btnPlay10.setPosition(btnPlay.getRight() + 15, top.getHeight() / 2, Align.left);
        top.addActor(btnPlay10);
        btnPlay10.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                for (Button button : playButtons) {
                    button.setColor(Color.WHITE);
                }
                btnPlay10.setColor(Color.GREEN);
                playSpeed = 10;
            }
        });

        //50倍播放
        final Button btnPlay50 = game.getButton("images/btnplayx50.png");
        playButtons.add(btnPlay50);
        btnPlay50.setSize(btnPlay50.getWidth() * 0.3f, btnPlay50.getHeight() * 0.3f);
        btnPlay50.setPosition(btnPlay10.getRight() + 15, top.getHeight() / 2, Align.left);
        top.addActor(btnPlay50);
        btnPlay50.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                for (Button button : playButtons) {
                    button.setColor(Color.WHITE);
                }
                btnPlay50.setColor(Color.GREEN);
                playSpeed = 50;
            }
        });


        //时间
        labTime = game.getLabel(format.format(longTime));
        labTime.setPosition(getWidth() / 2, top.getHeight() / 2, Align.center);
        labTime.setAlignment(Align.center);
        labTime.setVisible(false);
        top.addActor(labTime);
        imgHoliday = game.getImageText("放假中");
        imgHoliday.setOrigin(Align.center);//缩放中心为中心点
        imgHoliday.setScale(0.6f);//缩小到0.6倍
        imgHoliday.setPosition(getWidth() / 2, imgTopbg.getHeight() / 2, Align.center);
        top.addActor(imgHoliday);

        //开始新学年
        btnStart = getStartButton("开始新学年");
        btnStart.setSize(150, top.getHeight());
        btnStart.setPosition(btnPlay10.getX(Align.center), btnPlay10.getY(Align.center), Align.center);
        btnStart.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                if (isSemestering == false) {
                    if (arrIntNumbers[0] < 1 || arrIntNumbers[1] < 1 || arrIntNumbers[2] < 1) {
                        game.showDialog(new DialogTips());
                    } else {
                        game.showDialog(new DialogEnrollmentPlan());
                    }

                }
            }
        });

        //排行榜
        btnRank = getStartButton("学生排名");
        btnRank.setSize(120, top.getHeight());
        btnRank.setPosition(top.getWidth() - 10, top.getHeight() / 2, Align.right);
        btnRank.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                rank.setVisible(!rank.isVisible());
                rank.refush(students);
            }
        });

        //跳过本学年
        btnSkip = getStartButton("跳过本学年");
        btnSkip.setSize(120, top.getHeight());
        btnSkip.setPosition(top.getWidth() - 150, top.getHeight() / 2, Align.right);
        btnSkip.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("sound/btnclicked.mp3");
                if (Config.isNewSemestering == true) {
                    Config.isNewSemestering = false;

                    for (int i = 0; i < students.size; i++) {
                        students.get(i).intScore = (students.get(i).intGrade + 1) * 1000 + (int) (Math.random() * 100);
                        //测试
                        //Config.totalNumberQualifiedGraduates = 500;
                    }
                    String strTime = format.format(longTime);
                    String str = strTime.split("-")[0];
                    int year = Integer.parseInt(str);
                    strTime = year + 1 + "-07-01 00:00";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        longTime = simpleDateFormat.parse(strTime).getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    game.showDialog(new DialogSkip());
                }
            }
        });
        top.addActor(btnSkip);
        //学校评级
        Image imgLevel = game.getImageText("学校评级:", font20);
        imgLevel.setPosition(10, top.getHeight() / 2, Align.left);
        top.addActor(imgLevel);

        labLevel = game.getLabel("" + Config.intLevel);
        labLevel.setFontScale(1.2f);
        labLevel.setPosition(imgLevel.getRight() + 10, top.getHeight() / 2, Align.left);
        top.addActor(labLevel);

        //面积
        Image imgArea = game.getImageText("面积:", font20);
        imgArea.setPosition(labLevel.getRight() + 30, top.getHeight() / 2, Align.left);
        top.addActor(imgArea);

        labArea = game.getLabel("" + intArea * intArea);
        labArea.setFontScale(1.2f);
        labArea.setPosition(imgArea.getRight() + 10, top.getHeight() / 2, Align.left);
        top.addActor(labArea);

        //学年
        imgYear = game.getImageText("学年:", font20);
        imgYear.setPosition(labArea.getRight() + 30, top.getHeight() / 2, Align.left);
        top.addActor(imgYear);

        labYear = game.getLabel("" + intYear);
        labYear.setFontScale(1.2f);
        labYear.setPosition(imgYear.getRight() + 10, top.getHeight() / 2, Align.left);
        top.addActor(labYear);


        //学生数
        imgStudentNumber = game.getImageText("学生:", font20);
        imgStudentNumber.setPosition(labYear.getRight() + 30, top.getHeight() / 2, Align.left);
        top.addActor(imgStudentNumber);

        labStudentNumber = game.getLabel("" + 0);
        labStudentNumber.setFontScale(1.2f);
        labStudentNumber.setPosition(imgStudentNumber.getRight() + 10, top.getHeight() / 2, Align.left);
        top.addActor(labStudentNumber);


        //学生列表排行榜
        rank = new Rank();
        rank.setPosition(btnRank.getX() - 30, top.getY() + btnRank.getY() - 10, Align.topLeft);
        rank.setVisible(false);
        addActor(rank);

        //底部容器
        bottomBuiding = addMenuGroup();

        //添加教室按钮，加载各种建筑图片
        Button btnAddClassRoom = addButton("map/classroom.png", "教学楼");
        addButton("map/bedroom.png", "宿舍");
        addButton("map/canteen.png", "食堂");
        addButton("map/library.png", "图书馆");
        addButton("map/floor.png", "水泥路");
        addButton("map/fountainicon.png", "喷泉");
        addButton("map/planter.png", "绿植");
        addButton("map/stadium.png", "体育馆");

        //菜单栏里面的box标记框，外层边框
        box = game.getImage("images/box.png");
        box.setColor(Color.GREEN);
        box.setTouchable(Touchable.disabled);
        box.setSize(btnAddClassRoom.getWidth(), btnAddClassRoom.getHeight());
        box.setPosition(addButtons.first().getX(), addButtons.first().getY());
        bottomBuiding.addActor(box);
        //校园一级标识
        refushLevel(1);
    }

    private Button getStartButton(String text) {
        Button btnStart;
        if (text.equals("开始新学年")) {
            btnStart = game.getButton(150, 40, 10);
        } else {
            btnStart = game.getButton(120, 40, 10);
        }

        btnStart.setColor(Color.valueOf("5ba730ff"));
        //btnStart.setPosition(top.getWidth() - 10, 0, Align.right);
        Image imgStart = game.getImageText(text);
        imgStart.setSize(imgStart.getWidth() * 0.8f, imgStart.getHeight() * 0.8f);
        imgStart.setPosition(btnStart.getWidth() / 2, btnStart.getHeight() / 2, Align.center);
        btnStart.addActor(imgStart);
        top.addActor(btnStart);
        return btnStart;
    }

    //创建一个提示消息，多由于建造时错误提示
    public static void showMessage(Actor point, String msg, Color color) {
        Image imgMsg = game.getImageText(msg, font18);
        imgMsg.setTouchable(Touchable.disabled);
        imgMsg.setColor(color);
        imgMsg.setPosition(point.getX(Align.center), point.getTop(), Align.bottom);
        map.addActor(imgMsg);
        //加一个自动消失的动画
        imgMsg.addAction(Actions.sequence(Actions.alpha(0), Actions.alpha(1, 0.1f)
                , Actions.delay(1), Actions.alpha(0, 3), Actions.removeActor()));
        imgMsg.addAction(Actions.moveBy(0, 100, 4));
    }

    //刷新学校评级
    public void refushLevel() {
        refushLevel(Config.intLevel);
    }

    private void refushLevel(int intLevel) {
        //修改等级
        Config.intLevel = intLevel;
        labLevel.setText("" + intLevel);
        //刷新教室上限,1个评级能建1栋教室
        arrIntNumberMax[0] = intLevel * 1;
        arrNumbers.get(0).setText("[" + arrIntNumbers[0] + "/" + arrIntNumberMax[0] + "]");
        arrNumbers.get(0).setColor(arrIntNumbers[0] >= arrIntNumberMax[0] ? Color.RED : Color.WHITE);
        //刷新宿舍上限，1级可建2栋宿舍
        arrIntNumberMax[1] = intLevel * 1;
        arrNumbers.get(1).setText("[" + arrIntNumbers[1] + "/" + arrIntNumberMax[1] + "]");
        arrNumbers.get(1).setColor(arrIntNumbers[1] >= arrIntNumberMax[1] ? Color.RED : Color.WHITE);
        //刷新食堂上限，食堂只能建设1栋
        arrIntNumberMax[2] = 1;
        arrNumbers.get(2).setText("[" + arrIntNumbers[2] + "/1]");
        arrNumbers.get(2).setColor(arrIntNumbers[2] >= 1 ? Color.RED : Color.WHITE);
        //刷新图书馆上限，图书馆建设数量没有上限
        arrIntNumberMax[3] = 999;
        arrNumbers.get(3).setText("[" + arrIntNumbers[3] + "/999]");
        arrNumbers.get(3).setColor(arrIntNumbers[3] >= 999 ? Color.RED : Color.WHITE);
        //刷新水泥路上限,可建等级的平方*10块路砖
        arrIntNumberMax[4] = (int) (Math.pow(intLevel, 1.5) * 10);
        arrNumbers.get(4).setText("[" + arrIntNumbers[4] + "/" + arrIntNumberMax[4] + "]");
        arrNumbers.get(4).setColor(arrIntNumbers[4] >= arrIntNumberMax[4] ? Color.RED : Color.WHITE);
        //刷新喷泉上限，可建喷泉数量没有上限
        arrIntNumberMax[5] = 999;
        arrNumbers.get(5).setText("[" + arrIntNumbers[5] + "/999]");
        arrNumbers.get(5).setColor(arrIntNumbers[5] >= 999 ? Color.RED : Color.WHITE);
        //刷新绿植上限
        arrIntNumberMax[6] = 999;
        arrNumbers.get(6).setText("[" + arrIntNumbers[6] + "/999]");
        arrNumbers.get(6).setColor(arrIntNumbers[6] >= 999 ? Color.RED : Color.WHITE);
        //刷新体育馆上限,体育馆只能建设1栋
        arrIntNumberMax[7] = 1;
        arrNumbers.get(7).setText("[" + arrIntNumbers[7] + "/1]");
        arrNumbers.get(7).setColor(arrIntNumbers[7] >= 1 ? Color.RED : Color.WHITE);
    }

    //创建底部菜单容器
    private Group addMenuGroup() {
        final Group group = new Group();
        Image imgBottomBg = game.getImage(new Color(0, 0, 0, 0.5f));
        imgBottomBg.setSize(getWidth(), 80);
        group.setSize(imgBottomBg.getWidth(), imgBottomBg.getHeight());
        group.setPosition(getWidth() / 2, 0, Align.bottom);
        group.addActor(imgBottomBg);
        addActor(group);
        return group;
    }

    //创建底部按钮
    private int indexBuiding = 0;
    private Type[] types = {ClassRoom, BedRoom, Canteen, Library, Floor, Fountain, Planter, Stadium};

    /*
     * 创建建筑列表基本按钮
     * */
    private Button addButton(final String iconPath, String text) {
        final Button btnClassroom = game.getButton("images/rectbg.png");
        addButtons.add(btnClassroom);
        btnClassroom.setSize(40, 40);
        btnClassroom.setPosition(10 + indexBuiding * 60, 55, Align.left);
        bottomBuiding.addActor(btnClassroom);
        Image imgClassRoomIcon = game.getImage(iconPath);
        imgClassRoomIcon.setSize(btnClassroom.getWidth() * 0.8f, btnClassroom.getHeight() * 0.8f);
        imgClassRoomIcon.setPosition(btnClassroom.getWidth() / 2, btnClassroom.getHeight() / 2, Align.center);
        btnClassroom.addActor(imgClassRoomIcon);
        Image textClassroom = game.getImageText(text, font20);
        textClassroom.setSize(textClassroom.getWidth() / 2, textClassroom.getHeight() / 2);
        textClassroom.setPosition(btnClassroom.getWidth() / 2, 0, Align.top);
        btnClassroom.addActor(textClassroom);

        Label labNumber = game.getLabel("[0/0]");
        arrNumbers.add(labNumber);
        labNumber.setFontScale(0.8f);
        labNumber.setSize(labNumber.getPrefWidth(), labNumber.getPrefHeight());
        labNumber.setAlignment(Align.center);//文本居中显示
        labNumber.setPosition(btnClassroom.getWidth() / 2, textClassroom.getY(), Align.top);
        btnClassroom.addActor(labNumber);

        final int finalIndex = indexBuiding;
        btnClassroom.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                box.setPosition(btnClassroom.getX(), btnClassroom.getY());
                nextType = types[finalIndex];
                nextId = finalIndex;
                if (nextType == Floor || nextType == Fountain || nextType == Planter) {
                    target.refushSize(boxSize, boxSize);
                } else {
                    target.refushSize(boxSize * 3, boxSize * 3);
                }
            }
        });
        indexBuiding++;
        return btnClassroom;
    }


    //通过滚轮来实现对地图的缩放
    public boolean scrolled(int amount) {
        float scale = map.getScaleX();
        float delay = amount > 0 ? (scaleMin - scale) * 0.2f : (scaleMax - scale) * 0.2f;
        map.scaleBy(delay);
        return false;
    }

    //创造默认地图
    public void createDefateMap() {
        //创建所有单元图对象，用它们的坐标命名，方便查找
        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                GameObject gameObject = new GameObject(Glass);
                gameObject.setPosition(x * boxSize, y * boxSize);
                gameObject.setXYId(x, y);
                map.addActor(gameObject);
                sandys.add(gameObject);
            }
        }

        //校园区域
        int startX = mapSizeX / 2 - intArea / 2;
        int startY = 7;

        for (int y = startY; y < startY + intArea; y++) {
            for (int x = startX; x < startX + intArea; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                gameObject.setId(Sandy);
                if (x == startX || x == startX + intArea - 1 || y == startY || y == startY + intArea - 1) {
                    if (y == startY && (x == startX + intArea / 2 - 1 || x == startX + intArea / 2)) {//开个大门洞
                    } else {
                        gameObject.setId(Wall);
                    }
                }
            }
        }

        //大门
        gate = new GameObject(Gate);
        gate.setColor(1, 1, 1, 0.6f);
        gate.setTouchable(Touchable.disabled);//使大门无法被点击
        gate.setPosition((startX + intArea / 2) * boxSize, startY * boxSize, Align.bottom);
        map.addActor(gate);

        //公路
        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                if (y == 1 || y == 6) {
                    gameObject.setId(RoadSide);
                }
                if (y > 1 && y < 6) {
                    if ((x % 4 == 0 || x % 4 == 1) && y == 3) {
                        gameObject.setId(HighWayLine1);
                    } else if ((x % 4 == 0 || x % 4 == 1) && y == 4) {
                        gameObject.setId(HighWayLine0);
                    } else {
                        gameObject.setId(HighWay);
                    }
                }
            }
        }
//libGDX
        //所有元素刷新一下，部分元素需要重算适合的纹理
        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                gameObject.refush();
            }
        }
    }


    //创建学生
    public void createStudents() {
        Student student = new Student(Type.StudentBoy);
        student.setPosition(mapSizeX / 2 * boxSize, boxSize * 2, Align.bottom);
        map.addActor(student);
        students.add(student);
    }


    //寻路算法
    public boolean[] mapint = new boolean[mapSizeX * mapSizeY];
    public Array<Point> zones = new Array<>();//把可通行的区域保存起来
    public static Astar astar;

    public void createAstar() {//创建寻路用二维地图
        zones.clear();
        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                boolean isNoWay = !gameObject.isCanCross();
                mapint[y * mapSizeX + x] = isNoWay;
            }
        }

        astar = new Astar(false, mapSizeX, mapSizeY) {
            protected boolean isValid(int x, int y) {
                int id = y * mapSizeX + x;
                if (id > mapint.length || id < 0) return true;
                return !mapint[id];
            }
        };
    }

    private Array<Image> tests = new Array<>();

    //刷新A星数组
    public void refushAstar() {
        //if (true) return;
//        for (int i = 0; i < tests.size; i++) {
//            Image red = tests.get(i);
//            red.remove();
//        }
//        tests.clear();

        //Gdx.app.log("aaaaaa", "刷新A星");
        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                boolean isNoWay = !gameObject.isCanCross();
                mapint[y * mapSizeX + x] = isNoWay;
            }
        }
        //遍历用户创建的对象
        for (GameObject gameObject : userObjects) {
            if (gameObject.isCanCross()) {
                if (gameObject.isHouse()) {
                    int xidStart = (int) (gameObject.getX() / boxSize);
                    int yidStart = (int) (gameObject.getY() / boxSize);
                    for (int dy = 0; dy < 3; dy++) {
                        for (int dx = 0; dx < 3; dx++) {
                            int xid = xidStart + dx;
                            int yid = yidStart + dy;
                            mapint[yid * mapSizeX + xid] = false;
                        }
                    }
                } else {
                    int xid = (int) (gameObject.getX() / boxSize);
                    int yid = (int) (gameObject.getY() / boxSize);
                    mapint[yid * mapSizeX + xid] = false;
                }
            }
        }


    }

    //刷新遮盖关系
    public void refushLayerDepth() {
        //学生置顶
        for (Student student : students) {
            student.toFront();
        }
        //大门置顶
        gate.toFront();
        rank.toFront();
    }

    //正常来说，舞台每一帧只调用一次act,但如果我们重写act，让它每一帧调用多次act,就能加速了
    public void act(float delta) {
        for (int i = 0; i < playSpeed; i++) {//act调用次数取决于playSpeed
            super.act(delta);
        }
        refushDepth();
    }


    //刷新层叠关系
    public void refushDepth() {
        if (isSemestering == false) return;
        map.getChildren().sort(new Comparator<Actor>() {
            public int compare(Actor actor, Actor t1) {
                if (actor.getY() > t1.getY()) return -1;
                if (actor.getY() < t1.getY()) return 1;
                else return 0;
            }
        });
        for (GameObject gameObject : userObjects) {
            if (gameObject.getType() == Floor) {
                gameObject.toBack();
            }
            if (gameObject.isHouse()) {
                gameObject.room.toFront();
            }
        }
        for (GameObject sandy : sandys) {
            sandy.toBack();
        }
        rank.toFront();
        studentMenu.toFront();
    }

    //开始新学年
    int stime = 0;

    public void startNewSchoolYear() {
        game.playSound("sound/shangke.mp3");

        isSemestering = true;
        btnStart.setVisible(false);
        stime = 0;

        //把学生重新放入地图中
        for (int i = 0; i < students.size; i++) {
            Student student = students.get(i);
            student.clearActions();
            student.stateMachine.changeState(START);
            student.reLife();
            student.isAi = true;
            student.isStop = false;
            student.setPosition(mapSizeX / 2 * boxSize, boxSize * 2, Align.bottom);
            map.addActor(student);
        }

        for (int i = 0; i < Config.intStudent; i++) {
            createStudents();
        }
        refushLayerDepth();
        rank.refush(students);

        btnStart.clearActions();
        btnStart.addAction(Actions.forever(Actions.sequence(Actions.delay(1, Actions.run(new Runnable() {
            public void run() {
                longTime += 60 * 1000 * 60L;
                stime++;
                String strTime = format.format(longTime);
                labTime.setText(strTime);

                String str = strTime.split(" ")[0];
                if (str.endsWith("07-01")) {//设定次年的7月1日放假
                    longTime += 60L * 1000L * 60L * (24L * 62L + 8L);//62天之后（跳过假期）
                    String strTime2 = format.format(longTime);
                    labTime.setText(strTime2);
                    goHoliday();
                }

                if (stime % 5 == 0) rank.refush(students);//5秒钟刷新一次排名
            }
        })))));
        imgHoliday.setVisible(false);
        labTime.setVisible(true);
        bottomBuiding.setVisible(false);//开学后就不允许建设了，只能在假期建设

        if (studentMenu != null) studentMenu.remove();
        studentMenu = new StudentMenu();//学生菜单
        btnPlay.setColor(Color.GREEN);

        intYear++;//学年增加
        labYear.setText("" + intYear);

        labStudentNumber.setText(students.size);
    }


    //放假啦
    public void goHoliday() {
        //isSemestering = false;
        imgHoliday.setVisible(true);
        labTime.setVisible(false);
        btnStart.clearActions();
        for (int i = 0; i < students.size; i++) {//遍历大地图里的学生
            Student student = students.get(i);
            student.clearActions();
            student.isAi = false;//先把所有ai都暂停
            if (student.getParent() != map) {//如果学生不在大地图中，首先将学生添加到大地图容器中
                if (student.getParent() != null) {
                    Room room = (Room) student.getParent();
                    student.setPosition(room.link.getX(Align.center), room.link.getY(Align.center));
                }
                map.addActor(student);
            }
        }
        //遍历所有建筑内部
        Array<GameObject> houses = getHouses();
        for (int i = 0; i < houses.size; i++) {
            GameObject house = houses.get(i);
            Room room = house.room;
            for (int m = 0; m < room.students.size; m++) {
                Student student = room.students.get(m);
                if (student.seat != null) {
                    room.seats.add(student.seat);
                    student.seat = null;
                }//把座位还给教室
            }
            room.students.clear();
            room.queues.clear();
            room.labQueue.setText("" + room.queues.size);
            room.labNumber.setText("" + room.students.size);
        }

        //然后寻路离开学校
        float maxTime = 0;//算出离开学校需要的最大的时长
        for (int i = 0; i < students.size; i++) {//遍历大地图里的学生
            Student student = students.get(i);
            student.clearActions();
            student.createMovePaths(mapSizeX / 2, 2);
            student.target = null;
            if (student.paths.size > 0) {
                student.stateMachine.changeState(MOVESTEPS);
                student.isAi = true;
                maxTime = Math.max(maxTime, (student.paths.size + 1) * boxSize / student.speed);
            }
        }
        //从场景中移除掉所有学生
        btnStart.addAction(Actions.delay(maxTime, Actions.run(new Runnable() {
            public void run() {
                for (int i = 0; i < students.size; i++) {
                    Student student = students.get(i);
                    student.intGrade++;
                    student.remove();
                    isSemestering = false;
                }
                DialogEndSummary dialogEndSummary = (DialogEndSummary) game.showDialog(new DialogEndSummary(students, intArea));
                dialogEndSummary.isEnd();
                btnStart.setVisible(true);
                playSpeed = 1;
                for (Button button : playButtons) {
                    button.setColor(Color.WHITE);
                }
                btnPlay.setColor(Color.GREEN);
            }
        })));
    }

    //所有玩家建筑的房子
    public Array<GameObject> getHouses() {
        Array<GameObject> houses = new Array<>();
        for (int m = 0; m < userObjects.size; m++) {
            GameObject house = userObjects.get(m);
            if (house.isHouse()) {
                houses.add(house);
            }
        }
        return houses;
    }

    //下一学年重新开始
    public void reStart() {
        //移除所有建筑
        for (int i = 0; i < userObjects.size; i++) {
            GameObject gameObject = userObjects.get(i);
            gameObject.remove();
        }
        userObjects.clear();
        for (int i = 0; i < arrIntNumbers.length; i++) {
            arrIntNumbers[i] = 0;
        }


        labLevel.setText(Config.intLevel);//刷新评级文本
        if (Config.intLevel >= 15) {
            intArea = 10 + (15 - 1) * 3;
        } else {
            intArea = 10 + (Config.intLevel - 1) * 3;//根据学校评级确定学校边长
        }

        labArea.setText(intArea * intArea);//刷新面积文本
        bottomBuiding.setVisible(true);//重新显示底部菜单
        refushLevel();//刷新各种建筑的数量上限

        //改变校园区域大小
        int startX = mapSizeX / 2 - intArea / 2;
        int startY = 7;

        for (int y = startY; y < startY + intArea; y++) {
            for (int x = startX; x < startX + intArea; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                gameObject.setId(Sandy);
                if (x == startX || x == startX + intArea - 1 || y == startY || y == startY + intArea - 1) {
                    if (y == startY && (x == startX + intArea / 2 - 1 || x == startX + intArea / 2)) {//开个大门洞
                    } else {
                        gameObject.setId(Wall);
                    }
                }
            }
        }

        //所有元素刷新一下，部分元素需要重算适合的纹理
        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                GameObject gameObject = map.findActor("obj" + x + "_" + y);
                gameObject.refush();
            }
        }
        //刷新排行榜列表
        rank.refush(students);
    }
}

