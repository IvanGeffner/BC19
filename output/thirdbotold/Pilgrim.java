package bc19;


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
    boolean builtChurch;

    CastleCommunication castleCommunication;

    public Pilgrim(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        castleCommunication = new CastleCommunication(myRobot);
    }

    @Override
    public Action turn(){
        utils.update();
        castleCommunication.update();
        if (objective == null) findObjective();
        if (objective != null) findClosestStructure();
        actionToPerform = null;
        findDestination();
        goToDestination();
        tryBuildChurch();
        sendCastleMessage();
        return actionToPerform;
    }

    void sendCastleMessage(){
        int info = Constants.MSG_PILGRIM;
        if (needChurch()) info = Constants.MSG_CHURCH;
        castleCommunication.sendCastleMessage(info, objective);
    }

    void tryBuildChurch(){
        builtChurch = false;
        if (!onObjective()) return;
        if (!needChurch()) return;
        if (!utils.canBuild(Constants.CHURCH)) return;
        int bestIndex = -1;
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX = myRobot.me.x + Constants.X[i], newY = myRobot.me.y + Constants.Y[i];
            if (!utils.isInMap(newX, newY)) continue;
            if (isBetter(bestIndex, i)) bestIndex = i;
        }
        if (bestIndex != -1){
            actionToPerform = myRobot.buildUnit(Constants.CHURCH, Constants.X[bestIndex], Constants.Y[bestIndex]);
            builtChurch = true;
        }
    }

    boolean isBetter(int i, int j){
        int newX1 = myRobot.me.x + Constants.X[i], newY1 = myRobot.me.y + Constants.Y[i];
        int newX2 = myRobot.me.x + Constants.X[j], newY2 = myRobot.me.y + Constants.Y[j];
        if (!utils.isEmptySpaceAbsolute(newX2, newY2)) return false;
        if (i == -1) return true;
        if (!utils.isEmptySpaceAbsolute(newX1, newY1)) return true;
        if (myRobot.karboniteMap[newY1][newX1]) return true;
        if (myRobot.fuelMap[newY1][newX1]) return true;
        return false;
    }

    void findDestination(){
        if (myRobot.me.fuel >= Constants.MAX_FUEL_PILGRIM || myRobot.me.karbonite >= Constants.MAX_KARBO_PILGRIM){
            targetCode = STRUCTURE_TARGET;
            destination = closestStructure;
        }
        else {
            targetCode = MINING_TARGET;
            destination = objective;
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
                    int xObj = (message/ Constants.maxMapSize)%Constants.maxMapSize;
                    int yObj = message% Constants.maxMapSize;
                    objective = new Location(xObj,yObj);
                    break;
                }
            }
        }
    }

    void goToDestination(){
        if (destination == null) return;
        int myX = myRobot.me.x, myY = myRobot.me.y;
        if (targetCode == MINING_TARGET){
            if (myX == destination.x && myY == destination.y){
                actionToPerform = myRobot.mine();
                return;
            }
        } else if (targetCode == STRUCTURE_TARGET){
            if (utils.distance(myX, destination.x, myY, destination.y) <= 2){
                actionToPerform = myRobot.give(destination.x - myX, destination.y - myY, myRobot.me.karbonite, myRobot.me.fuel);
                return;
            }
        }
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
            if (isDestination(last)){
                actionToPerform = myRobot.move(Constants.X[dirs[last.x][last.y]], Constants.Y[dirs[last.x][last.y]]);
                break;
            }
            for (int i = 0; i < Constants.rad4Index; ++i){
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
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(objective.x, objective.y));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuelFromObjective[last.x][last.y];
            int lastDist = distFromObjective[last.x][last.y];
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
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

    int approxLostFuel(int x, int y){
        return 2*(Constants.FUEL_MINING_RATE*(distFromObjective[x][y]-1) + fuelFromObjective[x][y]);
    }

    void findClosestStructure(){
        if (objective == null) return;
        if (distFromObjective == null) generateDistsFromObjective();
        for (Robot r : utils.robotsInVision){
            if (r.team != myRobot.me.team) continue;
            if (r.unit == Constants.CASTLE || r.unit == Constants.CHURCH){
                if (closestStructure == null || approxLostFuel(r.x, r.y) < approxFuelLostInTravel){
                    closestStructure = new Location(r.x, r.y);
                    approxFuelLostInTravel = approxLostFuel(r.x, r.y);
                }
            }
        }
    }

    boolean needChurch(){
        return !builtChurch && objective != null && approxFuelLostInTravel > Constants.MAX_FUEL_LOST;
    }

    boolean onObjective(){
        return objective != null && objective.x == myRobot.me.x && objective.y == myRobot.me.y;
    }
}
