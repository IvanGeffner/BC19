package bc19;

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
        //if (myRobot.me.turn%50 == 0) myRobot.log(myRobot.me.turn + " ");
        castleUtils.update();
        castleUtils.checkAttack();
        castleUtils.checkPilgrim();
        castleUtils.checkFinalAttack();
        castleUtils.checkBuildPreacher();
        castleUtils.checkPreachers();
        castleUtils.broadcast.broadcast();
        return castleUtils.nextTurnAction;
    }
}
