package highschool.tools;

public enum Type {
    //水泥地，草地，沙地，泥土，地砖，墙，高速公路，斑马线，路牙，教室，宿舍，食堂，图书馆，喷泉，植物，大门，男孩，体育馆
    Floor(0),
    Glass(1),
    Sandy(2),
    Soil(3),
    Brick(4),
    Wall(5),
    HighWay(6),
    HighWayLine0(7),
    HighWayLine1(8),
    RoadSide(9),
    ClassRoom(10),
    BedRoom(11),
    Canteen(12),
    Library(13),
    Fountain(14),
    Planter(15),
    Gate(16),
    StudentBoy(17),
    Stadium(18);

    private final int n;

    Type(int n) {
        this.n = n;
    }

    public int value() {
        return this.n;
    }
}
