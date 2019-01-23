package seventhbot;
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
        broadcast.broadcastClosestProphet();
        broadcast.broadcast();
        return act;
    }

    public Action getAction(){
        DefenseMechanismAction dma = defenseMechanism.defenseAction(true);
        if (dma != null){
            ProphetLoc loc = getBestProphetLoc(dma.dir, Constants.INF);
            if (loc != null){
                broadcast.sendTarget(loc.target, broadcast.PROPHET_AGGRO, Constants.Steplength[dma.dir]);
            }
            return myRobot.buildUnit(dma.type, Constants.X[dma.dir], Constants.Y[dma.dir]);
        }
        if (defenseMechanism.buildUnitRich()){
            ProphetLoc loc = getBestProphetLoc(null, Constants.MAX_DIST_CHURCH_TROOPS);
            if (loc != null){
                broadcast.sendTarget(loc.target, broadcast.PROPHET_AGGRO, Constants.Steplength[loc.dirInitial]);
                return myRobot.buildUnit(Constants.PROPHET, Constants.X[loc.dirInitial], Constants.Y[loc.dirInitial]);
            }
        }
        return null;
    }

    ProphetLoc getBestProphetLoc(Integer dir, int maxDist){
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        int myX = myRobot.me.x, myY = myRobot.me.y;
        if (dir != null) {
            myX += Constants.X[dir];
            myY += Constants.Y[dir];
        }
        distM[myX][myY] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if (lastDist > 1 && (last.x + last.y)%2 == Constants.MOD2 && !myRobot.karboniteMap[last.y][last.x] && !myRobot.fuelMap[last.y][last.x] && utils.robotMap[last.y][last.x] == 0){
                return new ProphetLoc(dirs[last.x][last.y], last);
            }
            int limit = Constants.rad2Index;
            if (lastDist >1) limit = Constants.rad4Index;
            for (int i = 0; i < limit; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                if (utils.distance(myRobot.me.x, myRobot.me.y, newX, newY) > maxDist) continue;
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

    class ProphetLoc{
        int dirInitial; Location target;

        public ProphetLoc(int dirInitial, Location target){
            this.dirInitial = dirInitial;
            this.target = target;
        }
    }

}
