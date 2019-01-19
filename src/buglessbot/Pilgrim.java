package buglessbot;
import btcutils.*;

import java.util.LinkedList;
import java.util.Queue;

public class Pilgrim extends Unit {

    Location objective = null;
    Utils utils;
    Action actionToPerform;

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
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX =  myRobot.me.x + Constants.X[i];
            int newY = myRobot.me.y + Constants.Y[i];
            Robot robot = utils.getRobot(newX, newY);
            if (robot != null){
                if (robot.unit == Constants.CASTLE && myRobot.isRadioing(robot)){
                    int message = robot.signal;
                    int xObj = message/Constants.maxMapSize;
                    int yObj = message%Constants.maxMapSize;
                    objective = new Location(xObj,yObj);
                    break;
                }
            }
        }
    }

    void goToObjective(){
        int myX = myRobot.me.x, myY = myRobot.me.y;
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
                actionToPerform = myRobot.move(Constants.X[dirs[last.x][last.y]], Constants.Y[dirs[last.x][last.y]]);
                break;
            }
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY)){
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
