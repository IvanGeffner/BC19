package secondbot;
import btcutils.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CastleUtils {

    MyRobot myRobot;
    boolean hSim;
    boolean vSim;
    Utils utils;
    Location myLocation;
    Action nextTurnAction = null;


    Objective[] objectives;
    int[] karbo;
    int[] fuel;
    int[] enemyCastles;


    public CastleUtils(MyRobot myRobot){
        this.myRobot = myRobot;
        hSim = vSim = true;
        utils = new Utils(myRobot);
        myLocation = new Location(myRobot.me.y, myRobot.me.x);
        objectives = null;
    }

    void update(){
        utils.update();
        if (objectives == null) generateObjectiveList();
        nextTurnAction = null;
    }

    boolean createPilgrim(int objectiveIndex){
        int dir = objectives[objectiveIndex].dir;
        if (!utils.canBuild(Constants.PILGRIM)) return false;
        myRobot.log("Trying to build: Direction " + dir);
        for (int i = 0; i < Utils.X.length; ++i){
            if (utils.isEmptySpace(Utils.X[dir], Utils.Y[dir])){
                myRobot.log("Building! Direction " + dir);
                nextTurnAction = myRobot.buildUnit(Constants.PILGRIM, Utils.X[dir], Utils.Y[dir]);
                /*TODO check resources*/
                sendMiningLocation(objectiveIndex);
                return true;
            }
            ++dir;
            if (dir > Utils.X.length) dir = 0;
        }
        return false;
    }

    static final int prodConstant = 64;

    void sendMiningLocation(int objectiveIndex){
        int rad = Utils.Z[objectives[objectiveIndex].dir];
        int message = ((objectives[objectiveIndex].x*prodConstant) + objectives[objectiveIndex].y);
        myRobot.signal(message, rad);
    }


    void generateObjectiveList(){
        int[][] dist = new int[utils.dimX][utils.dimY];
        int[][] dir = new int[utils.dimX][utils.dimY];
        int[][] fuelM = new int[utils.dimX][utils.dimY];
        dist[myLocation.x][myLocation.y] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(myLocation);
        ArrayList<Objective> aux = new ArrayList<>();
        int karboCont = 0, fuelCont = 0;
        while (!queue.isEmpty()){
            Location last = queue.poll();
            int lastDist = dist[last.x][last.y];
            if (utils.getFuel(last.x,last.y)){
                aux.add(new Objective(Constants.OBJ_FUEL, lastDist-1, last.x, last.y, dir[last.x][last.y]));
                ++fuelCont;
            }
            if (utils.getKarbonite(last.x,last.y)){
                aux.add(new Objective(Constants.OBJ_KARBO, lastDist-1, last.x, last.y, dir[last.x][last.y]));
                ++karboCont;
            }
            int limit = Pilgrim.Xfast.length;
            if (lastDist == 1) limit = Utils.X.length;
            for (int i = 0; i < limit; ++i){
                int newX = last.x + Pilgrim.Xfast[i];
                int newY = last.y + Pilgrim.Yfast[i];
                int newFuel = fuelM[last.x][last.y] + Pilgrim.fuelCost[i];
                if (utils.isInMap(newX, newY) && utils.isPassable(newX, newY) && (lastDist > 1 || utils.isEmptySpaceAbsolute(newX, newY))){
                    if (dist[newX][newY] == 0){
                        queue.add(new Location(newX, newY));
                        dist[newX][newY] = lastDist + 1;
                        fuelM[newX][newY] = newFuel;
                        if (lastDist == 1) dir[newX][newY] = i;
                        else dir[newX][newY] = dir[last.x][last.y];
                    }
                    if (dist[newX][newY] == lastDist + 1){
                        if (fuelM[newX][newY] > newFuel){
                            fuelM[newX][newY] = newFuel;
                            if (lastDist == 1) dir[newX][newY] = i;
                            else dir[newX][newY] = dir[last.x][last.y];
                        }
                    }
                }
            }
        }
        objectives = aux.toArray(new Objective[aux.size()]);
        karbo = new int[karboCont];
        fuel = new int[fuelCont];
        int indKarbo = 0, indFuel = 0;
        for (int i = 0; i < objectives.length; ++i){
            if (objectives[i].type == Constants.OBJ_KARBO) karbo[indKarbo++] = i;
            if (objectives[i].type == Constants.OBJ_FUEL) fuel[indFuel++] = i;
        }
    }

    class Objective{
        int type, dist, x, y, dir;

        public Objective (int type, int dist, int x, int y, int dir){
            this.type = type;
            this.dist = dist;
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

}
