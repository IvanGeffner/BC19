package bc19;


public class DefenseMechanism {

    MyRobot myRobot;
    Utils utils;
    int[] myUnits;
    int[] enemyUnits;

    Micro[] micro;

    final int firstSoldierIndex = 3;

    int roundLastUnit = -100;
    int mageNearby;

    public DefenseMechanism(MyRobot myRobot, Utils utils){
        this.myRobot = myRobot;
        this.utils = utils;
        micro = new Micro[Constants.UNITTYPES - firstSoldierIndex];
        for (int i = 0; i < micro.length; ++i){
            micro[i] = new Micro(myRobot, utils, Constants.rad2Index, i+firstSoldierIndex);
        }
    }

    DefenseMechanismAction defenseAction(boolean safe){
        myUnits = new int[Constants.UNITTYPES];
        enemyUnits = new int[Constants.UNITTYPES];
        int totalUnits = 0, totalEnemies = 0, totalTroops = 0, totalEnemyTroops = 0;
        mageNearby = 0;
        for (Robot r : utils.robotsInVision){
            if (myRobot.isVisible(r)){
                if (r.team != myRobot.me.team){
                    ++enemyUnits[r.unit];
                    ++totalEnemies;
                    if (r.unit != Constants.PILGRIM) ++totalEnemyTroops;
                }
                else{
                    /*TODO this patch is rtded*/
                    if (r.unit > Constants.PILGRIM && utils.distance(myRobot.me.x, myRobot.me.y, r.x, r.y) <= Constants.MAX_DIST_TROOPS){
                        ++totalTroops;
                        ++myUnits[r.unit];
                        ++totalUnits;
                        if (r.unit == Constants.PREACHER) ++mageNearby;
                    }
                }
            }
        }
        if (totalTroops >= Constants.MIN_TROOPS) return null;
        //int type = whichUnitToBuild();
        if (totalEnemies > 0 && totalEnemies == enemyUnits[Constants.PILGRIM] && totalTroops == 0) return buildDefenseUnit(Constants.PROPHET, safe);
        int myMelees = myUnits[Constants.PREACHER] - Constants.DIFF_MELEES;
        if (myMelees < 0) myMelees = 0;
        if (enemyUnits[Constants.CRUSADER]/2 + enemyUnits[Constants.PREACHER] > myMelees) return buildDefenseUnit(Constants.PREACHER, safe);
        int myRangeds = myUnits[Constants.PROPHET] - Constants.DIFF_RANGERS;
        if (myRangeds < 0) myRangeds = 0;
        if (enemyUnits[Constants.PROPHET] < myRangeds) return buildDefenseUnit(Constants.PROPHET, safe);
        return null;
    }

    int whichUnitToBuild(){
        if (mageNearby <= Constants.MAX_MAGES && (enemyUnits[Constants.CRUSADER] + enemyUnits[Constants.PREACHER]) > 0) return Constants.PREACHER;
        return Constants.PROPHET;
    }

    int whichUnitToBuildFree(){
        return Constants.PROPHET;
    }

    boolean isSafe(int type, int index){
        return !micro[type-firstSoldierIndex].oneShot(index);
    }

    DefenseMechanismAction buildDefenseUnit(int type, boolean safe){
        if (!utils.canBuild(type)) return null;
        if (myRobot.me.unit == Constants.CHURCH && !utils.canBuildChurch(type)) return null;
        Integer index = micro[type-firstSoldierIndex].getBestIndex();
        if (index == null) return null;
        if (!safe || isSafe(type, index)) return new DefenseMechanismAction(type, index);
        return null;
    }

    boolean buildUnitRich(){
        int unit = whichUnitToBuildFree();
        if (!utils.canBuild(unit)) return false;
        if (utils.superRich()){
            roundLastUnit = myRobot.me.turn;
            return true;
        }
        if (utils.rich() && roundLastUnit + Constants.MIN_TURNS_RICH <= myRobot.me.turn){
            roundLastUnit = myRobot.me.turn;
            return true;
        }
        return false;
    }

}
