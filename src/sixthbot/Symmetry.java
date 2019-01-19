package sixthbot;

public class Symmetry {

    MyRobot myRobot;
    Utils utils;

    boolean xSym = true, ySym = true;

    public Symmetry(MyRobot myRobot, Utils utils){
        this.myRobot = myRobot;
        this.utils = utils;
        checkMaps();
    }

    void checkMaps(){
        utils.update();
        for (int i = 0; i < utils.dimX; ++i){
            for (int j = 0; j < utils.dimY; ++j){
                if (j < (utils.dimY-1)/2) {
                    if (myRobot.karboniteMap[j][i] != myRobot.karboniteMap[utils.dimY - j - 1][i]) {
                        ySym = false;
                        return;
                    }
                    if (myRobot.fuelMap[j][i] != myRobot.fuelMap[utils.dimY - j - 1][i]) {
                        ySym = false;
                        return;
                    }
                    if (myRobot.map[j][i] != myRobot.map[utils.dimY - j - 1][i]) {
                        ySym = false;
                        return;
                    }
                }
                if (i < (utils.dimX-1)/2) {
                    if (myRobot.karboniteMap[j][i] != myRobot.karboniteMap[j][utils.dimX - 1 - i]) {
                        xSym = false;
                        return;
                    }
                    if (myRobot.fuelMap[j][i] != myRobot.fuelMap[j][utils.dimX - 1 - i]) {
                        xSym = false;
                        return;
                    }
                    if (myRobot.map[j][i] != myRobot.map[j][utils.dimX - 1 - i]) {
                        xSym = false;
                        return;
                    }
                }
            }
        }
    }

    Location getSymmetric(Location loc){
        if (xSym){
            return new Location(utils.dimX - 1 - loc.x, loc.y);
        }
        return new Location(loc.x, utils.dimY - 1 - loc.y);
    }

    Location getSymmetric(int x, int y){
        if (xSym){
            return new Location(utils.dimX - 1 - x, y);
        }
        return new Location(x, utils.dimY - 1 - y);
    }

    /*TODO*/
    void checkSymmetry(){
        if (!xSym || !ySym) return;
    }



}
