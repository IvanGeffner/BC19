package fourthbot;
import btcutils.Action;

public class Church extends Unit {

    DefenseMechanism defenseMechanism;
    Utils utils;


    public Church(MyRobot myRobot){
        super(myRobot);
        utils = new Utils(myRobot);
        defenseMechanism = new DefenseMechanism(myRobot, utils);
    }

    @Override
    public Action turn(){
        utils.update();
        Action act = defenseMechanism.defenseAction();
        if(act != null) return act;
        if (rich()){
            return tryBuildProphet();
        }
        return null;
    }

    boolean rich(){
        if (myRobot.karbonite < Constants.RICH_KARBO) return false;
        if (myRobot.fuel < Constants.RICH_FUEL) return false;
        return true;
    }

    Action tryBuildProphet(){
        for (int i = 0; i < Constants.rad2Index; ++i){
            if (utils.isEmptySpace(Constants.X[i], Constants.Y[i])) return myRobot.buildUnit(Constants.PROPHET, Constants.X[i],Constants.Y[i]);
        }
        return null;
    }

}
