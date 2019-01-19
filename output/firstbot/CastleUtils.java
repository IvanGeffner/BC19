package bc19;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CastleUtils {

    MyRobot myRobot;
    boolean hSim;
    boolean vSim;
    int X, Y;
    Utils utils;
    int spawningIndex = 0;
    Location myLocation;
    Location spawningLocation;
    Action nextTurnAction = null;


    Objective[] objectives;
    int[] karbo;
    int[] fuel;
    int[] enemyCastles;


    public CastleUtils(MyRobot myRobot){
        this.myRobot = myRobot;
        hSim = vSim = true;
        X = myRobot.map.length;
        Y = myRobot.map[0].length;
        checkSym();
        utils = new Utils(myRobot);
        myLocation = new Location(myRobot.me.x, myRobot.me.y);
        findSpawningLocation();
        generateObjectiveList();
    }

    void update(){
        utils.update();
        if(hSim && vSim) readSymmetry();
        nextTurnAction = null;
    }

    void findSpawningLocation(){
        for (int i = 0; i < 8; ++i){
            if (utils.isEmptySpace(utils.X[i], utils.Y[i])){
                spawningIndex = i;
                spawningLocation = myLocation.add(i);
                return;
            }
        }
    }

    boolean createPilgrim(int objectiveIndex){
        int dir = objectives[objectiveIndex].dir;
        if (!utils.canBuild(Constants.PILGRIM)) return false;
        for (int i = 0; i < 8; ++i){
            if (utils.isEmptySpace(Utils.X[dir], Utils.Y[dir])){
                nextTurnAction = myRobot.buildUnit(Constants.PILGRIM, Utils.X[dir], Utils.Y[dir]);
                /*TODO check resources*/
                sendMiningLocation(objectiveIndex);
            }
            ++dir;
            if (dir > 8) dir = 0;
        }
        return false;
    }

    static final int prodConstant = 64;

    void sendMiningLocation(int objectiveIndex){
        int rad = Utils.Z[objectives[objectiveIndex].dir];
        int message = ((objectives[objectiveIndex].x*prodConstant) + objectives[objectiveIndex].y);
        myRobot.signal(message, rad);
    }

    void readSymmetry(){

    }

    void checkSym(){
        for (int i = 0; 2*i < X-1; ++i){
            for (int j = 0; j < Y; ++j){
                if (myRobot.map[i][j] != myRobot.map[X-1-i][j]){
                    vSim = false;
                    return;
                }
            }
        }
        for (int i = 0; i < X; ++i){
            for (int j = 0; 2*j < Y-1; ++j){
                if (myRobot.map[i][j] != myRobot.map[i][Y-1-j]){
                    hSim = false;
                    return;
                }
            }
        }
    }


    void generateObjectiveList(){
        int[][] dist = new int[X][Y];
        int[][] dir = new int[X][Y];
        int[][] fuelM = new int[X][Y];
        dist[myLocation.x][myLocation.y] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(myLocation);
        ArrayList<Objective> aux = new ArrayList<>();
        int karboCont = 0, fuelCont = 0;
        while (!queue.isEmpty()){
            Location last = queue.poll();
            int lastDist = dist[last.x][last.y];
            if (myRobot.fuelMap[last.x][last.y]){
                aux.add(new Objective(Constants.OBJ_FUEL, lastDist-1, last.x, last.y, dir[last.x][last.y]));
                ++fuelCont;
            }
            if (myRobot.karboniteMap[last.x][last.y]){
                aux.add(new Objective(Constants.OBJ_KARBO, lastDist-1, last.x, last.y, dir[last.x][last.y]));
                ++karboCont;
            }
            for (int i = 0; i < Utils.X.length; ++i){
                int newX = last.x + Utils.X[i];
                int newY = last.y + Utils.Y[i];
                int newFuel = fuelM[last.x][last.y] + 1;
                if (utils.isInMap(newX, newY)){
                    if (dist[newX][newY] == 0){
                        queue.add(new Location (newX, newY));
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
