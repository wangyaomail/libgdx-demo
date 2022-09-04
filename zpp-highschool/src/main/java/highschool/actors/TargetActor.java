package highschool.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

/**
 * 用于高显鼠标选中地图中的演员
 */
public class TargetActor extends Actor {

    public boolean isRight;

    public void drawDebug(ShapeRenderer shapes) {
        drawDebugBounds(shapes);
    }

    protected void drawDebugBounds(ShapeRenderer shapes) {
        if (!getDebug()) return;
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getColor());
        shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        setTouchable(Touchable.disabled);
    }
    //用于判断建筑位置是否规范
    public void setIsRight(boolean isRight) {
        setColor(isRight ? Color.GREEN : Color.RED);
        this.isRight = isRight;
    }
    //刷新尺寸
    public void refushSize(float width, float height) {
        float prefX = getX(Align.center);
        float prefY = getY(Align.center);
        setSize(width, height);
        setPosition(prefX, prefY, Align.center);
    }
}
