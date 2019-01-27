package bc19;


public class PreacherMicro {

    MyRobot myRobot;
    Utils utils;
    int maxMovementIndex;

    int turnActivated;

    MicroInfo[] microInfoArray;
    MicroInfo bestMicro;

    int unitType;
    Broadcast broadcast;

    public PreacherMicro(MyRobot myRobot, Utils utils, int maxMovementIndex, Broadcast broadcast){
        this.myRobot = myRobot;
        this.utils = utils;
        this.maxMovementIndex = maxMovementIndex;
        this.turnActivated = this.myRobot.me.turn-1;
        this.unitType = myRobot.me.unit;
        this.broadcast = broadcast;
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

        //myRobot.log("Best action is..." + bestMicro.i);
        return bestMicro.i;
    }

    void generateMicroArray(){
        microInfoArray = new MicroInfo[maxMovementIndex+1];
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
        //myRobot.log("Checking extra units");
        if (broadcast != null){
            MeleeUnit[] enemyMelee = broadcast.getMelee();
            int cont = -1;
            for (MeleeUnit loc : enemyMelee) {
                if (loc == null) break;
                //myRobot.log("Detected enemy at " + loc.x + " " + loc.y + " of type " + loc.type);
                cont++;
                int limit = Constants.rad4Index;
                if (loc.type == Constants.CRUSADER) limit = Constants.rad9Index;
                for (int i = 0; i < limit; ++i) {
                    int newX = loc.x + Constants.X[i], newY = loc.y + Constants.Y[i];
                    int d = utils.distance(myRobot.me.x, myRobot.me.y, newX, newY);
                    if (d <= Constants.visionRange[myRobot.me.unit]) continue;
                    if (!utils.isAccessible(newX, newY)) continue;
                    for (MicroInfo m : microInfoArray) {
                        boolean updateDist = utils.distance(myRobot.me.x, myRobot.me.y, loc.signalOriginX, loc.signalOriginY) <= Constants.CLOSEST_PROPHET_RANGE;
                        if (m.accessible) m.updateMelee(cont, newX, newY, Constants.attack[loc.type], updateDist);
                    }
                }
            }
        }
    }

    boolean isSafe(int i){
        return microInfoArray[i].isEqualOrBetter(bestMicro);
    }

    boolean isSafeStaying(){
        return isSafe(maxMovementIndex);
    }

    boolean isOptimal(int i){
        MicroInfo m = microInfoArray[i];
        return (m.canShoot && m.dmgTaken == 0);
    }

    boolean isOptimalToStay(){
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
        int latestUpdate = -10;
        boolean far = false;

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
            if (minRange > d) minRange = d;
            if (d >= Constants.minRange[myRobot.me.unit] && d <= Constants.dangerRange[myRobot.me.unit]) canShoot = true;
        }

        void updateMelee(int i, int newX, int newY, int atk, boolean updateDist){
            int d = utils.distance(x,y,newX,newY);
            if (latestUpdate != i && d <= Constants.range[Constants.PREACHER]){
                dmgTaken += atk;
                latestUpdate = i;
            }
            if (updateDist && minRange > d) minRange = d;
            if (d >= Constants.minRange[myRobot.me.unit] && d <= Constants.dangerRange[myRobot.me.unit]) canShoot = true;
        }

    }

}
