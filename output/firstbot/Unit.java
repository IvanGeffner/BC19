package bc19;

public abstract class Unit {

    MyRobot myRobot;

    public Unit (MyRobot myRobot){
        this.myRobot = myRobot;
    }

    public abstract Action turn();

}
