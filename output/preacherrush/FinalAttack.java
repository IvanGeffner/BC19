package bc19;


public class FinalAttack {

    final int MIN_FUEL = 15000;
    final int MIN_TURN = 500;
    final int MIN_CONT = 70;

    final int BROADCAST_DIST = 45;

    MyRobot myRobot;
    Utils utils;
    Symmetry symmetry;
    Broadcast broadcast;
    CastleUtils castleUtils;

    int lastTurnCalles = -10;

    boolean alreadyCalled = false;

    FinalAttack(MyRobot myRobot, CastleUtils castleUtils){
        this.myRobot = myRobot;
        this.castleUtils = castleUtils;
        this.utils = castleUtils.utils;
        symmetry = new Symmetry(myRobot, utils);
        this.broadcast =  castleUtils.broadcast;
    }

    FinalAttack(MyRobot myRobot, Utils utils, Broadcast broadcast){
        this.myRobot = myRobot;
        this.utils = utils;
        this.broadcast = broadcast;
        this.symmetry = new Symmetry(myRobot, utils);
    }

    Location getFinalAttackLocation(){
        int minDist = Constants.INF;
        Location myLoc = new Location(myRobot.me.x, myRobot.me.y);
        Location bestLoc = null;
        for (Robot r : utils.robotsInVision){
            if (broadcast.canReadSignal(r)){
                int mes = r.signal/(2* Constants.maxMapSize* Constants.maxMapSize);
                if (mes != broadcast.FINAL_ATTACK) continue;
                int xObj = (r.signal/ Constants.maxMapSize)% Constants.maxMapSize;
                int yObj = r.signal% Constants.maxMapSize;
                Location loc = new Location(xObj, yObj);
                //if (myRobot.me.unit != Constants.CASTLE && myRobot.me.unit != Constants.CRUSADER && utils.distance(myRobot.me.x, myRobot.me.y, r.x, r.y) > BROADCAST_DIST) continue;
                if (bestLoc == null || utils.distance(myLoc, loc) < minDist){
                    minDist = utils.distance(myLoc, loc);
                    bestLoc = loc;
                }
            }
        }
        return bestLoc;
    }


    void checkFinalAttack(){
        if (myRobot.me.turn - lastTurnCalles < 6) return;
        lastTurnCalles = myRobot.me.turn;
        broadcast.sendTarget(mySymmetric(), broadcast.FINAL_ATTACK, Constants.MAX_RANGE);
    }

    Location mySymmetric(){
        return symmetry.getSymmetric(castleUtils.myLocation);
    }

}
