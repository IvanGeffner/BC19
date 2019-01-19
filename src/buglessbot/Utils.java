package buglessbot;
import btcutils.*;

public class Utils {

    MyRobot myRobot;
    int[][] robotMap;
    int dimY, dimX;

    public Utils(MyRobot myRobot){
        this.myRobot = myRobot;
        dimY = myRobot.map.length;
        dimX = myRobot.map[0].length;
    }

    void update(){
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
        return isEmptySpaceAbsolute(myRobot.me.x + dx, myRobot.me.y + dy);
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

}
