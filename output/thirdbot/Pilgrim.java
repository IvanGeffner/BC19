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

    CastleCommunication castleCommunication;
    ChurchBuild churchBuild;

    public Pilgrim(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        castleCommunication = new CastleCommunication(myRobot);
        churchBuild = new ChurchBuild(myRobot, this);
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
        churchBuild.tryBuildChurch();
        sendCastleMessage();
        return actionToPerform;
    }

    void sendCastleMessage(){
        int info = Constants.MSG_PILGRIM;
        if (needChurch()) info = Constants.MSG_CHURCH;
        castleCommunication.sendCastleMessage(info, objective);
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

    boolean needChurch(){
        return !churchBuild.builtChurch && objective != null && approxFuelLostInTravel > Constants.MAX_FUEL_LOST;
    }

    boolean onObjective(){
        return objective != null && objective.x == myRobot.me.x && objective.y == myRobot.me.y;
    }
}
