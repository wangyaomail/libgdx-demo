package highschool.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import highschool.actors.GameObject;
import highschool.actors.Student;
import highschool.tools.Type;

import static highschool.Config.boxSize;
import static highschool.stages.StageGame.userObjects;

/**
 * 学生状态机
 */

public enum StudentState implements State<Student> {
    //入口
    START {
        public void enter(Student entity) {
            entity.setAngle(MathUtils.PI);
        }

        public void update(Student entity) {
            if (MathUtils.random(60) == 10) {
                entity.stateMachine.changeState(STOP);
            }
        }


        public void exit(Student entity) {
        }

        public boolean onMessage(Student entity, Telegram telegram) {
            return false;
        }
    },
    STOP {//停下来
        private Array<GameObject> array = new Array<>();

        private void findBuiding(Type type) {
            array.clear();
            for (GameObject gameObject : userObjects) {
                if (gameObject.getType() == type) {
                    array.add(gameObject);
                }
            }
        }

        public void enter(Student entity) {
            //Gdx.app.log("aaaaaaaa", "STOP");
        }

        public void update(Student entity) {
            if (MathUtils.random(60) == 1) {
                entity.target = null;
                //首先判断是不是饿了
                if (entity.intHunger <= 0) {
                    //遍历用户建筑，将食堂添加到临时数组
                    findBuiding(Type.Canteen);
                } else if (entity.intEnergy <= 0) {//是不是困了
                    //遍历用户建筑，将宿舍添加到临时数组
                    findBuiding(Type.BedRoom);
                } else {//精力足也不饿，去上课或者去图书馆(随机)
                    if (MathUtils.random(5) == 0) {//有小几率他会去体育馆
                        findBuiding(Type.Stadium);
                    } else {
                        if (MathUtils.randomBoolean()) {//去上课
                            //遍历用户建筑，将教室添加到临时数组
                            findBuiding(Type.ClassRoom);
                        } else {//去图书馆
                            //遍历用户建筑，将图书馆添加到临时数组
                            findBuiding(Type.Library);
                        }
                    }
                }
                if (array.size == 0) {//如果没有找到建筑，回到初始状态
                    // Gdx.app.log("aaaaaaaa", "没有找到建筑，回到初始状态");
                    entity.stateMachine.changeState(START);
                } else {//否则随机一个建筑前往
                    //Gdx.app.log("aaaaaaaa", "开始前往");
                    GameObject classRoom = array.random();
                    entity.createMovePaths((int) (classRoom.getX(Align.center) / boxSize), (int) (classRoom.getY(Align.center) / boxSize));
                    if (entity.paths.size == 0) {//如果没有找到路，回到初始状态
                        // Gdx.app.log("aaaaaaaa", "无路可走");
                        entity.stateMachine.changeState(START);
                    } else {//有路，奥利给
                        //Gdx.app.log("aaaaaaaa", "找到路了");
                        entity.stateMachine.changeState(MOVESTEPS);
                        entity.target = classRoom;
                    }
                }
            }
        }


        public void exit(Student entity) {

        }


        public boolean onMessage(Student entity, Telegram telegram) {
            return false;
        }
    }, MOVESTEPS {//移动循环

        public void enter(final Student entity) {
            //Gdx.app.log("aaaaaaaa", "MoveSteps");
            if (entity.paths.size < 1) {
                if (entity.target == null) {
                    return;
                }
                //走到了终点,嘿嘿
                switch (entity.target.getType()) {
                    case ClassRoom:
                    case Library:
                    case Canteen:
                    case BedRoom:
                    case Stadium:
                        entity.target.room.addStudent(entity);
                        break;
                }
            } else {//还没到终点呢
                entity.moveTarget.set(entity.paths.pop());//从路径中取出最近的点
                float tx = entity.moveTarget.x;
                float ty = entity.moveTarget.y;
                float dist = Vector2.dst(tx, ty, entity.getX(Align.center), entity.getY());//算出距离
                float angle = (float) (Math.atan2(tx - entity.getX(Align.center), ty - entity.getY()));//算出角度
                entity.setAngle(angle);
                //Gdx.app.log("aaaaaaa", "angle=" + angle);
                entity.moveTime = dist / entity.speed;//算出移动需要的时间
                entity.clearActions();
                entity.addAction(Actions.sequence(Actions.moveToAligned(tx, ty, Align.bottom, entity.moveTime), Actions.run(new Runnable() {
                    public void run() {
                        entity.stateMachine.changeState(MOVESTEPS);
                    }
                })));
            }
        }


        public void update(Student entity) {

        }


        public void exit(Student entity) {

        }


        public boolean onMessage(Student entity, Telegram telegram) {
            return false;
        }

    }
}
