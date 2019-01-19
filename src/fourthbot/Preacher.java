package fourthbot;

import btcutils.Action;
import btcutils.Robot;

public class Preacher extends Unit {

    Micro micro;
    Utils utils;

    public Preacher(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        micro = new Micro(myRobot, utils, Constants.rad4Index);
    }

    @Override
    public Action turn(){
        utils.update();
        Action attackAction = getAttackAction();
        if (attackAction != null) return attackAction;
        Integer bestAction = micro.getBestIndex();
        if (bestAction != null && bestAction != micro.maxMovementIndex) return myRobot.move(Constants.X[bestAction], Constants.Y[bestAction]);
        return null;
    }

    public Action getAttackAction(){
        Robot bestRobot = null;
        for (Robot r : utils.robotsInVision){
            if (!myRobot.isVisible(r)) continue;
            if (r.team == myRobot.me.team) continue;
            if (!inRange(r)) continue;
            if (bestRobot == null || bestRobot.health < r.health) bestRobot = r;
        }
        if (bestRobot != null) return myRobot.attack(bestRobot.x-myRobot.me.x, bestRobot.y-myRobot.me.y);
        return null;
    }

    boolean inRange(Robot r){
        int d = utils.distance(r.x, r.y, myRobot.me.x, myRobot.me.y);
        if (d > Constants.range[myRobot.me.unit]) return false;
        if (d < Constants.minRange[myRobot.me.unit]) return false;
        return true;
    }

}
