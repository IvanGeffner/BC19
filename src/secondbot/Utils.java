package secondbot;
import btcutils.*;

public class Utils {

    MyRobot myRobot;
    int[][] robotMap;
    int dimY, dimX;
    Location myLocation;

    static final int[] X = new int[]{1, 0, -1, 0, 1, 1, -1, -1};
    static final int[] Y = new int[]{0, 1, 0, -1, 1, -1, -1, 1};
    static final int[] Z = new int[]{1,1,1,1,2,2,2,2};

    public Utils(MyRobot myRobot){
        this.myRobot = myRobot;
        dimY = myRobot.map.length;
        dimX = myRobot.map[0].length;
    }

    void update(){
        myLocation = new Location(myRobot.me.x, myRobot.me.y);
        robotMap = myRobot.getVisibleRobotMap();
    }

    boolean canBuild (int unit){
        if (myRobot.fuel < Constants.fuelCosts[unit]) return false;
        if (myRobot.karbonite < Constants.karboCosts[unit]) return false;
        return true;
    }

    boolean isInMap(int x, int y){
        if (x < 0 || x >= dimX) return false;
        if (y < 0 || y >= dimY) return false;
        return true;
    }

    boolean isEmptySpace(int dx, int dy){
        return isEmptySpaceAbsolute(myLocation.x + dx, myLocation.y + dy);
    }

    boolean isEmptySpaceAbsolute(int x, int y){
        if (x < 0 || x >= dimX) return false;
        if (y < 0 || y >= dimY) return false;
        if (!myRobot.map[y][x]) return false;
        return (robotMap[y][x] <= 0);
    }

    Robot getRobot (int x, int y){
        if (!isInMap(x,y)) return null;
        if (robotMap[y][x] <= 0) return null;
        return myRobot.getRobot(robotMap[y][x]);
    }

    boolean getKarbonite(int x, int y){
        return myRobot.karboniteMap[y][x];
    }

    boolean getFuel (int x, int y){
        return myRobot.fuelMap[y][x];
    }

    boolean isPassable (int x, int y){
        return myRobot.map[y][x];
    }

}
