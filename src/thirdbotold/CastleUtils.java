package thirdbotold;

import btcutils.Action;
import btcutils.Robot;

import java.util.ArrayList;
import java.util.Arrays;
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

    int[][] closestCastle;
    boolean allCastleLocs = false;
    boolean[] alive;


    int initialTurn;
    int castleIndex = 0;
    CastleInfo[] myCastles;
    int numCastles = 0;

    int[] XOccupied, YOccupied;
    Location[] targetByID;
    PilgrimInfo[][] pilgrimInfoMap;

    int churchRequiredCont = 0;


    public CastleUtils(MyRobot myRobot){
        this.myRobot = myRobot;
        initialTurn = myRobot.me.turn;
        hSim = vSim = true;
        utils = new Utils(myRobot);
        myLocation = new Location(myRobot.me.x, myRobot.me.y);
        objectives = null;
        initializePilgrimInfo();
        initializeCastleInfo();
    }

    void update(){
        utils.update();
        readCastleTalk();
        sendLocation();
        doFirstTurnsStuff();
        //if (myRobot.me.turn == initialTurn+3) printCastleInfo();
        checkObjectives();
        nextTurnAction = null;
    }

    void doFirstTurnsStuff(){
        if (myRobot.me.turn == initialTurn) {
            getCastleIndex();
            sendIndex();
        }
        else if (myRobot.me.turn == initialTurn+1) myCastles = Arrays.copyOf(myCastles, numCastles);
    }

    /*void createPilgrim(){
        for (int objective = 0; objective < objectives.length; ++objective) {
            int occ = isOccupied(objective);
            if (occ == Constants.FREE) {
                createPilgrim(objective);
                return;
            } else if (occ == Constants.PARTIALLLY_OCCUPIED) return;
        }
    }*/

    boolean createPilgrim(int objectiveIndex){
        int dir = objectives[objectiveIndex].dir;
        if (!utils.canBuild(Constants.PILGRIM)) return false;
        for (int i = 0; i < Constants.rad2Index; ++i){
            if (utils.isEmptySpace(Constants.X[dir], Constants.Y[dir])){
                nextTurnAction = myRobot.buildUnit(Constants.PILGRIM, Constants.X[dir], Constants.Y[dir]);
                /*TODO check resources*/
                sendMiningLocation(objectiveIndex, dir);
                return true;
            }
            ++dir;
            if (dir >= Constants.rad2Index) dir = 0;
        }
        return false;
    }

    void sendMiningLocation(int objectiveIndex, int dir){
        int rad = Constants.Steplength[dir];
        int message = ((objectives[objectiveIndex].x* Constants.maxMapSize) + objectives[objectiveIndex].y);
        signalWithRound(message, rad);
    }

    void signalWithRound(int message, int rad){
        message = (((myRobot.me.turn)%2)* Constants.LocationSize + message);
        myRobot.signal(message, rad);
    }


    void generateObjectiveList(){
        int[][] dist = new int[utils.dimX][utils.dimY];
        int[][] dir = new int[utils.dimX][utils.dimY];
        int[][] fuelM = new int[utils.dimX][utils.dimY];
        int[][] closestCastle = new int[utils.dimX][utils.dimY];
        Queue<Location> queue = new LinkedList<>();
        for (int i = 0; i < myCastles.length; ++i){
            if (myCastles[i].wellDefined()){
                dist[myCastles[i].x][myCastles[i].y] = 1;
                closestCastle[myCastles[i].x][myCastles[i].y] = i;
                queue.add(new Location(myCastles[i].x, myCastles[i].y));
            }
        }
        ArrayList<Objective> aux = new ArrayList<>();
        int karboCont = 0, fuelCont = 0;
        while (!queue.isEmpty()){
            Location last = queue.poll();
            int lastDist = dist[last.x][last.y];
            int castle = closestCastle[last.x][last.y];
            if (myRobot.fuelMap[last.y][last.x]){
                aux.add(new Objective(Constants.OBJ_FUEL, lastDist-1, last.x, last.y, dir[last.x][last.y],castle));
                ++fuelCont;
            }
            if (myRobot.karboniteMap[last.y][last.x]){
                aux.add(new Objective(Constants.OBJ_KARBO, lastDist-1, last.x, last.y, dir[last.x][last.y],castle));
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
                        closestCastle[newX][newY] = castle;
                        if (lastDist == 1) dir[newX][newY] = i;
                        else dir[newX][newY] = dir[last.x][last.y];
                    }
                    if (dist[newX][newY] == lastDist + 1){
                        if (fuelM[newX][newY] > newFuel){
                            fuelM[newX][newY] = newFuel;
                            closestCastle[newX][newY] = castle;
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

    Integer shouldBuildPilgrim(){
        int totalCostKarbo = churchRequiredCont* Constants.karboCosts[Constants.CHURCH];
        int totalCostFuel = Constants.SAFETY_FUEL + churchRequiredCont* Constants.fuelCosts[Constants.CHURCH];
        if (objectives == null) return null;
        Integer ans = null;
        for (int objective = 0; objective < objectives.length; ++objective) {
            int occ = isOccupied(objective);
            if (occ == Constants.FREE) {
                totalCostKarbo += Constants.karboCosts[Constants.PILGRIM];
                totalCostFuel += Constants.fuelCosts[Constants.PILGRIM];
                if (objectives[objective].castle == castleIndex){
                    ans = objective;
                    break;
                }
            } else if (occ == Constants.PARTIALLLY_OCCUPIED){
                if (objectives[objective].castle == castleIndex) return null;
                else{
                    totalCostKarbo += Constants.karboCosts[Constants.PILGRIM];
                    totalCostFuel += Constants.fuelCosts[Constants.PILGRIM];
                }
            }
        }
        if (ans == null) return null;
        if (totalCostKarbo >= myRobot.karbonite) return null;
        if (totalCostFuel >= myRobot.fuel) return null;
        return ans;
    }

    void initializePilgrimInfo(){
        pilgrimInfoMap = new PilgrimInfo[utils.dimX][utils.dimY];
        targetByID = new Location[Constants.MAX_ID];
    }

    void readCastleTalk(){
        XOccupied = new int[utils.dimX];
        YOccupied = new int[utils.dimY];
        churchRequiredCont = 0;
        for (Robot r : utils.robotsInVision){
            if (r.team == myRobot.me.team && r.castle_talk > 0){
                int mes = (r.castle_talk-1)/ Constants.maxMapSize;
                if (mes == Constants.MSG_PILGRIM || mes == Constants.MSG_CHURCH) {
                    if (mes == Constants.MSG_CHURCH) ++churchRequiredCont;
                    int locInfo = (r.castle_talk - 1) % Constants.maxMapSize;
                    updatePilgrimInfo(r.id, locInfo, myRobot.me.turn % 2);
                }
                if (mes == Constants.MSG_CASTLE){
                    if (myRobot.me.turn == initialTurn+1) ++numCastles;
                    int locInfo = (r.castle_talk - 1) % Constants.maxMapSize;
                    updateCastleInfo(r.id, locInfo, myRobot.me.turn%2);
                }
            }
        }
    }

    void updateCastleInfo(int id, int partialLoc, int turn){
        if (id == myRobot.me.id) return;
        boolean found = false;
        for (int i = 0; i < myCastles.length; ++i){
            if (myCastles[i].id == id){
                found = true;
                if (i > castleIndex) turn = (turn+1)%2;
                if (turn == 0) myCastles[i].x = partialLoc;
                else myCastles[i].y = partialLoc;
                myCastles[i].lastReportedTurn = myRobot.me.turn;
            }
        }
        if (!found){
            myCastles[partialLoc].id = id;
            myCastles[partialLoc].lastReportedTurn = myRobot.me.turn;
        }
    }

    void updatePilgrimInfo(int id, int partialLoc, int turn){
        Location loc = targetByID[id];
        if (loc == null) loc = new Location(-1, -1);
        if (turn == 0){
            if (loc.x == -1){
                loc.x = partialLoc;
                if (loc.y == -1) ++XOccupied[loc.x];
            } else if (loc.x != partialLoc) {
                if (loc.y != -1) pilgrimInfoMap[loc.x][loc.y] = null;
                loc.x = partialLoc;
                loc.y = -1;
                ++XOccupied[loc.x];
            }
        } else{
            if (loc.y == -1){
                loc.y = partialLoc;
                if (loc.x == -1) ++YOccupied[loc.y];
            } else if (loc.y != partialLoc) {
                if (loc.x != -1) pilgrimInfoMap[loc.x][loc.y] = null;
                loc.y = partialLoc;
                loc.x = -1;
                ++YOccupied[loc.y];
            }
        }
        targetByID[id] = loc;
        if (loc.x != -1 && loc.y != -1){
            pilgrimInfoMap[loc.x][loc.y] = new PilgrimInfo(id, myRobot.me.turn);
        }
    }


    int isOccupied(int objectiveIndex){
        Objective obj = objectives[objectiveIndex];
        if (XOccupied[obj.x] > 0 || YOccupied[obj.y] > 0) return Constants.PARTIALLLY_OCCUPIED;
        PilgrimInfo pi = pilgrimInfoMap[obj.x][obj.y];
        if (pi != null && pi.lastTurnReporting + Constants.MAX_TURNS_REPORTING > myRobot.me.turn) return Constants.OCCUPIED;
        return Constants.FREE;
    }

    void printObjectives(){
        for (int i = 0; i < utils.dimX; ++i){
            for (int j = 0; j < utils.dimY; ++j){
                PilgrimInfo pi = pilgrimInfoMap[i][j];
                if (pi != null)myRobot.log("Robot with id " + pi.id + " is going to " + i + " " + j);
            }
        }
    }

    void getCastleIndex(){
        for (Robot r: utils.robotsInVision){
            if (r.team == myRobot.me.team && r.castle_talk > 0) ++castleIndex;
        }
        myCastles[castleIndex].x = myLocation.x;
        myCastles[castleIndex].y = myLocation.y;
        myCastles[castleIndex].id = myRobot.me.id;
    }

    final int locBits = 64;

    void sendLocation(){
        int mes = (Constants.MSG_CASTLE)*locBits;
        if (myRobot.me.turn%2 == 0) mes += myLocation.x;
        else mes += myLocation.y;
        myRobot.castleTalk(mes+1);
    }

    void sendIndex(){
        int mes = (Constants.MSG_CASTLE)*locBits + castleIndex;
        myRobot.castleTalk(mes+1);
    }

    void initializeCastleInfo(){
        myCastles = new CastleInfo[Constants.MAX_CASTLES];
        for (int i = 0; i < myCastles.length; ++i) myCastles[i] = new CastleInfo();
    }

    void checkObjectives(){
        /*if (!allCastleLocs) {
            boolean aux = true;
            for (int i = 0; i < myCastles.length; ++i) if (!myCastles[i].wellDefined()) aux = false;
            if (aux){
                allCastleLocs = true;
                generateObjectiveList();
            }
        } else{
            boolean shouldCheck = false;
            for (int i = 0; i < myCastles.length; ++i){
                if (i != castleIndex && myCastles[i].alive && !myCastles[i].isAlive()){
                    myCastles[i].alive = false;
                    shouldCheck = true;
                }
            }
            if (shouldCheck) generateObjectiveList();
        }*/
        if (objectives == null) generateObjectiveList();
    }

    void printCastleInfo(){
        myRobot.log("Total number of castles " + numCastles);
        myRobot.log("My index is " + castleIndex);
        for (int i = 0; i < myCastles.length; ++i){
            myRobot.log("Castle " + i);
            myRobot.log(myCastles[i].id + " " + myCastles[i].x + " " + myCastles[i].y);
        }
    }

    class Objective{
        int type, dist, x, y, dir, castle;

        public Objective (int type, int dist, int x, int y, int dir, int castle){
            this.type = type;
            this.dist = dist;
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.castle = castle;
        }

        void print(){
            myRobot.log(this.type + " " + this.dist + " " + this.x + " " + this.y + " " + this.dir + " " + this.castle);
        }
    }

    class PilgrimInfo{
        int id, lastTurnReporting;

        public PilgrimInfo(int id, int lastTurnReporting){
            this.id = id;
            this.lastTurnReporting = lastTurnReporting;
        }

    }

    class CastleInfo{
        int id, x, y, lastReportedTurn;
        boolean alive = true;

        public CastleInfo(){
            id = -1;
            x = -1;
            y = -1;
            lastReportedTurn = 0;
            alive = true;
        }

        boolean wellDefined(){
            return id >= 0 && x >= 0 & y >= 0;
        }

        boolean isAlive(){
            return myRobot.me.turn - lastReportedTurn < Constants.MAX_TURNS_REPORTING;
        }
    }
}
