package bc19;

import java.util.LinkedList;
import java.util.Queue;

public class Pilgrim extends Unit {

    Location objective = null;
    Utils utils;
    Action actionToPerform;

    static final int[] Xfast = new int[] {1, 0, -1, 0, 1, 1, -1, -1, 2, 0, -2, 0};
    static final int[] Yfast = new int[] {0, 1, 0, -1, 1, -1, -1, 1, 0, 2, 0, -2};
    static final int[] fuelCost = new int[] {1,1,1,1,2,2,2,2,4,4,4,4};

    public Pilgrim(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
    }

    @Override
    public Action turn(){
        utils.update();
        actionToPerform = null;
        if (objective == null) findObjective();
        if (objective != null) goToObjective();
        return actionToPerform;
    }

    void findObjective(){
        for (int i = 0; i < 8; ++i){
            int newX =  utils.myLocation.x + utils.X[i];
            int newY = utils.myLocation.y + Utils.Y[i];
            Robot robot = utils.getRobot(newX, newY);
            if (robot != null){
                if (robot.unit == Constants.CASTLE && myRobot.isRadioing(robot)){
                    int message = robot.signal;
                    int xObj = message/CastleUtils.prodConstant;
                    int yObj = message%CastleUtils.prodConstant;
                    objective = new Location(xObj,yObj);
                }
            }
        }
    }

    void goToObjective(){
        int myX = utils.myLocation.x, myY =utils.myLocation.y;
        if (myX == objective.x && myY == objective.y) return;
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        distM[myX][myY] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location (myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if (last.x == objective.x && last.y == objective.y){
                actionToPerform = myRobot.move(Xfast[dirs[last.x][last.y]], Yfast[dirs[last.x][last.y]]);
                break;
            }
            for (int i = 0; i < Xfast.length; ++i){
                int newX = last.x + Xfast[i], newY = last.y + Yfast[i];
                int newFuel = lastFuel + fuelCost[i];
                if (utils.isInMap(newX, newY) && utils.isPassable (newX, newY) && (lastDist > 1 || utils.isEmptySpaceAbsolute(newX, newY))){
                    if (distM[newX][newY] == 0){
                        queue.add(new Location (newX, newY));
                        distM[newX][newY] = lastDist + 1;
                        fuel[newX][newY] = newFuel;
                        if (lastDist == 1) dirs[newX][newY] = i;
                        else dirs[newX][newY] = dirs[last.x][last.y];
                    }
                    if (distM[newX][newY] == lastDist + 1){
                        if (fuel[newX][newY] > newFuel){
                            fuel[newX][newY] = newFuel;
                            if (lastDist == 1) dirs[newX][newY] = i;
                            else dirs[newX][newY] = dirs[last.x][last.y];
                        }
                    }
                }
            }
        }
    }
}
