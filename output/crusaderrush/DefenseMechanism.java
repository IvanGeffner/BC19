package bc19;


public class DefenseMechanism {

    MyRobot myRobot;
    Utils utils;
    int[] myUnits;
    int[] enemyUnits;

    Micro[] micro;

    final int firstSoldierIndex = 3;

    int roundLastUnit = -100;

    public DefenseMechanism(MyRobot myRobot, Utils utils){
        this.myRobot = myRobot;
        this.utils = utils;
        micro = new Micro[Constants.UNITTYPES - firstSoldierIndex];
        for (int i = 0; i < micro.length; ++i){
            micro[i] = new Micro(myRobot, utils, Constants.rad2Index, i+firstSoldierIndex);
        }
    }

    Integer defenseAction(){
        myUnits = new int[Constants.UNITTYPES];
        enemyUnits = new int[Constants.UNITTYPES];
        int totalUnits = 0, totalEnemies = 0, totalTroops = 0, totalEnemyTroops = 0;
        for (Robot r : utils.robotsInVision){
            if (myRobot.isVisible(r)){
                if (r.team != myRobot.me.team){
                    ++enemyUnits[r.unit];
                    ++totalEnemies;
                    if (r.unit != Constants.PILGRIM) ++totalEnemyTroops;
                }
                else{
                    ++myUnits[r.unit];
                    ++totalUnits;
                    if (r.unit > Constants.PILGRIM) ++totalTroops;
                }
            }
        }
        if ((totalEnemies > 0 && totalEnemies == enemyUnits[Constants.PILGRIM] && totalTroops == 0)|| totalEnemyTroops > 0){
            return buildDefenseUnit();
        }
        return null;
    }

    int whichUnitToBuild(){
        return Constants.CRUSADER;
    }

    Integer buildDefenseUnit(){
        int unitToBuild = whichUnitToBuild();
        if (!utils.canBuild(unitToBuild)) return null;
        Integer index = micro[unitToBuild-firstSoldierIndex].getBestIndex();
        return index;
    }

    Integer buildUnitRich(){
        if (utils.superRich()){
            roundLastUnit = myRobot.me.turn;
            return buildDefenseUnit();
        }
        if (utils.rich() && roundLastUnit + Constants.MIN_TURNS_RICH <= myRobot.me.turn){
            roundLastUnit = myRobot.me.turn;
            return buildDefenseUnit();
        }
        return null;
    }

}
