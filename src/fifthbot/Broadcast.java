package fifthbot;

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
                int mes = r.signal/(2*message_bits);
                //if (mes != PROPHET_AGGRO) continue;
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
        int mes = message*target_bits + target.x*Constants.maxMapSize + target.y;
        messageToBroadcast = mes;
        radToBroadcast = rad;
        turnBroadcast = myRobot.me.turn;
        //myRobot.signal(mes, rad);
    }

    void sendCastleMessage(int message, Location target){
        if (target == null) return;
        int mes = (message%message_bits)*Constants.maxMapSize;
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
}
