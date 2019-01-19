package sixthbot;

import btcutils.Action;
import btcutils.Robot;

import java.util.LinkedList;
import java.util.Queue;

public class Prophet extends Unit {

    Micro micro;
    Utils utils;

    Location objective = null;
    Location finalAttackObjective = null;

    boolean[][] occupied = null;

    int mod2 = -1;

    Broadcast broadcast;
    Attacker attack;
    FinalAttack finalAttack;

    int turnsWaiting = 0;

    public Prophet(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        micro = new Micro(myRobot, utils, Constants.rad4Index);
        occupied = new boolean[utils.dimX][utils.dimY];
        broadcast = new Broadcast(myRobot, utils);
        attack = new Attacker(myRobot, utils, broadcast);
        finalAttack = new FinalAttack(myRobot, utils, broadcast);
    }

    @Override
    public Action turn(){
        utils.update();
        if (objective == null) findObjective();
        checkObjective();
        //if (mod2 == -1) readMod();
        Action act = chooseAction();
        if (objective != null) broadcast.sendCastleMessage(Constants.MSG_TROOP, objective);
        return act;
    }

    public Action chooseAction(){
        Integer microAction = micro.getBestIndex();
        if (microAction != null && microAction != micro.maxMovementIndex){
            if (micro.isOptimal(microAction) && !micro.isOptimalToStay()) return myRobot.move(Constants.X[microAction], Constants.Y[microAction]);
        }
        Location loc = attack.tryAttack();
        if (loc != null){
            int id = utils.robotMap[myRobot.me.x + loc.x][myRobot.me.y + loc.y];
            if (id > 0) broadcast.sendAttackID(id, Constants.ATTACK_BROADCAST);
            return myRobot.attack(loc.x, loc.y);
        }
        if (microAction != null && microAction != micro.maxMovementIndex) return myRobot.move(Constants.X[microAction], Constants.Y[microAction]);
        return movementAction();
    }

    void checkObjective(){
        if (objective == null) return;
        if (utils.robotMap[objective.y][objective.x] > 0){
            int id = utils.robotMap[objective.y][objective.x];
            if (id != myRobot.id){
                turnsWaiting++;
            }
        } else turnsWaiting = 0;
        if (turnsWaiting > Constants.MAX_PROPHET_WAIT){
            objective = null;
            turnsWaiting = 0;
        }
    }

    void findObjective(){
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX =  myRobot.me.x + Constants.X[i];
            int newY = myRobot.me.y + Constants.Y[i];
            Robot robot = utils.getRobot(newX, newY);
            if (robot != null){
                if (robot.team == myRobot.me.team && (robot.unit == Constants.CASTLE || robot.unit == Constants.CHURCH) && myRobot.isRadioing(robot)){
                    int message = robot.signal;
                    int mes = message/(2*Constants.maxMapSize*Constants.maxMapSize);
                    if (mes != broadcast.PROPHET_AGGRO) continue;
                    int xObj = (message/ Constants.maxMapSize)% Constants.maxMapSize;
                    int yObj = message% Constants.maxMapSize;
                    objective = new Location(xObj,yObj);
                    break;
                }
            }
        }
    }

    Action movementAction(){
        if (finalAttackObjective == null) finalAttackObjective = finalAttack.getFinalAttackLocation();
        if (finalAttackObjective != null){
            if (checkFinalObjective()) return finalAttackAction();
            else finalAttackObjective = null;
        }
        return objectiveAction();
    }

    boolean checkFinalObjective(){
        return utils.robotMap[finalAttackObjective.y][finalAttackObjective.x] != 0;
    }


    Action objectiveAction(){
        updateOccMatrix();
        int myX = myRobot.me.x, myY = myRobot.me.y;
        int bestDir = -1;
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        distM[myX][myY] = 1;
        dirs[myX][myY] = -1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if (objective == null && isGoodPosition(last)){
                bestDir = dirs[last.x][last.y];
                if (last.x == myRobot.me.x && last.y == myRobot.me.y) objective = last;
                break;
            } else if (objective != null && objective.x == last.x && objective.y == last.y){
                bestDir = dirs[last.x][last.y];
                break;
            }
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY) && !occupied[newX][newY]){
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

    Action finalAttackAction(){
        int limit = Constants.rad2Index;
        if (utils.distance(myRobot.me.x, myRobot.me.y, finalAttackObjective.x, finalAttackObjective.y) <= 150){
            limit = Constants.rad4Index;
        }
        int myX = myRobot.me.x, myY = myRobot.me.y;
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        distM[myX][myY] = 1;
        dirs[myX][myY] = -1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if (utils.distance(finalAttackObjective, last) <= Constants.range[myRobot.me.unit]){
                int dir = dirs[last.x][last.y];
                if (dir < 0) return null;
                return myRobot.move(Constants.X[dir], Constants.Y[dir]);
            }
            for (int i = 0; i < limit; ++i){
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
        return null;
    }

    boolean isGoodPosition(Location loc){
        if ((loc.x+loc.y)%2 != Constants.MOD2) return false;
        if (myRobot.karboniteMap[loc.y][loc.x]) return false;
        if (myRobot.fuelMap[loc.y][loc.x]) return false;
        return true;
    }

    void updateOccMatrix(){
        for (int i = myRobot.me.x-8; i <= myRobot.me.x+8; ++i){
            for (int j = myRobot.me.y-8; j <= myRobot.me.y+8; ++j){
                if (utils.isInMap(i,j)){
                    if (utils.robotMap[j][i] == 0) occupied[i][j] = false;
                    else if (utils.robotMap[j][i] > 0){
                        Robot r = myRobot.getRobot(utils.robotMap[j][i]);
                        if (r.unit != Constants.PILGRIM) occupied[i][j] = true;
                    }
                }
            }
        }
    }




}
