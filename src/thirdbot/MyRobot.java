package thirdbot;

import btcutils.Action;
import btcutils.BCAbstractRobot;

public class MyRobot extends BCAbstractRobot {

    Unit unit = null;

    public Action turn() {
        if (unit == null) {
            switch (me.unit){
                case Constants.CASTLE:
                    unit = new Castle(this);
                    break;
                case Constants.CHURCH:
                    unit = new Church(this);
                    break;
                case Constants.PILGRIM:
                    unit = new Pilgrim(this);
                    break;
                case Constants.CRUSADER:
                    unit = new Crusader(this);
                    break;
                case Constants.PROPHET:
                    unit = new Prophet(this);
                    break;
                case Constants.PREACHER:
                    unit = new Preacher(this);
                    break;
            }
        }
        return unit.turn();
    }
}
