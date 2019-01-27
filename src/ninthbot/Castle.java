package ninthbot;
import btcutils.Action;

public class Castle extends Unit {

    public CastleUtils castleUtils;
    int objective;

    public Castle (MyRobot myRobot){
        super(myRobot);
        castleUtils = new CastleUtils(myRobot);
        objective = 0;
    }

    @Override
    public Action turn(){
        if (myRobot.me.turn%50 == 0) myRobot.log(myRobot.me.turn + " ");
        castleUtils.update();
        castleUtils.checkDefense();
        castleUtils.checkAttack();
        castleUtils.checkPilgrim();
        castleUtils.checkFinalAttack();
        castleUtils.checkFreeBuild();
        castleUtils.broadcast.broadcastClosestUnit();
        castleUtils.broadcast.broadcast();
        return castleUtils.nextTurnAction;
    }
}
