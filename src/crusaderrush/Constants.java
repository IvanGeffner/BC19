package crusaderrush;

public class Constants {

    static final int[] X = new int[] {1, 0, -1, 0, 1, 1, -1, -1, 2, 0, -2, 0, 2, 2, -2, -2, 1, 1, -1, -1, 2, 2, -2, -2, 3, -3, 0, 0};
    static final int[] Y = new int[] {0, 1, 0, -1, 1, -1, -1, 1, 0, 2, 0, -2, 1, -1, 1, -1, 2, -2, 2, -2, -2, 2, -2, 2, 0, 0, 3, -3};
    static final int[] Steplength = new int[] {1,1,1,1,2,2,2,2,4,4,4,4};

    static final int maxMapSize = 64;
    static final int LocationSize = 64*64;
    static final int rad4Index = 12;
    static final int rad2Index = 8;
    static final int rad9Index = 28;

    final static int CASTLE = 0;
    final static int CHURCH = 1;
    final static int PILGRIM = 2;
    final static int CRUSADER = 3;
    final static int PROPHET = 4;
    final static int PREACHER = 5;
    final static int UNITTYPES = 6;

    final static int[] karboCosts = new int[]{0, 50, 10, 15, 25, 30};
    final static int[] fuelCosts = new int[]{0, 200, 50, 50, 50, 50};
    final static int[] attack = new int[]{10,0,0,10,10,20};
    final static int[] range = new int[]{64,0,0,16,64,16};
    final static int[] dangerRange = new int[]{64,0,0,16,64,26};
    final static int[] minRange = new int[]{0,0,0,0,16,0};

    final static int RED = 0;
    final static int BLUE = 1;

    final static int OBJ_CASTLE = 0;
    final static int OBJ_FUEL= 1;
    final static int OBJ_KARBO = 2;


    final static int MAX_FUEL_PILGRIM = 100;
    final static int MAX_KARBO_PILGRIM = 10;
    final static int MAX_KARBO_PILGRIM_POOR = 10;
    final static int POOR_KARBO = 110;

    final static int MAX_ID = 4100;

    final static int OCCUPIED = 2;
    final static int PARTIALLLY_OCCUPIED = 1;
    final static int FREE = 0;

    final static int MAX_TURNS_REPORTING = 5;

    final static int FUEL_MINING_RATE = 10;
    final static int MAX_FUEL_LOST = 30;

    final static int SAFETY_FUEL = 100;
    final static int SAFETY_KARBO = 60;

    final static int SENDING_TURNS = 2;

    final static int MSG_PILGRIM = 0;
    final static int MSG_CHURCH = 1;
    final static int MSG_CASTLE = 2;
    final static int MSG_CASTLE_LATE = 3;
    final static int MSG_TROOP = 2;

    final static int MAX_CASTLES = 3;

    final static int INF = 10000;

    final static int RICH_KARBO = 300;
    final static int RICH_FUEL = 1500;
    final static int SUPER_RICH_KARBO = 500;
    final static int SUPER_RICH_FUEL = 2500;

    final static int MAX_CHURCHES_WAITING = 1;
    final static int MAX_TURN_CASTLE_LOC = 6;

    final static int MIN_TURNS_RICH = 12;

    final static int MOD2 = 0;

    final static int MAX_PROPHET_WAIT = 6;

}
