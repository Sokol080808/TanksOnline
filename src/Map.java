import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Map implements Serializable {
    int X_SIZE, Y_SIZE;
    boolean[][] map;
    int WALL_SIZE = 90;

    Map(int X_SIZE, int Y_SIZE) throws IOException {
        this.X_SIZE = X_SIZE;
        this.Y_SIZE = Y_SIZE;
        map = new boolean[X_SIZE][Y_SIZE];
    }

    Map(boolean[][] map) throws IOException {
        X_SIZE = map.length;
        Y_SIZE = map[0].length;
        this.map = map;
    }

    void paint(Graphics g, BufferedImage wall) {
        for (int i = 0; i < X_SIZE; i++) {
            for (int j = 0; j < Y_SIZE; j++) {
                if (map[i][j]) {
                    g.drawImage(wall, WALL_SIZE * i, WALL_SIZE * j, null);
                }
            }
        }
    }

    void update(int x, int y) {
        assert(0 <= x && x < X_SIZE);
        assert(0 <= y && y < Y_SIZE);
        map[x][y] ^= true;
    }

    static void save(Map map, String file_name) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data\\" + file_name + ".data"));
        out.writeObject(map);
        out.close();
    }

    static Map open(String file_name) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("data\\" + file_name + ".data"));
            Map res = (Map) in.readObject();
            in.close();
            return res;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
