package fifthbot;

import btcutils.Action;
import btcutils.Robot;

public class Attacker {

    MyRobot myRobot;
    Utils utils;

    public Attacker(MyRobot myRobot, Utils utils){
        this.myRobot = myRobot;
        this.utils = utils;
    }

    Action tryAttack(){
        Robot bestRobot = null;
        for (Robot r : utils.robotsInVision){
            if (!myRobot.isVisible(r)) continue;
            if (r.team == myRobot.me.team) continue;
            if (!inRange(r)) continue;
            if (isBetter(r, bestRobot)) bestRobot = r;
        }
        if (bestRobot != null){
            return myRobot.attack(bestRobot.x-myRobot.me.x, bestRobot.y-myRobot.me.y);
        }
        return null;
    }

    boolean isBetter (Robot a, Robot b){
        if (b == null) return true;
        if (isBetterType(a,b)) return true;
        if (isBetterType(b,a)) return false;
        return a.health < b.health;
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

}
