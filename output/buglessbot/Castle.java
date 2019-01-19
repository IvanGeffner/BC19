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
        castleUtils.update();
        myRobot.log("Turn: " + myRobot.me.turn);
        myRobot.log("Karbonite: " + myRobot.karbonite);
        myRobot.log("fuel: " + myRobot.fuel);
        myRobot.log("My location is: " + castleUtils.myLocation.x + " " + castleUtils.myLocation.y);
        if (objective < castleUtils.objectives.length){
            myRobot.log("Sending pilgrim to " + objective + ", located at: " + castleUtils.objectives[objective].x + " " + castleUtils.objectives[objective].y);
            myRobot.log("Direction: " + castleUtils.objectives[objective].dir);
            castleUtils.createPilgrim(objective);
            ++objective;
        }
        return castleUtils.nextTurnAction;
    }

}
