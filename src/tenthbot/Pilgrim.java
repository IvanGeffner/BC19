package tenthbot;

import btcutils.Action;
import btcutils.Robot;

import java.util.LinkedList;
import java.util.Queue;

public class Pilgrim extends Unit {

    Location objective = null;
    Location closestStructure = null;
    int approxFuelLostInTravel = 0;
    Utils utils;
    Action actionToPerform;

    final int MINING_TARGET = 0;
    final int STRUCTURE_TARGET = 1;

    int targetCode;
    Location destination;

    //CastleCommunication castleCommunication;
    Broadcast broadcast;
    ChurchBuild churchBuild;

    boolean beenToObjective = false;

    Micro micro;

    public Pilgrim(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        broadcast = new Broadcast(myRobot, utils);
        micro = new Micro(myRobot, utils, Constants.rad4Index, null);
        churchBuild = new ChurchBuild(myRobot, this, micro);
    }

    @Override
    public Action turn(){
        utils.update();
        if (onObjective()) beenToObjective = true;
        int safeDir = micro.getBestIndex();
        if (objective == null) findObjective();
        if (objective != null) findClosestStructure();
        actionToPerform = null;
        findDestination();
        goToDestination();
        churchBuild.tryBuildChurch();
        sendCastleMessage();
        if (!micro.isSafeStaying()) actionToPerform = myRobot.move(Constants.X[safeDir], Constants.Y[safeDir]);
        //broadcast.broadcastClosestProphet();
        broadcast.broadcast();
        return actionToPerform;
    }

    void sendCastleMessage(){
        int info = Constants.MSG_PILGRIM;
        if (needChurch()) info = Constants.MSG_CHURCH;
        broadcast.sendCastleMessage(info, objective);
    }

    int getMaxFuel(){
        return Constants.MAX_FUEL_PILGRIM;
    }

    int getMaxKarbo(){
        if (myRobot.karbonite >= Constants.KARBO_PILGRIM_RICH && approxFuelLostInTravel <= Constants.MAX_FUEL_LOST) return Constants.MAX_KARBO_PILGRIM_RICH;
        return Constants.MAX_KARBO_PILGRIM;
    }

    void findDestination(){
        if (myRobot.me.fuel == 0 && myRobot.me.karbonite == 0){
            targetCode = MINING_TARGET;
            destination = objective;
            return;
        }
        if (closestStructure == null) return;
        if (myRobot.me.fuel >= getMaxFuel() || myRobot.me.karbonite >= getMaxKarbo()){
            if (closestStructure != null && approxFuelLostInTravel <= Constants.MAX_FUEL_LOST) {
                targetCode = STRUCTURE_TARGET;
                destination = closestStructure;
            }
        }
    }

