import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientFrame extends JFrame implements KeyEventDispatcher {
    int MAX_PLAYER_CNT = 4;
    Socket connection;
    ObjectOutputStream out;

    int X_CNT = 20, Y_CNT = 10, WALL_SIZE = 90;
    int V = 2;
    double Va = 2 * Math.PI / 150;
    Map map;
    BufferedImage wall, tank_image, bullet_image;

    boolean isPressedW = false, isPressedA = false, isPressedS = false, isPressedD = false, isPressedSpace = false;

    int id;
    Tank[] tanks = new Tank[MAX_PLAYER_CNT];
    ArrayList<Bullet> bullets = new ArrayList<>();

    long last_shot = 0;

    ClientFrame(int id, Map map, Socket connection, ObjectOutputStream out) throws IOException {
        this.id = id;
        this.out = out;
        this.connection = connection;
        this.map = map;
        wall = ImageIO.read(new File("data\\wall.jpg"));
        tank_image = ImageIO.read(new File("data\\tank.png"));
        bullet_image = ImageIO.read(new File("data\\bullet.png"));

//        this.setUndecorated(true);
//        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(1000, 1000);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    void update() throws IOException {
        Tank cur = tanks[id];
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

        for (Bullet bullet : bullets) {
            bullet.x += 5 * V * Math.cos(bullet.alpha);
            bullet.y += 5 * V * Math.sin(bullet.alpha);
        }

        if (isPressedSpace) {
            long cur_time = System.currentTimeMillis();
            if (cur_time - last_shot >= 250) {
                last_shot = cur_time;
                double bx = cur.x + tank_image.getWidth() / 2.0 - bullet_image.getWidth() / 2.0;
                double by = cur.y + tank_image.getHeight() / 2.0 - bullet_image.getHeight() / 2.0;
                bullets.add(new Bullet(bx, by, cur.alpha));
                Thread thread = new Thread(() -> {
                    Event ev = new Event();
                    ev.type = Event.BULLET_CREATED;

                    ev.double_data.add(bx);
                    ev.double_data.add(by);
                    ev.double_data.add(cur.alpha);
                    synchronized (out) {
                        try {
                            out.writeObject(ev);
                            out.flush();
                        } catch (IOException se) {
                            System.out.println("BAD");
                        }
                    }
                });
                thread.start();
            }
        }

        Thread thread = new Thread(() -> {
            Event e = new Event();
            e.type = Event.TANK_POSITION;
            e.int_data.add(id);
            e.double_data.add(cur.x);
            e.double_data.add(cur.y);
            e.double_data.add(cur.alpha);
            synchronized (out) {
                try {
                    out.writeObject(e);
                    out.flush();
                } catch (IOException se) {
                    System.out.println("BAD");
                }
            }
        });
        thread.start();
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
        for (Bullet bullet : bullets) bullet.paint(g, bullet_image);
        for (Tank tank : tanks) {
            if (tank == null) continue;
            tank.paint(g, tank_image);
        }

        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }

        g.dispose();
        bufferStrategy.show();
    }

    void update_event(Event e) {
        if (e.type == Event.TANK_CREATED) {
            int t_id = e.int_data.get(0);
            tanks[t_id] = new Tank(e.double_data.get(0), e.double_data.get(1), e.double_data.get(2));
        }
        if (e.type == Event.TANK_POSITION) {
            int t_id = e.int_data.get(0);
            if (tanks[t_id] == null) tanks[t_id] = new Tank();
            tanks[t_id].x = e.double_data.get(0);
            tanks[t_id].y = e.double_data.get(1);
            tanks[t_id].alpha = e.double_data.get(2);
        }
        if (e.type == Event.TANK_DELETED) {
            int t_id = e.int_data.get(0);
            tanks[t_id] = null;
        }
        if (e.type == Event.BULLET_CREATED) {
            bullets.add(new Bullet(e.double_data.get(0), e.double_data.get(1), e.double_data.get(2)));
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == 87) {    // W
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedW = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedW = false;
            }
        }
        if (e.getKeyCode() == 65) {    // A
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedA = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedA = false;
            }
        }
        if (e.getKeyCode() == 83) {    // S
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedS = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedS = false;
            }
        }
        if (e.getKeyCode() == 68) {    // D
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedD = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedD = false;
            }
        }
        if (e.getKeyCode() == 32) {    // SPACE
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedSpace = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedSpace = false;
            }
        }
        System.out.println(e.getKeyCode());
        return false;
    }
}
