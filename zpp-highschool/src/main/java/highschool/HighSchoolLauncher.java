package highschool;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import highschool.tools.FreePaint;
import highschool.tools.VListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.AttributedString;
import java.util.HashMap;

public class HighSchoolLauncher implements VListener {
    public static void main(String[] arg) {
        System.out.println(System.getProperty("java.class.path"));
        //此类允许设置各种配置，例如初始屏幕分辨率，是否使用OpenGL ES 2.0或3.0（当前还处于实验阶段）等。
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //配置游戏宽度
        config.width = (int) (1136 * 1.5f);
        //配置游戏高度
        config.height = (int) (640 * 1.5f);
        //配置游戏名称
        config.title = "高校模拟器";
        //配置游戏图标
        config.addIcon("images/logo.png", Files.FileType.Internal);
        //LwjglApplication为桌面端启动类，它的构造方法包含一个MyGdxGame()类对象，这个MyGdxGame类实际上是实现了游戏逻辑的ApplicationListener接口实例。
        new LwjglApplication(new Game(new HighSchoolLauncher()), config);
    }

    @Override
    public Pixmap getFontPixmap(String txt, FreePaint vpaint) {//我们可以通过com.badlogic.gdx.graphics.Pixmap类来构建简单的像素纹理.
        //Font 类表示字体，用来以可见方式呈现文本。字体提供将字符 序列映射到字形 序列所需要的信息，以便在 Graphics 对象和 Component 对象上呈现字形序列。
        Font font = getFont(vpaint);
        //FontMetrics类定义了一个字体度量对象，该对象封装了在特定屏幕上呈现特定字体的信息。
        FontMetrics fm = metrics.get(vpaint.getName());
        //字体宽度
        int strWidth = fm.stringWidth(txt);
        //字体高度
        int strHeight = fm.getAscent() + fm.getDescent();
        if (strWidth == 0) {
            strWidth = strHeight = vpaint.getTextSize();
        }
        BufferedImage bi = new BufferedImage(strWidth, strHeight, BufferedImage.TYPE_4BYTE_ABGR);
        //该Graphics2D类扩展了Graphics类，以提供对几何，坐标变换，颜色管理和文本布局的更复杂的控制。
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        if (vpaint.getStrokeColor() != null) {
            //字体描边颜色处理
            GlyphVector v = font.createGlyphVector(fm.getFontRenderContext(), txt);
            Shape shape = v.getOutline();
            g.setColor(getColor(vpaint.getColor()));
            g.translate(0, fm.getAscent());
            g.fill(shape);
            g.setStroke(new BasicStroke(vpaint.getStrokeWidth()));
            g.setColor(getColor(vpaint.getStrokeColor()));
            g.draw(shape);
        } else if (vpaint.getUnderlineText() == true) {
            //字体下划线处理
            AttributedString as = new AttributedString(txt);
            as.addAttribute(TextAttribute.FONT, font);
            as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            g.setColor(getColor(vpaint.getColor()));
            g.drawString(as.getIterator(), 0, fm.getAscent());
        } else if (vpaint.getStrikeThruText() == true) {
            //字体删除线处理
            AttributedString as = new AttributedString(txt);
            as.addAttribute(TextAttribute.FONT, font);
            as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            g.setColor(getColor(vpaint.getColor()));
            g.drawString(as.getIterator(), 0, fm.getAscent());
        } else {
            g.setColor(getColor(vpaint.getColor()));
            g.drawString(txt, 0, fm.getAscent());
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pixmap pixmap = new Pixmap(buffer.toByteArray(), 0, buffer.toByteArray().length);
        return pixmap;
    }

    private HashMap<String, Font> fonts = new HashMap<String, Font>();
    private HashMap<String, FontMetrics> metrics = new HashMap<String, FontMetrics>();

    /*
     * 规范字体设置
     * */
    private Font getFont(FreePaint vpaint) {
        boolean isBolo = vpaint.getFakeBoldText()
                || vpaint.getStrokeColor() != null;
        Font font = fonts.get(vpaint.getName());
        //判断字体是否已经包含在fonts里。
        if (font == null) {
            //判断字体TTF引用库是否为空
            if (vpaint.getTTFName().equals("")) {
                //	BOLD 大胆的风格常数(加粗)。PLAIN 平原风格常数（正常）Font.ITALIC(斜体)。
                font = new Font(null, isBolo ? Font.BOLD : Font.PLAIN,
                        vpaint.getTextSize());
            } else {
                try {
                    //加载ttf文件库的文件
                    ByteArrayInputStream in = new ByteArrayInputStream(
                            Gdx.files.internal(vpaint.getTTFName() + (vpaint.getTTFName().endsWith(
                                    ".ttf") ? "" : ".ttf")).readBytes());
                    BufferedInputStream fb = new BufferedInputStream(in);
                    font = Font.createFont(Font.TRUETYPE_FONT, fb).deriveFont(
                            Font.BOLD, vpaint.getTextSize());
                    fb.close();
                    in.close();
                } catch (FontFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //Key是字体配置信息格式（xxx_xxx_xxx）,Values是处理后的字体设置
            fonts.put(vpaint.getName(), font);
            //把字体转成图片格式
            BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = bi.createGraphics();
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            metrics.put(vpaint.getName(), fm);
        }
        return font;
    }

    /*
     *获取字体颜色
     */
    private java.awt.Color getColor(Color libColor) {
        return new java.awt.Color(libColor.r, libColor.g, libColor.b, libColor.a);
    }
}
