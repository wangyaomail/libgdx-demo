package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import highschool.Config;
import highschool.tools.FreePaint;
import highschool.tools.Type;

import static highschool.Game.game;

/**
 * 地图对象
 */
public class GameObject extends Actor {
    private TextureRegion bg, title;
    public int xid, yid;

    public int index;//该类楼房的编号
    public Image labIndex;
    private FreePaint paint = new FreePaint(15);

    private String[] texNames = new String[]{"floor", "glass", "sandy", "soil", "brick", "wall1_1", "highway", "highway_line0", "highway_line1", "roadside"
            , "classroom", "bedroom", "canteen", "library", "fountain0", "planter", "gate", "studentgirl0", "stadium"};

    public Room room;//房子内部细节

    public Animation<TextureRegion> animation;

    private Type type;

    public GameObject(Type type) {
        setId(type);
    }

    public GameObject(int id) {
        setId(type);
    }

    //是否为房子
    public boolean isHouse() {
        return type == Type.ClassRoom || type == Type.BedRoom || type == Type.Canteen || type == Type.Library || type == Type.Stadium;
    }

    //是否为玩家建设的建筑
    public boolean isBuiding() {
        return isHouse() || type == Type.Floor || type == Type.Fountain || type == Type.Planter;
    }

    //是否为可通行建筑
    public boolean isCanCross() {
        return isHouse() || type == Type.Floor || type == Type.HighWay || type == Type.HighWayLine0 || type == Type.HighWayLine1 || type == Type.RoadSide;
    }

    //返回地图单元类型
    public Type getType() {
        return type;
    }

    //设置坐标
    public void setXYId(int xid, int yid) {
        this.xid = xid;
        this.yid = yid;
        setName("obj" + xid + "_" + yid);//用坐标命名，方便获取
    }

    public GameObject fideObjectById(int xid, int yid) {
        if (hasParent()) {
            return getParent().findActor("obj" + xid + "_" + yid);
        }
        return null;
    }

