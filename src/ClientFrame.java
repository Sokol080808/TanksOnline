import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ClientFrame extends JFrame implements KeyEventDispatcher {
    ObjectOutputStream out;

    int X_CNT = 20, Y_CNT = 10, WALL_SIZE = 90;
    int V = 2;
    double Va = 2 * Math.PI / 150;
    Map map;
    BufferedImage wall, tank_image;

    boolean isPressedW = false, isPressedA = false, isPressedS = false, isPressedD = false;

    int id;
    ArrayList<Tank> tanks = new ArrayList<>();

    ClientFrame(int id, Map map, ObjectOutputStream out) throws IOException {
        this.id = id;
        this.out = out;
        this.map = map;
        wall = ImageIO.read(new File("data\\wall.jpg"));
        tank_image = ImageIO.read(new File("data\\tank.png"));

        tanks.add(new Tank(500, 500, 0));

        this.setUndecorated(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    void update() throws IOException {
        Tank cur = tanks.get(id);
        if (isPressedA) {
            cur.alpha -= Va;
        }
        if (isPressedD) {
            cur.alpha += Va;
        }
        if (isPressedW) {
            cur.x += V * Math.cos(cur.alpha);
            cur.y += V * Math.sin(cur.alpha);
        }
        if (isPressedS) {
            cur.x -= V * Math.cos(cur.alpha);
            cur.y -= V * Math.sin(cur.alpha);
        }

        Event e = new Event();
        e.type = Event.TANK_POSITION;
        e.int_data.add(id);
        e.double_data.add(cur.x);
        e.double_data.add(cur.y);
        e.double_data.add(cur.alpha);
        out.writeObject(e);
    }

    @Override
    public void paint(Graphics g) {
        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();
        }
        g = bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, getWidth(), getHeight());

        map.paint(g, wall);
        for (Tank tank : tanks) tank.paint(g, tank_image);
        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }

        g.dispose();
        bufferStrategy.show();
    }

    void update_event(Event e) {
        if (e.type == Event.TANK_POSITION) {
            int id = e.int_data.get(0);
            tanks.get(id).x = e.double_data.get(0);
            tanks.get(id).y = e.double_data.get(1);
            tanks.get(id).alpha = e.double_data.get(2);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedW = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedW = false;
            }
        }
        if (e.getKeyChar() == 'a') {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedA = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedA = false;
            }
        }
        if (e.getKeyChar() == 's') {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedS = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedS = false;
            }
        }
        if (e.getKeyChar() == 'd') {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedD = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedD = false;
            }
        }
        return false;
    }
}
