package highschool.tools;

import com.badlogic.gdx.graphics.Color;

/**
 * 字体处理类
 */
public class FreePaint {
    private int textSize = 30;// 字号
    private Color color = Color.WHITE;// 颜色
    private boolean isFakeBoldText = false;// 是否粗体
    private boolean isUnderlineText = false;// 是否下划线
    private boolean isStrikeThruText = false;// 是否删除线
    private Color strokeColor = null;// 描边颜色
    private int strokeWidth = 3;// 描边宽度
    private String ttfName = "";//加载ttf文件名称

    public String getName() {
        StringBuffer name = new StringBuffer();
        name.append(ttfName).append("_").append(textSize).append("_").append(color.toIntBits())
                .append("_").append(booleanToInt(isFakeBoldText)).append("_")
                .append(booleanToInt(isUnderlineText));
        if (strokeColor != null) {
            name.append("_").append(strokeColor.toIntBits()).append("_").append(strokeWidth);
        }
        //把所有有关字体设置的信息通过字符串（xx_xx_xx_xx_xx）的形式传输出去。
        return name.toString();
    }

    private int booleanToInt(boolean b) {
        return b == true ? 0 : 1;
    }

    public FreePaint() {
    }

    public FreePaint(String ttfName, int textSize, Color color, Color stroke, int strokeWidth,
                     boolean bold, boolean line, boolean thru) {
        this.ttfName = ttfName;
        this.textSize = textSize;
        this.color = color;
        this.strokeColor = stroke;
        this.strokeWidth = strokeWidth;
        this.isFakeBoldText = bold;
        this.isUnderlineText = line;
        this.isStrikeThruText = thru;
    }

    public FreePaint(String ttfName) {
        this.ttfName = ttfName;
    }

    public FreePaint(String ttfName, int size) {
        this.ttfName = ttfName;
        this.textSize = size;
    }

    public FreePaint(String ttfName, int size, Color color) {
        this.ttfName = ttfName;
        this.textSize = size;
        this.color = color;
    }

    public FreePaint(String ttfName, Color color) {
        this.ttfName = ttfName;
        this.color = color;
    }

    public FreePaint(int size) {
        this.textSize = size;
    }

    public FreePaint(Color color) {
        this.color = color;
    }

    public FreePaint(int size, Color color) {
        this.textSize = size;
        this.color = color;
    }

    /*
     *获取字体大小
     */
    public int getTextSize() {
        return textSize;
    }

    /*
     *设置字体大小
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /*
     *获取字体颜色
     */
    public Color getColor() {
        return color;
    }

    /*
    设置字体颜色
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /*
    获得字体加粗信息
     */
    public boolean getFakeBoldText() {
        return isFakeBoldText;
    }

    /*设置字体加粗信息*/
    public void setFakeBoldText(boolean isFakeBoldText) {
        this.isFakeBoldText = isFakeBoldText;
    }

    /*获取字体下划线信息*/
    public boolean getUnderlineText() {
        return isUnderlineText;
    }

    /*设置下划线信息*/
    public void setUnderlineText(boolean isUnderlineText) {
        this.isUnderlineText = isUnderlineText;
    }

    /*是否含有删除线 */
    public boolean getStrikeThruText() {
        return isStrikeThruText;
    }

    /*设置删除线*/
    public void setStrikeThruText(boolean isStrikeThruText) {
        this.isStrikeThruText = isStrikeThruText;
    }

    /*获取字体描边颜色*/
    public Color getStrokeColor() {
        return strokeColor;
    }

    /*设置字体描边颜色*/
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /*获取字体描边宽度*/
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /*设置字体描边宽度*/
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /*设置ttf字体名称*/
    public void setTTFName(String ttfName) {
        this.ttfName = ttfName;
    }

    /*获取ttf字体名称*/
    public String getTTFName() {
        return ttfName;
    }

}
