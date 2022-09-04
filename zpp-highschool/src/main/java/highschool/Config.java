package highschool;

public class Config {
    public static float boxSize = 32;//单元方块尺寸
    public static int mapSizeX = 100;//地图尺寸（单元方块个数）
    public static int mapSizeY = 60;//地图尺寸（单元方块个数）
    public static float scaleMax = 3;//地图放大的最大系数
    public static float scaleMin = 0.5f;//地图缩小的最小系数
    public static String schoolName = "中原工学院";
    public static float hourTime = 1f;//设定游戏中的1小时等于现实中的1秒
    public static int intLevel = 1;//学校评级
    public static int intStudent = 0;//招收的新生数量
    public static int ineligibleSchoolYear = 0;//连续不合格的年数
    public static int totalNumberQualifiedGraduates = 0;//合格的毕业生总数(达到一定数量，游戏胜利)
    public static int targetTotalNumberQualifiedGraduates = 500;//超过500名合格毕业生，游戏就胜利了
    public static int intSemesterCredit = 1000;//一学年的达标学分(一个学年需要达到的学分分数)
    public static boolean isNewSemestering = false;//是否点击开始新学年


    public static void reStart() {
        intLevel = 1;
        intStudent = 0;
        ineligibleSchoolYear = 0;
    }
}
