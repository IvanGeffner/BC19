package fifthbot;
import btcutils.Action;

import java.util.LinkedList;
import java.util.Queue;

public class Church extends Unit {

    DefenseMechanism defenseMechanism;
    Utils utils;
    Broadcast broadcast;
    int offset = -1;


    public Church(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        defenseMechanism = new DefenseMechanism(myRobot, utils);
        broadcast = new Broadcast(myRobot, utils);
    }

    @Override
    public Action turn(){
        utils.update();
        Action act = getAction();
        broadcast.broadcast();
        return act;
    }

    public Action getAction(){
        Integer dir = defenseMechanism.defenseAction();
        if (dir == null) dir = defenseMechanism.buildUnitRich();
        if(dir != null){
            Location loc = getBestProphetLoc(dir);
            if (loc != null){
                broadcast.sendTarget(loc, broadcast.PROPHET_AGGRO, Constants.Steplength[dir]);
            }
            return myRobot.buildUnit(Constants.PROPHET, Constants.X[dir], Constants.Y[dir]);
        }
        return null;
    }

    Location getBestProphetLoc(int dir){
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        int myX = myRobot.me.x + Constants.X[dir], myY = myRobot.me.y + Constants.Y[dir];
        distM[myX][myY] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if ((last.x + last.y)%2 == Constants.MOD2 && !myRobot.karboniteMap[last.y][last.x] && !myRobot.fuelMap[last.y][last.x] && utils.robotMap[last.y][last.x] == 0){
                return last;
            }
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY) && utils.robotMap[newY][newX] == 0){
                    if (distM[newX][newY] == 0){
                        queue.add(new Location(newX, newY));
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
        return null;
    }

}
