package highschool;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import highschool.stages.StageHead;
import highschool.stages.StageTop;
import highschool.tools.FreePaint;
import highschool.tools.VListener;

import java.util.HashMap;

/**
 * 游戏核心类
 */
public class Game extends ApplicationAdapter {
    public static Game game;//定义一个静态实例方便调用
    public static int WIDTH = 1136, HEIGHT = 640;//参照全屏尺寸
    public Stage stage, stageTop;//当前舞台,顶层舞台
    public Group dialog;//当前对话框
    public AssetManager assets;// 资源管理器
    public static VListener listener;
    private FreePaint defPaint = null;//字体设置
    //使用Inputmultiplexer来处理多个inputprocessor的情况。InputMultiplexer会将事件传给第一个添加的inputprocessor的相应的方法来处理，
    // 如果该方法返回false,则会调用第二个Inputprocessor中相应的方法来进行处理。
    private InputMultiplexer multiplexer;// 触控
    private final HashMap<String, Object> userDatas = new HashMap<>();// 用于数据中转

    /**
     * 构造
     */
    public Game(VListener listener) {
        game = this;
        this.listener = listener;
        defPaint = new FreePaint();
        assets = new AssetManager();// 资源管理
        multiplexer = new InputMultiplexer();
    }

    /**
     * 设置需要中转的数据
     */
    public void setUserData(String key, Object userData) {
        userDatas.put(key, userData);
    }

    /**
     * 获取中转数据
     */
    public <T> T getUserData(String key) {
        return (T) userDatas.get(key);
    }

    /**
     * 当游戏启动时执行
     */
    public void create() {
        Gdx.input.setInputProcessor(multiplexer);
        //用于后面存放顶层舞台
        stageTop = new StageTop();
        multiplexer.addProcessor(stageTop);
        setStage(new StageHead());//创建封面舞台做为当前舞台
    }

    /**
     * 设置当前显示的舞台
     *
     * @param newStage
     */
    public void setStage(Stage newStage) {
        if (stage != null) {
            multiplexer.removeProcessor(stage);//将老的stage监听移除
            stage.dispose();//销毁
        }
        stage = newStage;
        multiplexer.addProcessor(stage);//对新的stage进行触控监听
    }

    /**
     * 显示对话框
     *
     * @param dialog
     * @return
     */
    private Image mask;

    public Group showDialog(Group dialog) {
        stageTop.getRoot().clear();//将顶层舞台所有内容移除掉
        // 禁止底层stage响应
        if (stage != null) {
            //关闭所有触点事件产生
            stage.cancelTouchFocus();
            //Touchable.disabled 演员或任何孩子都不会收到触摸输入事件。
            stage.getRoot().setTouchable(Touchable.disabled);
        }
        if (mask == null) {
            mask = getImage(new Color(0, 0, 0, 0.5f));
            mask.setSize(WIDTH, HEIGHT);
            mask.setPosition(WIDTH / 2, HEIGHT / 2, Align.center);
        }
        stageTop.addActor(mask);
        dialog.setPosition(stageTop.getWidth() / 2, stageTop.getHeight() / 2, Align.center);
        stageTop.addActor(dialog);
        return dialog;
    }

    //移除对话框
    public void removeDialog(final Group dialog) {
        mask.remove();
        dialog.remove();
        if (stage != null) stage.getRoot().setTouchable(Touchable.enabled);//恢复响应
    }

