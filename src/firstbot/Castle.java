package firstbot;
import btcutils.*;

public class Castle extends Unit {

    public CastleUtils castleUtils;
    Utils utils;
    int objective = 0;

    public Castle (MyRobot myRobot){
        super(myRobot);
        castleUtils = new CastleUtils(myRobot);
    }

    @Override
    public Action turn(){
        castleUtils.update();
        myRobot.log("Turn: " + myRobot.me.turn);
        myRobot.log("Karbonite: " + myRobot.karbonite);
        myRobot.log("fuel: " + myRobot.fuel);
        if (objective < castleUtils.objectives.length) castleUtils.createPilgrim(objective);
        return castleUtils.nextTurnAction;
    }

}
