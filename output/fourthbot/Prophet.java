package bc19;


import java.util.LinkedList;
import java.util.Queue;

public class Prophet extends Unit {

    Micro micro;
    Utils utils;

    Location objective = null;

    int[][] distFromObjective = null;
    int[][] fuelFromObjective = null;

    int mod2 = -1;

    public Prophet(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        micro = new Micro(myRobot, utils, Constants.rad4Index);
    }

    @Override
    public Action turn(){
        utils.update();
        if (mod2 == -1) readMod();
        if (mod2 != -1) generateDistancesFromObjective();
        Action attackAction = getAttackAction();
        if (attackAction != null) return attackAction;
        Integer bestAction = micro.getBestIndex();
        if (bestAction != null && bestAction != micro.maxMovementIndex) return myRobot.move(Constants.X[bestAction], Constants.Y[bestAction]);
        Action act = moveToBestLoc();
        if (act != null) return act;
        return null;
    }

    void generateDistancesFromObjective(){
        fuelFromObjective = new int[utils.dimX][utils.dimY];
        distFromObjective = new int[utils.dimX][utils.dimY];
        distFromObjective[objective.x][objective.y] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(objective.x, objective.y));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuelFromObjective[last.x][last.y];
            int lastDist = distFromObjective[last.x][last.y];
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + 2*(Constants.Steplength[i] + Constants.FUEL_MINING_RATE);
                if (utils.isInMap(newX, newY) && myRobot.map[newY][newX]){
                    if (distFromObjective[newX][newY] == 0){
                        queue.add(new Location(newX, newY));
                        distFromObjective[newX][newY] = lastDist + 1;
                        fuelFromObjective[newX][newY] = newFuel;
                    }
                    if (distFromObjective[newX][newY] == lastDist + 1){
                        if (fuelFromObjective[newX][newY] > newFuel){
                            fuelFromObjective[newX][newY] = newFuel;
                        }
                    }
                }
            }
        }
    }

    Action moveToBestLoc(){
        if (mod2 == -1) return null;
        int myX = myRobot.me.x, myY = myRobot.me.y;
        int bestDist = Constants.INF;
        if ((myX+myY)%2 == mod2) bestDist = distFromObjective[myX][myY];
        int bestDir = -1;
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        distM[myX][myY] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if ((last.x + last.y)%2 == mod2 && !myRobot.karboniteMap[last.y][last.x] && !myRobot.fuelMap[last.y][last.x]){
                int d = distFromObjective[last.x][last.y];
                if (d < bestDist){
                    bestDist = d;
                    bestDir = dirs[last.x][last.y];
                }
            }
            for (int i = 0; i < Constants.rad2Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY)){
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
        if (bestDir >= 0){
            return myRobot.move(Constants.X[bestDir], Constants.Y[bestDir]);
        }
        return null;
    }

    void readMod(){
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX =  myRobot.me.x + Constants.X[i];
            int newY = myRobot.me.y + Constants.Y[i];
            Robot robot = utils.getRobot(newX, newY);
            if (robot != null){
                if (robot.team == myRobot.me.team && (robot.unit == Constants.CASTLE || robot.unit == Constants.CHURCH)){
                    mod2 = (robot.x + robot.y)%2;
                    objective = new Location(robot.x, robot.y);
                }
            }
        }
    }

    public Action getAttackAction(){
        Robot bestRobot = null;
        for (Robot r : utils.robotsInVision){
            if (!myRobot.isVisible(r)) continue;
            if (r.team == myRobot.me.team) continue;
            if (!inRange(r)) continue;
            if (bestRobot == null || r.health < bestRobot.health) bestRobot = r;
        }
        if (bestRobot != null){
            return myRobot.attack(bestRobot.x-myRobot.me.x, bestRobot.y-myRobot.me.y);
        }
        return null;
    }

    boolean inRange(Robot r){
        int d = utils.distance(r.x, r.y, myRobot.me.x, myRobot.me.y);
        if (d > Constants.range[myRobot.me.unit]) return false;
        if (d < Constants.minRange[myRobot.me.unit]) return false;
        return true;
    }


}
