package seventhbot;

import btcutils.Robot;

public class Broadcast {

    final int message_bits = 8;
    final int target_bits = 64*64;
    final int partial_target_bits = 64;

    final int PILGRIM_MINING = 0;
    final int PILGRIM_AGGRO = 1;
    final int PROPHET_AGGRO = 2;
    final int ENEMY_SEEN = 3;
    final int CHURCH_MSG = 4;
    final int FINAL_ATTACK = 5;
    final int ATK_ID = 6;
    final int CLOSEST_PROPHET = 7;

    MyRobot myRobot;
    int parity = -1;
    Utils utils;

    int turnBroadcast = -1;
    int messageToBroadcast;
    int radToBroadcast;

    Broadcast(MyRobot myRobot, Utils utils){
        this.myRobot = myRobot;
        this.utils = utils;
        this.utils.update();
        this.parity = readParity();
    }

    Broadcast(MyRobot myRobot, Utils utils, int parity){
        this.myRobot = myRobot;
        this.parity = parity;
        this.utils = utils;
    }

    boolean canReadSignal(Robot r){
        if (!myRobot.isRadioing(r)) return false;
        return utils.distance(myRobot.me.x, myRobot.me.y, r.x, r.y) <= r.signal_radius;
    }

    int readParity(){
        utils.update();
        for (Robot r : utils.robotsInVision){
            if (r.id == myRobot.me.id) continue;
            if (canReadSignal(r)){
                int par = ((r.signal / target_bits) % 2);
                if (r.unit != Constants.CASTLE) {
                    if (par == myRobot.me.turn) return 0;
                    return 1;
                }
                if (par == myRobot.me.turn) return 1;
                return 0;
            }
        }
        return -1;
    }

    void sendTarget(Location target, int message, int rad){
        if (parity == -1) readParity();
        message %= 8;
        message = message*2 + (myRobot.me.turn + parity)%2;
        int mes = message*target_bits + target.x* Constants.maxMapSize + target.y;
        messageToBroadcast = mes;
        radToBroadcast = rad;
        turnBroadcast = myRobot.me.turn;
        //myRobot.signal(mes, rad);
    }

    void sendAttackID(int id, int rad){
        if (parity == -1) readParity();
        int mes = ATK_ID*2 + (myRobot.me.turn + parity)%2;;
        mes = mes*target_bits + id;
        messageToBroadcast = mes;
        radToBroadcast = rad;
        turnBroadcast = myRobot.me.turn;
    }

    int[] readCastleAttack(){
        int[] ans = new int[Constants.MAX_ATTACK_MEMORY];
        int cont = 0;
        for (Robot r : utils.robotsInVision){
            if (cont >= ans.length) break;
            if (r.id == myRobot.me.id) continue;
            if (canReadSignal(r)){
                int mes = r.signal/(2*message_bits);
                if (mes != ATK_ID) continue;
                ans[cont] = mes%target_bits;
                ++cont;
            }
        }
        return ans;
    }

    void sendCastleMessage(int message, Location target){
        if (target == null) return;
        int mes = (message%message_bits)* Constants.maxMapSize;
        if ((myRobot.me.turn + parity)%2 == 0) mes += target.x;
        else mes += target.y;
        myRobot.castleTalk(mes+1);
    }

    void reset(){
        turnBroadcast = -1;
    }

    void broadcast(){
        if (myRobot.me.turn != turnBroadcast) return;
        myRobot.signal(messageToBroadcast, radToBroadcast);
    }

    boolean inUse(){
        return (myRobot.me.turn == turnBroadcast);
    }

    void broadcastClosestProphet(){
        if (myRobot.fuel < Constants.SAFETY_FUEL) return;
        Robot closestRobot = null;
        int shortestDist = 0;
        Location[] rangers = getRangers();
        boolean found = false;
        for (Robot r : utils.robotsInVision){
            if (!myRobot.isVisible(r)) continue;
            if (r.unit != Constants.PROPHET && r.unit != Constants.CASTLE) continue;
            if (r.team == myRobot.me.team){
                int d = utils.distance(myRobot.me.x, myRobot.me.y, r.x,r.y);
                if (d <= Constants.CLOSEST_PROPHET_RANGE) found = true;
                continue;
            }
            if (alreadySent(r, rangers)) continue;
            int d = utils.distance(myRobot.me.x, myRobot.me.y, r.x,r.y);
            if (closestRobot == null || shortestDist > d){
                shortestDist = d;
                closestRobot = r;
            }
        }
        if (found && closestRobot != null) sendClosestProphet(closestRobot);
    }

    boolean alreadySent(Robot r, Location[] locs){
        for (Location loc : locs){
            if (loc == null) return false;
            if (r.x == loc.x && r.y == loc.y) return true;
        }
        return false;
    }


    void sendClosestProphet(Robot prophet){
        int mes = CLOSEST_PROPHET*2 + (myRobot.me.turn + parity)%2;
        mes = mes*target_bits + prophet.x* Constants.maxMapSize + prophet.y;
        /*for (Robot r : utils.robotsInVision){
            if (!myRobot.isVisible(r)) continue;
            if (myRobot.me.team != r.team) continue;
            int d = utils.distance(myRobot.me.x, myRobot.me.y, r.x, r.y);
            if (d > Constants.MIN_RANGE_REPEATED) continue;
            if (myRobot.isRadioing(r) && d <= r.signal_radius) {
                if (r.signal == mes) return;
            }
        }*/
        //myRobot.log("I'm at " + myRobot.me.x + " " + myRobot.me.y + " Sending location " + prophet.x + " " + prophet.y);
        messageToBroadcast = mes;
        radToBroadcast = Constants.CLOSEST_PROPHET_RANGE;
        turnBroadcast = myRobot.me.turn;
    }

    Location[] getRangers(){
        Location[] ans = new Location[Constants.MAX_RANGERS];
        int cont = 0;
        for (Robot r : utils.robotsInVision){
            if (cont >= ans.length) break;
            if (!myRobot.isVisible(r)) continue;
            if (myRobot.me.team != r.team) continue;
            if (r.id == myRobot.me.id) continue;
            if (canReadSignal(r)){
                int mes = r.signal/(2*target_bits);
                if (mes != CLOSEST_PROPHET) continue;
                int y = r.signal%Constants.maxMapSize;
                int x = (r.signal/Constants.maxMapSize)%Constants.maxMapSize;
                ans[cont] = new Location(x,y);
                ++cont;
            }
        }
        return ans;
    }

}