    /**
     * 循环执行
     */
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);//清理绘制
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);//清理绘制
        if (stage != null) {
            stage.act();//舞台逻辑循环
            stage.draw();//舞台绘制循环
            stageTop.act();//舞台逻辑循环
            stageTop.draw();//舞台绘制循环
            if (soundRuns.size > 0) {//从音效池里拖一个音效出来播放,该构造避免同一帧播放过多音效导致播放失败
                soundRuns.removeIndex(0).run();
            }
        }
    }

    /**
     * 返回一个Image控件
     *
     * @param path 资源路径
     * @return
     */
    public Image getImage(String path) {
        //TextureRegionDrawable主要用于图片裁剪，可拉伸，适合纹理区域。
        TextureRegionDrawable tex = new TextureRegionDrawable(getTextureRegion(path));
        return new Image(tex);
    }

    /**
     * 返回一个纯色像素点Image空间
     *
     * @param color
     * @return
     */
    public Image getImage(Color color) {
        Image image = new Image(game.getPointTexture());
        image.setColor(color);
        return image;
    }


    /**
     * 返回一个Button控件
     * 主要是把资源里面的图片转化成按钮的形式
     *
     * @param path 资源路径
     * @return
     */
    public Button getButton(String path) {
        final Button button = new Button(getDrawable(path));
        button.addListener(new InputListener() {
            private Color pref;

            //实现按钮弹起与按下的状态转换
            public boolean touchDown(InputEvent event, float px, float py, int pointer, int but) {
                if (pref == null) pref = button.getColor().cpy();
                //按钮被按下是按钮的颜色加深
                button.setColor(Color.DARK_GRAY);
                return true;
            }

            public void touchUp(InputEvent event, float px, float py, int pointer, int but) {
                //按钮被放开后及弹起后颜色恢复
                button.setColor(pref);
            }
        });
        return button;
    }


    /**
     * 创建圆角Button
     */
    public Button getButton(float width, float height, int radius) {
        NinePatch patch = new NinePatch(getCircleRectTexture((int) (width * 2), (int) (height * 2), radius));
        NinePatchDrawable drawable = new NinePatchDrawable(patch);
        final Button button = new Button(drawable);
        button.setSize(width, height);
        button.addListener(new InputListener() {
            private Color pref;

            public boolean touchDown(InputEvent event, float px, float py, int pointer, int but) {
                if (pref == null) pref = button.getColor().cpy();
                button.setColor(Color.DARK_GRAY);
                return true;
            }

            public void touchUp(InputEvent event, float px, float py, int pointer, int but) {
                button.setColor(pref);
            }
        });
        return button;
    }

    /**
     * 返回一个文本按钮
     *
     * @param pathorcolor 图片路径或颜色
     * @param text        文本
     * @return
     */
    public TextButton getTexButton(Object pathorcolor, String text) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getBitmapFont();
        if (pathorcolor instanceof Color) {
            style.up = new TextureRegionDrawable(getColorPointTexture((Color) pathorcolor));
        } else if (pathorcolor instanceof String) {
            style.up = getDrawable((String) pathorcolor);
        }
        return new TextButton(text, style);
    }

    /**
     * 创建关闭按钮
     *
     * @return
     */

    public Button getCloseButton(int size, Color backgroundColor, Color lineColor) {
        final Button button = new Button(new TextureRegionDrawable(new TextureRegion(getCircleColorTexture(size, backgroundColor))));
        button.addListener(new InputListener() {
            private Color pref;

            public boolean touchDown(InputEvent event, float px, float py, int pointer, int but) {
                if (pref == null) pref = button.getColor().cpy();
                button.setColor(Color.DARK_GRAY);
                return true;
            }

            public void touchUp(InputEvent event, float px, float py, int pointer, int but) {
                button.setColor(pref);
            }
        });
        button.setSize(size, size);
        Image line_x = getImage(lineColor);
        line_x.setSize(size * 0.7f, 6);
        line_x.setOrigin(Align.center);
        line_x.setPosition(size * 0.5f, size * 0.5f, Align.center);
        line_x.setName("line_x");
        button.addActor(line_x);
        line_x.setRotation(45);

        Image line_Y = getImage(lineColor);
        line_Y.setSize(6, size * 0.7f);
        line_Y.setOrigin(Align.center);
        line_Y.setPosition(size * 0.5f, size * 0.5f, Align.center);
        line_Y.setName("line_y");
        button.addActor(line_Y);
        line_Y.setRotation(45);

        return button;
    }

    public Button getCloseButton() {
        return getCloseButton(Math.max(WIDTH, HEIGHT) / 20, Color.valueOf("d70015"), Color.WHITE);
    }

    /**
     * 返回一个Label控件,标签用于显示文本
     *
     * @param text
     * @return
     */
    public Label getLabel(String text) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFont();
        Label label = new Label(text, style);
        return label;
    }

    /**
     * 创建ScrollPane,用于学生列表
     */
    public ScrollPane getScrollPane(Actor actor, Color bg) {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        style.background = getRectColorDrawable(2, 2, bg);
        return getScrollPane(actor, style);
    }

    /**
     * 创建ScrollPane
     */
    public ScrollPane getScrollPane(Actor actor, String background) {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        style.background = getDrawable(background);
        return getScrollPane(actor, style);
    }

    /**
     * 创建ScrollPane
     */
    public ScrollPane getScrollPane(Actor actor, ScrollPane.ScrollPaneStyle style) {
        return new ScrollPane(actor, style);
    }

    /**
     * 返回BitmapFont
     *
     * @return
     */
    private BitmapFont bitmapFont;

    public BitmapFont getBitmapFont() {
        if (bitmapFont == null) bitmapFont = new BitmapFont();
        return bitmapFont;
    }

    /**
     * 获取文本纹理
     */
    private HashMap<String, TextureRegion> texts = new HashMap<>();//用一个hash表保存起来，避免重复创建相同的文字纹理

    public TextureRegion getTextureText(String txt, FreePaint paint) {
        if (paint == null) paint = defPaint;
        if (texts.containsKey(paint.getName() + "_" + txt)) {
            return texts.get(paint.getName() + "_" + txt);
        }
        //通过实现类的对象，可以调用接口中的默认方法，如果实现类重写了接口中的默认方法，调用时，仍然调用的是重写以后的方法；
        Pixmap pix = listener.getFontPixmap(txt, paint);
        Texture tex = new Texture(pix);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion textureRegion = new TextureRegion(tex);
        texts.put(paint.getName() + "_" + txt, textureRegion);
        return textureRegion;
    }

    public TextureRegion getTextureText(String txt) {
        return getTextureText(txt, null);
    }

    /**
     * 获取文本Image
     */

    public Image getImageText(String txt, FreePaint paint) {
        return new Image(getTextureText(txt, paint == null ? defPaint : paint));
        //paint == null defpaint : paint属于三目运算符，通过比较paint == null 的条件来寻则传入的值是defPaint还是paint，如果paint == null为true则传defPaint反之传paint
        //可以理解为条件 ? 结果1 : 结果2 里面的？号是格式要求。也可以理解为条件是否成立，条件成立为结果1，否则为结果2。
    }

    /**
     * 获取文本Image
     */

    public Image getImageText(String txt) {
        return getImageText(txt, null);
    }

    /**
     * 从资源管理器中返回纹理
     *
     * @param path 资源路径
     * @return
     */
    public TextureRegion getTextureRegion(String path) {
        if (path == null || path.equals("")) return getPointTexture();//如果缺省路径就返回一个白色像素点纹理
        Texture tex;
        if (assets.isLoaded(path, Texture.class) == true) {//如果资源管理器里面有资源，直接从中取出返回
            tex = assets.get(path, Texture.class);
        } else {//否则用资源管理器去加载资源然后再返回
            assets.load(path, Texture.class);
            assets.finishLoading();
            tex = assets.get(path, Texture.class);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);//纹理消除锯齿
        }
        return new TextureRegion(tex);
    }

    /**
     * 获取像素点纹理
     */
    public TextureRegion getPointTexture() {
        return getColorPointTexture(Color.WHITE);
    }

    /**
     * 获取颜色像素点
     */
    private final HashMap<String, TextureRegion> textures = new HashMap<String, TextureRegion>();// 保存new出来得资源或者网络资源

    public TextureRegion getColorPointTexture(Color color) {
        TextureRegion colorPoint = textures.get("color" + color.toString());
        if (colorPoint == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fill();
            colorPoint = new TextureRegion(new Texture(pixmap));
            colorPoint.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures.put("color" + color.toString(), colorPoint);
            colorPoint.getTexture().setAssetManager(assets);
            pixmap.dispose();
        }
        return colorPoint;
    }

    //创建圆角按钮
    public TextureRegion getCircleRectTexture(int width, int height, int radius) {
        TextureRegion colorRect = textures.get("colorCircleRect" + width + "_" + height + "_" + radius);
        if (colorRect == null) {
            colorRect = new TextureRegion(new Texture(getCircleRectPixmap(width, height, radius, Color.WHITE)));
            colorRect.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures.put("colorCircleRect" + width + "_" + height + "_" + radius, colorRect);
        }
        return colorRect;
    }

    //创建圆角按钮
    public Pixmap getCircleRectPixmap(int width, int height, int radius, Color color) {
        Pixmap pixmap = new Pixmap(width + radius * 2 + 1, height + radius * 2 + 1, Pixmap.Format.RGBA8888);
        pixmap.setFilter(Pixmap.Filter.BiLinear);
        // 绘制矩形
        pixmap.setColor(color);
        pixmap.fillRectangle(0, radius, pixmap.getWidth(), height);
        pixmap.fillRectangle(radius, 0, width, pixmap.getHeight());
        // 绘制4个圆角
        pixmap.fillCircle(radius, radius, radius);
        pixmap.fillCircle(width + radius, radius, radius);
        pixmap.fillCircle(width + radius, height + radius, radius);
        pixmap.fillCircle(radius, height + radius, radius);
        return pixmap;
    }

    public TextureRegion getCircleColorTexture(int radius, Color color) {
        TextureRegion circle = textures.get("circle" + radius + color.toString());
        if (circle == null) {
            Pixmap pixmap = new Pixmap(radius * 2 + 2, radius * 2 + 2,
                    Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fillCircle(radius + 1, radius + 1, radius);
            Texture texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            circle = new TextureRegion(texture);
            textures.put("circle" + radius + color.toString(), circle);
            texture.setAssetManager(assets);
            pixmap.dispose();
        }
        return circle;
    }

    /**
     * 获取drawable
     *
     * @param path 资源路径
     * @return
     */
    public TextureRegionDrawable getDrawable(String path) {
        return new TextureRegionDrawable(path == null ? getPointTexture() : getTextureRegion(path));
    }

    public TextureRegionDrawable getRectColorDrawable(float width, float height, Color color) {
        TextureRegion tex = new TextureRegion(getColorPointTexture(color));
        tex.setRegion(0, 0, width, height);
        return new TextureRegionDrawable(tex);
    }

    /**
     * Drawable
     */
    public TextureRegionDrawable getRectDrawable(float width, float height) {
        TextureRegion tex = new TextureRegion(getPointTexture());
        tex.setRegion(0, 0, width, height);
        return new TextureRegionDrawable(tex);
    }

    /**
     * 缩放动画
     *
     * @param actor
     * @param runnable
     */
    public void addScaleAnimation(Actor actor, Runnable runnable) {
        actor.clearActions();//清除按钮上原本的动画
        if (actor instanceof Button) {//如果是按钮类型
            ((Button) actor).setTransform(true);//开启按钮类型的形变能力
        }
        actor.setOrigin(Align.center);//设置缩放中心
        if (runnable != null)
            actor.addAction(Actions.sequence(Actions.scaleTo(1.1f, 1.1f, 0.1f), Actions.scaleTo(1, 1, 0.1f), Actions.run(runnable)));//缩放Actions
        else
            actor.addAction(Actions.sequence(Actions.scaleTo(1.1f, 1.1f, 0.1f), Actions.scaleTo(1, 1, 0.1f)));//缩放Actions
    }

    /**
     * 播放音效
     */
    private Array<Runnable> soundRuns = new Array<>();

    public void playSound(final String musicName) {
        if (!assets.isLoaded(musicName, Sound.class)) {
            assets.load(musicName, Sound.class);
            assets.finishLoading();
        }
        soundRuns.add(new Runnable() {
            public void run() {
                Sound sound = assets.get(musicName, Sound.class);
                while (true) {
                    long id = sound.play();
                    if (id != -1) break;
                }
            }
        });
    }

    /**
     * 播放音乐
     */
    private Music music;// 背景音乐实例

    public void playMusic(String soundName) {
        if (music != null)
            music.pause();//暂停播放
        if (assets.isLoaded(soundName, Music.class) == true) {
            music = assets.get(soundName, Music.class);
            music.setLooping(true);
            music.play();
        } else {
            assets.load(soundName, Music.class);
            assets.finishLoading();
            music = assets.get(soundName, Music.class);
            music.setLooping(true);
            music.play();
        }
    }

    /**
     * 停止音乐
     */
    public void stopMusic() {
        if (music != null) {
            music.stop();
            music = null;
        }
    }

    Rectangle rect1 = new Rectangle(), rect2 = new Rectangle();

    /**
     * r1是否和r2相交
     */
    public boolean isOverlaps(Actor r1, Actor r2) {
        rect1.set(r1.getX(), r1.getY(), r1.getWidth(), r1.getHeight());
        rect2.set(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight());
        return rect1.overlaps(rect2);
    }
}
