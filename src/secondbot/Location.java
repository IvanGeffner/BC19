package secondbot;


public class Location {
    int x, y;
    public Location(int x, int y){
        this.x = x; this.y = y;
    }

    public Location add(int i){
        return new Location(x + Utils.X[i], y + Utils.Y[i]);
    }

    public Location add (int dx, int dy){
        return new Location(x + dx, y + dy);
    }

}
