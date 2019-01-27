package tenthbot;

public class DefenseMechanismAction {

    int type, x,y,dir;

    public DefenseMechanismAction(int type, int dir){
        this.type = type;
        this.x = Constants.X[dir];
        this.y = Constants.Y[dir];
        this.dir = dir;
    }

}
