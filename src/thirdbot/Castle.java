package thirdbot;
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
        castleUtils.update();
        Integer objective = castleUtils.shouldBuildPilgrim();
        if (objective != null) castleUtils.createPilgrim(objective);
        return castleUtils.nextTurnAction;
    }

}
