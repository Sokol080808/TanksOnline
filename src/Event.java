import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    static int NOTHING = -2;
    static int TANK_CREATED = 0;
    static int TANK_POSITION = 1;
    static int TANK_DELETED = -1;

    int type = NOTHING;
    ArrayList<Integer> int_data = new ArrayList<>();
    ArrayList<Double> double_data = new ArrayList<>();
    Event() {}
    Event(Event e) {
        type = e.type;
        int_data = new ArrayList<>(e.int_data);
        double_data = new ArrayList<>(e.double_data);
    }
}
