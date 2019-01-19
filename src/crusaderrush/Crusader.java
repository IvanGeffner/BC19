package crusaderrush;

import btcutils.Action;
import btcutils.Robot;

import java.util.LinkedList;
import java.util.Queue;

public class Crusader extends Unit {

    Symmetry sym;
    Utils utils;

    Location spawner = null, target = null;

    Attacker attacker;

    int initialTurn;

    public Crusader(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        sym = new Symmetry(myRobot, utils);
        sym.checkMaps();
        attacker = new Attacker(myRobot, utils);
        initialTurn = myRobot.me.turn;
    }

    @Override
    public Action turn(){
        utils.update();
        if (spawner == null){
            spawner = getSpawnerLoc();
            target = sym.getSymmetric(spawner);
        }

        Action attackAction = attacker.tryAttack();
        if (attackAction != null) return attackAction;


        return movementAction();

    }

    Location getSpawnerLoc(){
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX = myRobot.me.x + Constants.X[i], newY = myRobot.me.y + Constants.Y[i];
            if (utils.robotMap[newY][newX] > 0){
                Robot r = myRobot.getRobot(utils.robotMap[newY][newX]);
                if (r.unit == Constants.CASTLE){
                    return new Location(newX, newY);
                }
            }
        }
        return null;
    }

    Action movementAction(){
        if (myRobot.me.turn > initialTurn && myRobot.karbonite >= 15) return null;
        int limit = Constants.rad4Index;
        if (utils.distance(myRobot.me.x, myRobot.me.y, target.x, target.y) <= 150){
            limit = Constants.rad9Index;
        }
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
            if (target != null && utils.distance(target, last) <= Constants.range[myRobot.me.unit]){
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

}
