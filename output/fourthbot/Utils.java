package bc19;


public class Utils {

    MyRobot myRobot;
    int[][] robotMap;
    Robot[] robotsInVision;
    int dimY, dimX;

    public Utils(MyRobot myRobot){
        this.myRobot = myRobot;
        dimY = myRobot.map.length;
        dimX = myRobot.map[0].length;
    }

    void update(){
        robotMap = myRobot.getVisibleRobotMap();
        robotsInVision = myRobot.getVisibleRobots();
    }

    boolean canBuild (int unit){
        if (myRobot.fuel < Constants.fuelCosts[unit]) return false;
        if (myRobot.karbonite < Constants.karboCosts[unit]) return false;
        return true;
    }

    boolean canSafelyBuild(int unit){
        if (myRobot.fuel < Constants.fuelCosts[unit] + Constants.SAFETY_FUEL) return false;
        if (myRobot.karbonite < Constants.karboCosts[unit] + Constants.SAFETY_KARBO) return false;
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

    int distance(int x1, int y1, int x2, int y2){
        return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
    }

    int distance (Location loc1, Location loc2){
        return distance (loc1.x, loc1.y, loc2.x, loc2.y);
    }

}
