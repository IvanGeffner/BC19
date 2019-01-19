package bc19;

public class Utils {

    MyRobot myRobot;
    int[][] robotMap;
    int dim1, dim2;

    static final int[] X = new int[]{0, 0, -1, 0, 1, 1, -1, -1};
    static final int[] Y = new int[]{0, 1, 0, -1, 1, -1, -1, 1};
    static final int[] Z = new int[]{1,1,1,1,2,2,2,2};

    public Utils(MyRobot myRobot){
        this.myRobot = myRobot;
        dim1 = myRobot.map.length;
        dim2 = myRobot.map[0].length;
    }

    void update(){
        robotMap = myRobot.getVisibleRobotMap();
    }

    boolean canBuild (int unit){
        if (myRobot.fuel < Constants.karboCosts[unit]) return false;
        if (myRobot.karbonite < Constants.karboCosts[unit]) return false;
        return true;
    }

    /*
    boolean isOutOfMap(int dx, int dy){
        int newLocx = myRobot.me.x + dx, newLocy = myRobot.me.y + dy;
        if (newLocx < 0 || newLocx >= dim1) return false;
        if (newLocy < 0 || newLocy >= dim2) return false;
        return true;
    }*/

    boolean isInMap(int x, int y){
        if (x < 0 || x >= dim1) return false;
        if (y < 0 || y >= dim2) return false;
        return true;
    }

    boolean isEmptySpace(int dx, int dy){
        int newLocx = myRobot.me.x + dx, newLocy = myRobot.me.y + dy;
        if (newLocx < 0 || newLocx >= dim1) return false;
        if (newLocy < 0 || newLocy >= dim2) return false;
        if (!myRobot.map[newLocx][newLocy]) return false;
        return (robotMap[newLocx][newLocy] <= 0);
    }

    Robot getRobot (int x, int y){
        if (!isInMap(x,y)) return null;
        if (robotMap[x][y] <= 0) return null;
        return myRobot.getRobot(robotMap[x][y]);
    }

}
