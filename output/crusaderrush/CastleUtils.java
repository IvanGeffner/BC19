package bc19;


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
    UnitInfo[] targetByID;
    UnitInfoGrid[][] unitInfoMap;

    int churchRequiredCont = 0;

    DefenseMechanism defenseMechanism;

    Broadcast broadcast;
    Attacker attack;


    public CastleUtils(MyRobot myRobot){
        this.myRobot = myRobot;
        initialTurn = myRobot.me.turn;
        hSim = vSim = true;
        utils = new Utils(myRobot);
        myLocation = new Location(myRobot.me.x, myRobot.me.y);
        objectives = null;
        defenseMechanism = new DefenseMechanism(myRobot, utils);
        broadcast = new Broadcast(myRobot, utils,0);
        attack = new Attacker(myRobot, utils);
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

    void checkBuildCrusader(){
        if (castleIndex != 0) return;
        Integer dir = defenseMechanism.buildDefenseUnit();
        if (dir != null) nextTurnAction = myRobot.buildUnit(Constants.CRUSADER, Constants.X[dir], Constants.Y[dir]);
    }

    void checkDefense(){
        Integer dir = defenseMechanism.defenseAction();
        if (dir != null){
            Location loc = getBestProphetLoc(dir);
            if (loc == null) return;
            nextTurnAction = myRobot.buildUnit(Constants.PROPHET, Constants.X[dir], Constants.Y[dir]);
            broadcast.sendTarget(loc, broadcast.PROPHET_AGGRO, Constants.Steplength[dir]);
        }
    }

    void checkFreeBuild(){
        if (nextTurnAction != null) return;
        Integer dir = defenseMechanism.buildUnitRich();
        if (dir != null) {
            Location loc = getBestProphetLoc(dir);
            if (loc == null) return;
            nextTurnAction = myRobot.buildUnit(Constants.PROPHET, Constants.X[dir], Constants.Y[dir]);
            broadcast.sendTarget(loc, broadcast.PROPHET_AGGRO, Constants.Steplength[dir]);
        }
    }

    void checkAttack(){
        Action act = attack.tryAttack();
        if (act != null){
            nextTurnAction = act;
            broadcast.reset();
        }
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
                broadcast.sendTarget(new Location(objectives[objectiveIndex].x, objectives[objectiveIndex].y), broadcast.PILGRIM_MINING,Constants.Steplength[dir]);
                /*TODO check resources*/
                //sendMiningLocation(objectiveIndex, dir);
                return true;
            }
            ++dir;
            if (dir >= Constants.rad2Index) dir = 0;
        }
        return false;
    }

    Location getBestProphetLoc(int dir){
        int[][] fuel = new int[utils.dimX][utils.dimY];
        int[][] dirs = new int[utils.dimX][utils.dimY];
        int[][] distM = new int[utils.dimX][utils.dimY];
        int myX = myRobot.me.x + Constants.X[dir], myY = myRobot.me.y + Constants.Y[dir];
        distM[myX][myY] = 1;
        Queue<Location> queue = new LinkedList<>();
        queue.add(new Location(myX, myY));
        while (!queue.isEmpty()) {
            Location last = queue.poll();
            int lastFuel = fuel[last.x][last.y];
            int lastDist = distM[last.x][last.y];
            if ((last.x + last.y)%2 == Constants.MOD2 && !myRobot.karboniteMap[last.y][last.x] && !myRobot.fuelMap[last.y][last.x]){
                if (isFree(last.x, last.y)) return last;
            }
            for (int i = 0; i < Constants.rad4Index; ++i){
                int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                int newFuel = lastFuel + Constants.Steplength[i];
                if (utils.isInMap(newX, newY) && utils.isEmptySpaceAbsolute(newX, newY) && isFree(newX, newY)){
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

    boolean isFree(int x, int y){
        UnitInfoGrid info = unitInfoMap[x][y];
        if (info == null || !info.isAlive()) return true;
        return false;
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
        if (churchRequiredCont > Constants.MAX_CHURCHES_WAITING) churchRequiredCont = Constants.MAX_CHURCHES_WAITING;
        int totalCostKarbo = Constants.SAFETY_KARBO + churchRequiredCont* Constants.karboCosts[Constants.CHURCH];
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
        if (totalCostKarbo > myRobot.karbonite) return null;
        if (totalCostFuel > myRobot.fuel) return null;
        return ans;
    }

    void initializePilgrimInfo(){
        unitInfoMap = new UnitInfoGrid[utils.dimX][utils.dimY];
        targetByID = new UnitInfo[Constants.MAX_ID];
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
                    updateInfo(r.id, Constants.PILGRIM, locInfo, myRobot.me.turn % 2);
                }
                if (mes == Constants.MSG_TROOP && myRobot.me.turn < Constants.MAX_TURN_CASTLE_LOC){
                    if (myRobot.me.turn == initialTurn+1) ++numCastles;
                    int locInfo = (r.castle_talk - 1) % Constants.maxMapSize;
                    updateCastleInfo(r.id, locInfo, myRobot.me.turn%2);
                }
                if (mes == Constants.MSG_CASTLE_LATE){
                    updateCastleInfo(r.id);
                }
                if (mes == Constants.MSG_TROOP && myRobot.me.turn >= Constants.MAX_TURN_CASTLE_LOC){
                    int locInfo = (r.castle_talk - 1) % Constants.maxMapSize;
                    updateInfo(r.id, Constants.PROPHET, locInfo, myRobot.me.turn%2);
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
        if (!found && myRobot.me.turn - initialTurn <= 1){
            myCastles[partialLoc].id = id;
            myCastles[partialLoc].lastReportedTurn = myRobot.me.turn;
        }
    }

    void updateCastleInfo(int id){
        if (id == myRobot.me.id) return;
        for (int i = 0; i < myCastles.length; ++i){
            if (myCastles[i].id == id){
                myCastles[i].lastReportedTurn = myRobot.me.turn;
            }
        }
    }

    void updateInfo(int id, int type, int partialLoc, int turn){
        UnitInfo info = targetByID[id];
        if (info == null) info = new UnitInfo(type, id);
        info.update();
        if (turn == 0){
            if (info.x == -1){
                info.x = partialLoc;
                if (info.y == -1) ++XOccupied[info.x];
            } else if (info.x != partialLoc) {
                if (info.y != -1) unitInfoMap[info.x][info.y] = null;
                info.x = partialLoc;
                info.y = -1;
                if (type == Constants.PILGRIM) ++XOccupied[info.x];
            }
        } else{
            if (info.y == -1){
                info.y = partialLoc;
                if (info.x == -1) ++YOccupied[info.y];
            } else if (info.y != partialLoc) {
                if (info.x != -1) unitInfoMap[info.x][info.y] = null;
                info.y = partialLoc;
                info.x = -1;
                if (type == Constants.PILGRIM) ++YOccupied[info.y];
            }
        }
        targetByID[id] = info;
        if (info.x != -1 && info.y != -1){
            if (unitInfoMap[info.x][info.y] == null) unitInfoMap[info.x][info.y] = new UnitInfoGrid(id, type);
            else unitInfoMap[info.x][info.y].update(id, type);
        }
    }


    int isOccupied(int objectiveIndex){
        Objective obj = objectives[objectiveIndex];
        if (XOccupied[obj.x] > 0 || YOccupied[obj.y] > 0) return Constants.PARTIALLLY_OCCUPIED;
        UnitInfoGrid pi = unitInfoMap[obj.x][obj.y];
        if (pi != null && pi.lastTurnReporting + Constants.MAX_TURNS_REPORTING > myRobot.me.turn) return Constants.OCCUPIED;
        return Constants.FREE;
    }

    void printObjectives(){
        for (int i = 0; i < utils.dimX; ++i){
            for (int j = 0; j < utils.dimY; ++j){
                UnitInfoGrid pi = unitInfoMap[i][j];
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
        if (myRobot.me.turn >= Constants.MAX_TURN_CASTLE_LOC - 1){
            int mes = Constants.MSG_CASTLE_LATE*locBits;
            myRobot.castleTalk(mes+1);
            return;
        }
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
        if (!allCastleLocs) {
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
        }
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

    class UnitInfo{
        int type, id, x, y, lastReportedTurn;

        public UnitInfo(int type, int id, int x, int y){
            this.type = type;
            this.id = id;
            this.x = x;
            this.y = y;
            lastReportedTurn = myRobot.me.turn;
        }

        public UnitInfo(int type, int id){
            this.type = type;
            this.id = id;
            this.x = -1;
            this.y = -1;
            lastReportedTurn = myRobot.me.turn;
        }

        void update(){
            lastReportedTurn = myRobot.me.turn;
        }

        boolean isAlive(){
            return myRobot.me.turn - lastReportedTurn < Constants.MAX_TURNS_REPORTING;
        }

    }

    class UnitInfoGrid {
        int id, lastTurnReporting, type;

        public UnitInfoGrid(int id, int type){
            this.id = id;
            this.type = type;
            lastTurnReporting = myRobot.me.turn;
        }

        void update(int id, int type){
            this.id = id;
            this.type = type;
            lastTurnReporting = myRobot.me.turn;
        }

        boolean isAlive(){
            return myRobot.me.turn - lastTurnReporting < Constants.MAX_TURNS_REPORTING;
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
