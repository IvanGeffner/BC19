package eigthbot;

import btcutils.Robot;

public class Attacker {

    MyRobot myRobot;
    Utils utils;
    Broadcast broadcast;

    int idOfLastUnitAttacked;
    int[] attacks;
    int turnLastUnitAttacked = -10;

    public Attacker(MyRobot myRobot, Utils utils, Broadcast broadcast){
        this.myRobot = myRobot;
        this.utils = utils;
        this.broadcast = broadcast;
    }

    Location tryAttack(){
        attacks = broadcast.readCastleAttack();
        Robot bestRobot = null;
        for (Robot r : utils.robotsInVision){
            if (!myRobot.isVisible(r)) continue;
            if (r.team == myRobot.me.team) continue;
            if (!inRange(r)) continue;
            if (isBetter(r, bestRobot)) bestRobot = r;
        }
        if (bestRobot != null){
            idOfLastUnitAttacked = bestRobot.id;
            turnLastUnitAttacked = myRobot.me.turn;
            return new Location(bestRobot.x-myRobot.me.x, bestRobot.y-myRobot.me.y);
        }
        return null;
    }

    Location tryAttackPreacher(){
        PreacherLoc bestLoc = null;
        MeleeUnit[] melee = broadcast.getMelee();
        for (Robot r : utils.robotsInVision) {
            if (!myRobot.isVisible(r)) continue;
            if (r.team == myRobot.me.team) continue;
            if (inRange(r)) {
                PreacherLoc neutralLoc = new PreacherLoc(r.x, r.y);
                if (neutralLoc.isBetterThan(bestLoc)) bestLoc = neutralLoc;
            }
            for (int i = r.x-1; i <= r.x +1; ++i) {
                for (int j = r.y-1 ; j <= r.y+1; ++j){
                    if (utils.isInMap(i,j) && inRange(i,j)){
                        PreacherLoc loc = new PreacherLoc(i,j);
                        if (loc.isBetterThan(bestLoc)) bestLoc = loc;
                    }
                }
            }
        }
        for (MeleeUnit loc : melee){
            if (loc == null) break;
            int d = utils.distance(loc.x, loc.y, myRobot.me.x, myRobot.me.y);
            if (d <= Constants.visionRange[myRobot.me.unit]) continue;
            for (int i = loc.x-1; i <= loc.x +1; ++i) {
                for (int j = loc.y-1 ; j <= loc.y+1; ++j){
                    if (utils.isInMap(i,j) && inRange(i,j)){
                        PreacherLoc ploc = new PreacherLoc(i,j);
                        ploc.enemiesHit++;
                        if (ploc.isBetterThan(bestLoc)) bestLoc = ploc;
                    }
                }
            }
        }
        if (bestLoc == null) return null;
        return new Location(bestLoc.x-myRobot.me.x, bestLoc.y-myRobot.me.y);
    }

    boolean isAttacked(int id){
        for (int attackID : attacks){
            if (attackID == 0) return false;
            if (attackID == id) return true;
        }
        return false;
    }

    boolean isBetter (Robot a, Robot b){
        if (b == null) return true;
        if (isBetterType(a,b)) return true;
        if (isBetterType(b,a)) return false;
        if (myRobot.me.turn - turnLastUnitAttacked <= 1) {
            if (a.id == idOfLastUnitAttacked) return true;
            if (b.id == idOfLastUnitAttacked) return false;
        }
        boolean isAttackedA = isAttacked(a.id), isAttackedB = isAttacked(b.id);
        if (isAttackedA && !isAttackedB) return true;
        if (isAttackedB && !isAttackedA) return false;
        return utils.distance(myRobot.me.x, myRobot.me.y, a.x, a.y) < utils.distance(myRobot.me.x, myRobot.me.y, b.x, b.y);
    }

    int value(int type){
        switch(type){
            case Constants.CHURCH:
                return 0;
            case Constants.CASTLE:
                return 1;
            case Constants.PILGRIM:
                return 2;
            case Constants.CRUSADER:
                return 3;
            case Constants.PREACHER:
                return 4;
            case Constants.PROPHET:
                return 5;
        }
        return -1;
    }

    boolean isBetterType(Robot a, Robot b){
        return value(a.unit) > value(b.unit);
    }

    boolean inRange(Robot r){
        int d = utils.distance(r.x, r.y, myRobot.me.x, myRobot.me.y);
        if (d > Constants.range[myRobot.me.unit]) return false;
        if (d < Constants.minRange[myRobot.me.unit]) return false;
        return true;
    }

    boolean inRange(int x, int y){
        int d = utils.distance(x, y, myRobot.me.x, myRobot.me.y);
        if (d > Constants.range[myRobot.me.unit]) return false;
        if (d < Constants.minRange[myRobot.me.unit]) return false;
        return true;
    }

    class PreacherLoc{
        int enemiesHit, alliesHit, x, y;

        PreacherLoc(int x, int y){
            this.x = x;
            this.y = y;
            generate();
        }

        void generate(){
            for (int i = x-1; i <= x+1; ++i){
                for (int j = y-1; j <= y+1; ++j){
                    if (!utils.isInMap(i,j)) continue;
                    int id = utils.robotMap[j][i];
                    if (id <= 0) continue;
                    Robot r = myRobot.getRobot(id);
                    int val = 5;
                    if (r.unit == Constants.PROPHET) val*=2;
                    if (r.team == myRobot.me.team) alliesHit += val;
                    else enemiesHit += val;
                }
            }
        }

        int value (){
            return enemiesHit - alliesHit;
        }

        boolean isBetterThan(PreacherLoc loc){
            if (value() <= 0) return false;
            if (loc == null) return true;
            return value() >= loc.value();
        }
    }

}
