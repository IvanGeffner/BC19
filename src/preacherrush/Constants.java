package preacherrush;

public class Constants {

    static final int[] X = new int[] {1, 0, -1, 0, 1, 1, -1, -1, 2, 0, -2, 0, 2, 2, -2, -2, 1, 1, -1, -1, 2, 2, -2, -2, 3, -3, 0, 0};
    static final int[] Y = new int[] {0, 1, 0, -1, 1, -1, -1, 1, 0, 2, 0, -2, 1, -1, 1, -1, 2, -2, 2, -2, -2, 2, -2, 2, 0, 0, 3, -3};
    static final int[] Steplength = new int[] {1,1,1,1,2,2,2,2,4,4,4,4,5,5,5,5,5,5,5,5,8,8,8,8,9,9,9,9};

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
    final static int[] visionRange = new int[]{100, 100, 100, 49, 64, 16};
    final static int[] dangerRange = new int[]{64,0,0,16,64,26};
    final static int[] minRange = new int[]{0,0,0,0,16,0};
    final static int[] health = new int[]{200, 100, 10, 40, 20, 60};

    final static int RED = 0;
    final static int BLUE = 1;

    final static int OBJ_CASTLE = 0;
    final static int OBJ_FUEL= 1;
    final static int OBJ_KARBO = 2;


    final static int MAX_FUEL_PILGRIM = 100;
    final static int MAX_KARBO_PILGRIM = 10;
    final static int MAX_KARBO_PILGRIM_RICH = 20;
    final static int KARBO_PILGRIM_RICH = 110;

    final static int MAX_ID = 4100;

    final static int OCCUPIED = 2;
    final static int PARTIALLLY_OCCUPIED = 1;
    final static int FREE = 0;

    final static int MAX_TURNS_REPORTING = 5;

    final static int FUEL_MINING_RATE = 10;
    final static int MAX_FUEL_LOST = 30;

    final static int SAFETY_FUEL = 200;
    final static int SAFETY_KARBO = 50;

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

    final static int PRIORITY_FUEL = 1;
    final static int PRIORITY_KARBO = 3;

    final static int MAX_DIST_TROOPS = 5;
    final static int MAX_DIST_CHURCH_TROOPS = 8;
    final static int MIN_FUEL_ATTACK_BROADCAST = 1000;
    final static int ATTACK_BROADCAST = 9;

    final static int MAX_ATTACK_MEMORY = 6;

    final static int MAX_STILL_TURNS_PREACHER = 8;

    final static int BARE_MINIMUM_KARBO = 30;
    final static int BARE_MINIMUM_FUEL = 100;

    final static int CLOSEST_PROPHET_RANGE = 16;
    final static int MIN_RANGE_REPEATED = 5;
    final static int MAX_RANGE_PROPHET = 100;
    final static int MAX_RANGERS = 10;

    final static int MIN_TURN_CASTLES_ENEMY = 120;
    final static int BUILD_FRENZY = 950;

    final static int SAFE_DIFFERENCE = 2;
    final static int MIN_TROOPS = 5;

    final static double MAX_COS = -0.05;

    final static int MAX_DIST_SYMMETRIC = 100;

    final static int MAX_MAGES = 3;

}
