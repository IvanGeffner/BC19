package bc19;
public class Constants {

    static final int[] X = new int[] {1, 0, -1, 0, 1, 1, -1, -1, 2, 0, -2, 0};
    static final int[] Y = new int[] {0, 1, 0, -1, 1, -1, -1, 1, 0, 2, 0, -2};
    static final int[] Steplength = new int[] {1,1,1,1,2,2,2,2,4,4,4,4};

    static final int maxMapSize = 64;
    static final int rad4Index = 12;
    static final int rad2Index = 8;

    final static int CASTLE = 0;
    final static int CHURCH = 1;
    final static int PILGRIM = 2;
    final static int CRUSADER = 3;
    final static int PROPHET = 4;
    final static int PREACHER = 5;

    final static int[] karboCosts = new int[]{0, 50, 10, 20, 25, 30};
    final static int[] fuelCosts = new int[]{0, 200, 50, 50, 50, 50};

    final static int RED = 0;
    final static int BLUE = 1;

    final static int OBJ_CASTLE = 0;
    final static int OBJ_FUEL= 1;
    final static int OBJ_KARBO = 2;

}
