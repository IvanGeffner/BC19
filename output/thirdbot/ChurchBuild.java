package bc19;

import java.util.LinkedList;
import java.util.Queue;

public class ChurchBuild {

    boolean builtChurch = false;
    MyRobot myRobot;
    Pilgrim pilgrim;
    Utils utils;
    int[] buildValue;
    int maxValue;
    ChurchBuildInfo[] churchBuildInfo;

    public ChurchBuild(MyRobot myRobot, Pilgrim pilgrim){
        this.myRobot = myRobot;
        this.pilgrim = pilgrim;
        this.utils = pilgrim.utils;
    }

    void tryBuildChurch(){
        builtChurch = false;
        if (!pilgrim.onObjective()) return;
        if (!pilgrim.needChurch()) return;
        if (!utils.canBuild(Constants.CHURCH)) return;

        /*buildValue = new int[Constants.rad2Index];
        maxValue = 0;
        for (int i = 0; i < Constants.rad2Index; ++i){
            int newX = myRobot.me.x + Constants.X[i], newY = myRobot.me.y + Constants.Y[i];
            buildValue[i] = getValue(newX, newY);
            if (buildValue[i] > maxValue) maxValue = buildValue[i];
        }*/

        maxValue = 0;

        churchBuildInfo = new ChurchBuildInfo[Constants.rad2Index];
        for (int i = 0; i < churchBuildInfo.length; ++i){
            churchBuildInfo[i] = new ChurchBuildInfo(i, getValue(i));
            if (maxValue < churchBuildInfo[i].value) maxValue = churchBuildInfo[i].value;
        }


        ChurchBuildInfo bestLoc = null;
        for (int i = 0; i < churchBuildInfo.length; ++i){
            if (churchBuildInfo[i].value == maxValue && churchBuildInfo[i].value > 0) churchBuildInfo[i].computeParameters();
            if (churchBuildInfo[i].isBetter(bestLoc)) bestLoc = churchBuildInfo[i];
        }
        if (bestLoc != null){
            builtChurch = true;
            pilgrim.actionToPerform = myRobot.buildUnit(Constants.CHURCH, bestLoc.dx, bestLoc.dy);
        }

        /*for (int i = 0; i < Constants.rad2Index; ++i){
            if (buildValue[i] >= maxValue && buildValue[i] > 0){
                pilgrim.actionToPerform = myRobot.buildUnit(Constants.CHURCH, Constants.X[i], Constants.Y[i]);
                builtChurch = true;
            }
        }*/
    }

    int getValue(int x, int y){
        if (!utils.isEmptySpaceAbsolute(x,y)) return 0;
        if (myRobot.karboniteMap[y][x]) return 1;
        if (myRobot.fuelMap[y][x]) return 1;
        return 2;
    }

    int getValue(int i){
        return getValue(myRobot.me.x + Constants.X[i], myRobot.me.y + Constants.Y[i]);
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

    class ChurchBuildInfo{
        int resourcesNearby, totalFuel, dx, dy, value;

        public ChurchBuildInfo(int index, int value){
            resourcesNearby = 0;
            totalFuel = 0;
            dx = Constants.X[index];
            dy = Constants.Y[index];
            this.value = value;
        }

        boolean isAdjacent(int dx, int dy){
            if (Math.abs(dx - this.dx) > 1) return false;
            if (Math.abs(dy - this.dy) > 1) return false;
            return true;
        }

        boolean isBetter(ChurchBuildInfo c){
            if (this.value == 0) return false;
            if (c == null) return true;
            if (this.value > c.value) return true;
            if (c.value > this.value) return false;
            if (resourcesNearby > c.resourcesNearby) return true;
            if (c.resourcesNearby > resourcesNearby) return false;
            return totalFuel <= c.totalFuel;
        }

        final int MAX_DIST = 5;

        void computeParameters(){
            int[][] distM = new int[2*MAX_DIST+1][2*MAX_DIST+1];
            int[][] fuelM = new int[2*MAX_DIST+1][2*MAX_DIST+1];
            Queue<Location> queue = new LinkedList<>();
            for (int i = 0; i < Constants.rad2Index; ++i){
                int x = Constants.X[i], y = Constants.Y[i];
                if (utils.isInMap(myRobot.me.x+dx+x, myRobot.me.y+dy+y)){
                    queue.add(new Location(x,y));
                    distM[x+MAX_DIST][y+MAX_DIST] = 1;
                }
            }
            while (!queue.isEmpty()){
                Location last = queue.poll();
                int lastX = last.x+MAX_DIST, lastY = last.y+MAX_DIST;
                int lastFuel = fuelM[lastX][lastY];
                int lastDist = distM[lastX][lastY];
                int lastActualX = myRobot.me.x + dx + last.x;
                int lastActualY = myRobot.me.y + dy + last.y;
                if (myRobot.karboniteMap[lastActualY][lastActualX] || myRobot.fuelMap[lastActualY][lastActualX]){
                    ++resourcesNearby;
                    totalFuel += lastFuel;
                }
                else {
                    for (int i = 0; i < Constants.rad4Index; ++i) {
                        int newFuel = lastFuel + 2 * (Constants.Steplength[i] + Constants.FUEL_MINING_RATE);
                        if (newFuel > Constants.MAX_FUEL_LOST) continue;
                        int newX = last.x + Constants.X[i], newY = last.y + Constants.Y[i];
                        int actualX = myRobot.me.x + dx + newX, actualY = myRobot.me.y + dy + newY;
                        if (in(newX, newY) && utils.isInMap(actualX, actualY) && myRobot.map[actualY][actualX]) {
                            newX += MAX_DIST;
                            newY += MAX_DIST;
                            if (distM[newX][newY] == 0) {
                                queue.add(new Location(newX-MAX_DIST, newY-MAX_DIST));
                                distM[newX][newY] = lastDist + 1;
                                fuelM[newX][newY] = newFuel;
                            }
                            if (distM[newX][newY] == lastDist + 1) {
                                if (fuelM[newX][newY] > newFuel) {
                                    fuelM[newX][newY] = newFuel;
                                }
                            }
                        }
                    }
                }
            }
        }

        void print(){
            myRobot.log("Church at location " + (myRobot.me.x + dx) +  " " + (myRobot.me.y + dy) + " " +resourcesNearby + " " + totalFuel);
        }

        boolean in (int x, int y){
            if (x < -MAX_DIST) return false;
            if (x > MAX_DIST) return false;
            if (y < -MAX_DIST) return false;
            if (y > MAX_DIST) return false;
            return true;
        }

    }
}
