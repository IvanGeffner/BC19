package bc19;

public class CastleCommunication {

    MyRobot myRobot;
    int round = -1;

    final int messageBits = 4;
    final int locBits = 64;

    public CastleCommunication(MyRobot myRobot){
        this.myRobot = myRobot;
        readRound();
    }

    void update(){
        if (round == 1) round = 0;
        else if (round == 0) round = 1;
        if (round == -1){
            readRound();
            //switch sign
            if (round != -1) update();
        }
    }

    void readRound(){
        Robot[] visibleRobots = myRobot.getVisibleRobots();
        for (Robot r : visibleRobots){
            if (!myRobot.isVisible(r)) continue;
            if (r.team == myRobot.me.team && r.unit == Constants.CASTLE || r.unit == Constants.CHURCH){
                if (myRobot.isRadioing(r)){
                    round = (r.signal/Constants.LocationSize)%2;
                    break;
                }
            }
        }
    }

    void sendCastleMessage(int message, Location target){
        if (target == null) return;
        int mes = (message%messageBits)*locBits;
        if (round%2 == 0) mes += target.x;
        else mes += target.y;
        myRobot.castleTalk(mes+1);
    }
}