    void findObjective(){
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX =  myRobot.me.x + Constants.X[i];
            int newY = myRobot.me.y + Constants.Y[i];
            Robot robot = utils.getRobot(newX, newY);
            if (robot != null){
                if (robot.team == myRobot.me.team && robot.unit == Constants.CASTLE && myRobot.isRadioing(robot)){
                    int message = robot.signal;
                    int xObj = (message/ Constants.maxMapSize)% Constants.maxMapSize;
                    int yObj = message% Constants.maxMapSize;
                    objective = new Location(xObj,yObj);
                    return;
                }
            }
        }
    }

    void goToDestination(){
        if (destination == null) return;
        int myX = myRobot.me.x, myY = myRobot.me.y;
        if (targetCode == MINING_TARGET){
            if (myX == destination.x && myY == destination.y){
                if (myRobot.me.karbonite < Constants.MAX_KARBO_PILGRIM_RICH && myRobot.me.fuel < Constants.MAX_FUEL_PILGRIM) actionToPerform = myRobot.mine();
                return;
            }
        } else if (targetCode == STRUCTURE_TARGET){
            if (utils.distance(myX, myY, destination.x, destination.y) <= 2){
                actionToPerform = myRobot.give(destination.x - myX, destination.y - myY, myRobot.me.karbonite, myRobot.me.fuel);
                return;
            }
        }
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        distM[myX][myY] = 1;
        Queue<Integer> queue = new LinkedList<>();
        queue.add((myX << Constants.SHIFT) | myY);
        while (!queue.isEmpty()) {
            Integer last = queue.poll();
            int lastX = (last >>> Constants.SHIFT) & Constants.BASE;
            int lastY = last & Constants.BASE;
            int lastFuel = fuel[lastX][lastY];
            int lastDist = distM[lastX][lastY];
            if (isDestination(lastX, lastY)){
                if (micro.isSafe(dirs[lastX][lastY])) actionToPerform = myRobot.move(Constants.X[dirs[lastX][lastY]], Constants.Y[dirs[lastX][lastY]]);
                return;
            }
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = lastX + Constants.X[i], newY = lastY + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY)){
                    if (distM[newX][newY] == 0){
                        queue.add((newX << Constants.SHIFT) | newY);
                        distM[newX][newY] = lastDist + 1;
                        fuel[newX][newY] = newFuel;
                        if (lastDist == 1) dirs[newX][newY] = i;
                        else dirs[newX][newY] = dirs[lastX][lastY];
                    }
                    if (distM[newX][newY] == lastDist + 1){
                        if (fuel[newX][newY] > newFuel){
                            fuel[newX][newY] = newFuel;
                            if (lastDist == 1) dirs[newX][newY] = i;
                            else dirs[newX][newY] = dirs[lastX][lastY];
                        }
                    }
                }
            }
        }
    }

    boolean isDestination(int x, int y){
        if (targetCode == MINING_TARGET) return utils.distance(x,y, destination.x, destination.y) == 0;
        if (targetCode == STRUCTURE_TARGET) return utils.distance(x,y, destination.x, destination.y) <= 2;
        return false;
    }

    boolean isDestination(Location loc){
        if (targetCode == MINING_TARGET) return utils.distance(loc, destination) == 0;
        if (targetCode == STRUCTURE_TARGET) return utils.distance(loc, destination) <= 2;
        return false;
    }

    int[][] distFromObjective = null;
    int[][] fuelFromObjective = null;

    void generateDistsFromObjective(){
        fuelFromObjective = new int[utils.dimX][utils.dimY];
        distFromObjective = new int[utils.dimX][utils.dimY];
        distFromObjective[objective.x][objective.y] = 1;
        Queue<Integer> queue = new LinkedList<>();
        queue.add((objective.x << Constants.SHIFT) | objective.y);
        while (!queue.isEmpty()) {
            Integer last = queue.poll();
            int lastX = (last >>> Constants.SHIFT) & Constants.BASE;
            int lastY = last & Constants.BASE;
            int lastFuel = fuelFromObjective[lastX][lastY];
            int lastDist = distFromObjective[lastX][lastY];
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = lastX + Constants.X[i], newY = lastY + Constants.Y[i];
                int newFuel = lastFuel + 2*(Constants.Steplength[i] + Constants.FUEL_MINING_RATE);
                if (utils.isInMap(newX, newY) && myRobot.map[newY][newX]){
                    if (distFromObjective[newX][newY] == 0){
                        queue.add((newX << Constants.SHIFT) | newY);
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

    Integer approxLostFuelToChurch(int x, int y){
        Integer ans = null;
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX = x + Constants.X[i], newY = y + Constants.Y[i];
            if (utils.isInMap(newX, newY) && myRobot.map[newY][newX]){
                if (ans == null || ans > fuelFromObjective[newX][newY]) ans = fuelFromObjective[newX][newY];
            }
        }
        return ans;
    }

    void findClosestStructure(){
        if (objective == null) return;
        if (distFromObjective == null) generateDistsFromObjective();
        checkStructure();
        for (Robot r : utils.robotsInVision){
            if (r.team != myRobot.me.team) continue;
            if (r.unit == Constants.CASTLE || r.unit == Constants.CHURCH){
                Integer approxFuel = approxLostFuelToChurch(r.x, r.y);
                if (approxFuel != null && (closestStructure == null || approxFuel < approxFuelLostInTravel)){
                    closestStructure = new Location(r.x, r.y);
                    approxFuelLostInTravel = approxFuel;
                }
            }
        }
    }

    void checkStructure(){
        if (closestStructure == null) return;
        int id = utils.robotMap[closestStructure.y][closestStructure.x];
        if (id == 0){
            closestStructure = null;
            churchBuild.builtChurch = false;
            approxFuelLostInTravel = Constants.INF;
        }
    }

    boolean needChurch(){
        return !churchBuild.builtChurch && objective != null && approxFuelLostInTravel > Constants.MAX_FUEL_LOST;
    }

    boolean onObjective(){
        return objective != null && objective.x == myRobot.me.x && objective.y == myRobot.me.y;
    }
}
