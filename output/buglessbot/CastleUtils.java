package bc19;

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
        myLocation = new Location(myRobot.me.x, myRobot.me.y);
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
        for (int i = 0; i < Constants.X.length; ++i){
            if (utils.isEmptySpace(Constants.X[dir], Constants.Y[dir])){
                myRobot.log("Building! Direction " + dir);
                nextTurnAction = myRobot.buildUnit(Constants.PILGRIM, Constants.X[dir], Constants.Y[dir]);
                /*TODO check resources*/
                sendMiningLocation(objectiveIndex);
                return true;
            }
            ++dir;
            if (dir > Constants.X.length) dir = 0;
        }
        return false;
    }

    void sendMiningLocation(int objectiveIndex){
        int rad = Constants.Steplength[objectives[objectiveIndex].dir];
        int message = ((objectives[objectiveIndex].x*Constants.maxMapSize) + objectives[objectiveIndex].y);
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
            if (myRobot.fuelMap[last.y][last.x]){
                aux.add(new Objective(Constants.OBJ_FUEL, lastDist-1, last.x, last.y, dir[last.x][last.y]));
                ++fuelCont;
            }
            if (myRobot.karboniteMap[last.y][last.x]){
                aux.add(new Objective(Constants.OBJ_KARBO, lastDist-1, last.x, last.y, dir[last.x][last.y]));
                ++karboCont;
            }
            int limit = Constants.rad4Index;
            if (lastDist == 1) limit = Constants.rad2Index;
            for (int i = 0; i < limit; ++i){
                int newX = last.x + Constants.X[i];
                int newY = last.y + Constants.Y[i];
                int newFuel = fuelM[last.x][last.y] + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY)){
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
