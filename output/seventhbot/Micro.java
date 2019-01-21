package bc19;


public class Micro {

    MyRobot myRobot;
    Utils utils;
    int maxMovementIndex;

    int turnActivated;

    MicroInfo[] microInfoArray;
    MicroInfo bestMicro;

    int unitType;
    boolean addZero = false;
    Broadcast broadcast;

    public Micro(MyRobot myRobot, Utils utils, int maxMovementIndex,Broadcast broadcast){
        this.myRobot = myRobot;
        this.utils = utils;
        this.maxMovementIndex = maxMovementIndex;
        this.turnActivated = this.myRobot.me.turn-1;
        this.unitType = myRobot.me.unit;
        addZero = true;
        this.broadcast = broadcast;
    }

    public Micro(MyRobot myRobot, Utils utils, int maxMovementIndex, int unitType){
        this.myRobot = myRobot;
        this.utils = utils;
        this.maxMovementIndex = maxMovementIndex;
        this.turnActivated = this.myRobot.me.turn-1;
        this.unitType = unitType;
        addZero = false;
    }

    Integer getBestIndex(){
        if (turnActivated < myRobot.me.turn){
            generateMicroArray();
            turnActivated = myRobot.me.turn;
        }

        bestMicro = null;
        for (MicroInfo m : microInfoArray){
            if (m.isEqualOrBetter(bestMicro)) bestMicro = m;
        }
        if (bestMicro == null) return null;
        //if (myRobot.me.unit == Constants.PROPHET) myRobot.log("I'm at " + myRobot.me.x + " " + myRobot.me.y + " Best micro is " + bestMicro.i);
        return bestMicro.i;
    }

    void generateMicroArray(){
        if (addZero) microInfoArray = new MicroInfo[maxMovementIndex+1];
        else microInfoArray = new MicroInfo[maxMovementIndex];
        for (int i = 0; i < microInfoArray.length; ++i){
            if (i == maxMovementIndex){
                microInfoArray[i] = new MicroInfo(i, true);
                continue;
            }
            int newX = myRobot.me.x + Constants.X[i], newY = myRobot.me.y+ Constants.Y[i];
            microInfoArray[i] = new MicroInfo(i, utils.isEmptySpaceAbsolute(newX, newY));
        }
        for (Robot r : utils.robotsInVision){
            if (myRobot.isVisible(r) && r.team != myRobot.me.team){
                for (MicroInfo m : microInfoArray){
                    if (m.accessible) m.update(r);
                }
            }
        }
        if (broadcast != null){
            Location[] enemyRangers = broadcast.getRangers();
            for (Location loc : enemyRangers) {
                if (loc == null) break;
                //myRobot.log("I'm " + myRobot.me.id + " Detecting ranger at " + loc.x + " " + loc.y);
                int d = utils.distance(myRobot.me.x, myRobot.me.y, loc.x, loc.y);
                //myRobot.log("Distance "+ d);
                if (d <= Constants.visionRange[myRobot.me.unit]) continue;
                for (MicroInfo m : microInfoArray){
                    if (m.accessible) m.updateRanger(loc);
                }
            }
        }
    }

    boolean isSafe(int i){
        return microInfoArray[i].isEqualOrBetter(bestMicro);
    }

    boolean isSafeStaying(){
        if (!addZero) return false;
        return isSafe(maxMovementIndex);
    }

    boolean isOptimal(int i){
        MicroInfo m = microInfoArray[i];
        return (m.canShoot && m.dmgTaken == 0);
    }

    boolean isOptimalToStay(){
        if (!addZero) return false;
        return isOptimal(maxMovementIndex);
    }

    boolean oneShot(int i){
        MicroInfo m = microInfoArray[i];
        return (m.dmgTaken >= Constants.health[unitType]);
    }

    class MicroInfo{
        int dmgTaken = 0;
        boolean accessible;
        boolean canShoot = false;
        int minRange = Constants.INF;
        int i;
        int x, y;

        MicroInfo(int i, boolean accessible){
            this.i = i;
            this.accessible = accessible;
            if (i < maxMovementIndex) {
                this.x = myRobot.me.x + Constants.X[i];
                this.y = myRobot.me.y + Constants.Y[i];
            }
            else {
                this.x = myRobot.me.x;
                this.y = myRobot.me.y;
            }
        }

        boolean isEqualOrBetter(MicroInfo m){
            if (!accessible) return false;
            if (m == null) return true;
            if (!m.accessible) return true;
            if (dmgTaken < m.dmgTaken) return true;
            if (m.dmgTaken < dmgTaken) return false;
            if (inRange()){
                if (!m.inRange()) return true;
                return (minRange >= m.minRange);
            }
            if (m.inRange()) return false;
            return minRange <= m.minRange;
        }

        boolean inRange(){
            return minRange <= Constants.range[unitType];
        }

        void update(Robot r){
            int d = utils.distance(r.x, r.y, x,y);
            if (Constants.dangerRange[r.unit] >= d) dmgTaken += Constants.attack[r.unit];
            if (myRobot.me.unit != Constants.PILGRIM && minRange > d) minRange = d;
            if (d >= Constants.minRange[myRobot.me.unit] && d <= Constants.dangerRange[myRobot.me.unit]) canShoot = true;
        }

        void updateRanger(Location loc){
            int d = utils.distance(loc.x, loc.y, x,y);
            if (d > Constants.MAX_RANGE_PROPHET) return;
            if (Constants.dangerRange[Constants.PROPHET] >= d) dmgTaken += Constants.attack[Constants.PROPHET];
            //if (myRobot.me.unit != Constants.PILGRIM && minRange > d) minRange = d;
            //if (d >= Constants.minRange[myRobot.me.unit] && d <= Constants.dangerRange[myRobot.me.unit]) canShoot = true;
        }

    }

}
