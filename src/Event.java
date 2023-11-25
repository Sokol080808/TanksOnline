import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    static int TANK_POSITION = 1;

    int type = -1;
    ArrayList<Integer> int_data = new ArrayList<>();
    ArrayList<Double> double_data = new ArrayList<>();
    Event() {}
}