    public void setId(Type type) {
        paint.setColor(Color.YELLOW);
        this.type = type;
        bg = game.getTextureRegion("map/" + texNames[type.value()] + ".png");
        setSize(bg.getRegionWidth(), bg.getRegionHeight());
        if (type == Type.Fountain && animation == null) {//如果是喷泉，我们创建一个动画
            bg = game.getTextureRegion("map/" + texNames[Type.Floor.value()] + ".png");
            TextureRegion[] regions = new TextureRegion[5];
            for (int i = 0; i < regions.length; i++) {
                regions[i] = game.getTextureRegion("map/fountain" + i + ".png");
            }
            animation = new Animation<TextureRegion>(0.1f, regions);
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (type == Type.Gate) {
            title = game.getTextureText(Config.schoolName);
            //Gdx.app.log("", "" + title.getRegionWidth() * 0.4f);
        }
        if (isHouse()) {//如果是房子，创建一个房子内部细节
            if (type == Type.ClassRoom || type == Type.BedRoom || type == Type.Library) {
                labIndex = game.getImageText(index + "号楼", paint);//房子编号
            }
            room = new Room(type, this);
            room.setVisible(false);
        }
    }

    //刷新几号楼
    public void refushIndex(int index) {
        this.index = index;
        labIndex.setDrawable(game.getImageText(index + "号楼", paint).getDrawable());
        room.refushName(index + "号楼");
    }

    public void refush() {
        if (type == Type.Wall) {
            GameObject top = findNeighbor(Align.top);
            GameObject bottom = findNeighbor(Align.bottom);
            GameObject left = findNeighbor(Align.left);
            GameObject right = findNeighbor(Align.right);
            if (top != null && top.type == Type.Wall) {//上是墙
                if (bottom != null && bottom.type == Type.Wall) {
                    //下是墙
                    bg = game.getTextureRegion("map/wall0_1.png");
                } else if (right != null && right.type == Type.Wall) {
                    //右是墙
                    bg = game.getTextureRegion("map/wall1_2.png");
                } else if (left != null && left.type == Type.Wall) {
                    //左是墙
                    bg = game.getTextureRegion("map/wall0_2.png");
                }
            } else if (bottom != null && bottom.type == Type.Wall) {//下是墙
                if (right != null && right.type == Type.Wall) {
                    //右是墙
                    bg = game.getTextureRegion("map/wall3_2.png");
                } else if (left != null && left.type == Type.Wall) {
                    //左是墙
                    bg = game.getTextureRegion("map/wall2_2.png");
                }
            } else if (left != null && left.type == Type.Wall) {//左是墙
                if (right != null && right.type == Type.Wall) {
                    //右是墙
                    bg = game.getTextureRegion("map/wall1_1.png");
                }
            }
        }
    }

    //该位置是否能够安放建筑物
    public boolean isCanBuiding() {
        GameObject top = findNeighbor(Align.top);
        GameObject bottom = findNeighbor(Align.bottom);
        GameObject left = findNeighbor(Align.left);
        GameObject right = findNeighbor(Align.right);

        GameObject bottomLeft = findNeighbor(Align.bottomLeft);
        GameObject bottomRight = findNeighbor(Align.bottomRight);
        GameObject topLeft = findNeighbor(Align.topLeft);
        GameObject topRight = findNeighbor(Align.topRight);

        if (top == null || bottom == null || left == null || right == null
                || bottomLeft == null || bottomRight == null || topLeft == null || topRight == null
        ) return false;
        if (type == Type.Sandy && top.type == Type.Sandy && bottom.type == Type.Sandy && left.type == Type.Sandy && right.type == Type.Sandy
                && bottomLeft.type == Type.Sandy && bottomRight.type == Type.Sandy && topLeft.type == Type.Sandy && topRight.type == Type.Sandy) {
            return true;
        }
        return false;
    }

    //返回相邻的图元
    public GameObject findNeighbor(int aglin) {
        Group map = getParent();
        switch (aglin) {
            case Align.top:
                return map.findActor("obj" + xid + "_" + (yid + 1));
            case Align.bottom:
                return map.findActor("obj" + xid + "_" + (yid - 1));
            case Align.left:
                return map.findActor("obj" + (xid - 1) + "_" + yid);
            case Align.right:
                return map.findActor("obj" + (xid + 1) + "_" + yid);
            case Align.bottomLeft:
                return map.findActor("obj" + (xid - 1) + "_" + (yid - 1));
            case Align.bottomRight:
                return map.findActor("obj" + (xid + 1) + "_" + (yid - 1));
            case Align.topLeft:
                return map.findActor("obj" + (xid - 1) + "_" + (yid + 1));
            case Align.topRight:
                return map.findActor("obj" + (xid + 1) + "_" + (yid + 1));

        }
        return null;
    }

    private float stateTime;
    private boolean isFirst = true;

    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (isFirst && getParent() != null && room != null) {
            isFirst = false;
            getParent().addActor(room);
        }
    }

    public void draw(Batch batch, float a) {
        if (type == Type.Gate) {
            batch.setColor(getColor());
            batch.draw(bg, getX(), getY(), getWidth(), getHeight());
            batch.setColor(0, 0, 0, getColor().a);
            float bl = title.getRegionWidth() * 0.4f < 100 ? 1 : title.getRegionWidth() * 0.4f / 100f;
            batch.draw(title, getX(Align.center) - title.getRegionWidth() / 2, getY() + 55, title.getRegionWidth() / 2
                    , title.getRegionHeight() / 2, title.getRegionWidth(), title.getRegionHeight(), 0.4f / bl, 0.4f / bl, 0);
        } else if (type == Type.Fountain) {
            batch.setColor(Color.WHITE);
            batch.draw(bg, getX(), getY(), getWidth(), getHeight());
            batch.draw(animation.getKeyFrame(stateTime), getX(), getY());
        } else {
            batch.setColor(Color.WHITE);
            batch.draw(bg, getX(), getY(), getWidth(), getHeight());
        }
        if (labIndex != null) {
            labIndex.setPosition(getX(Align.center), getY(Align.center), Align.center);
            labIndex.draw(batch, 1);
        }
    }
}
