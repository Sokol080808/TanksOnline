import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;

public class MapEditorFrame extends JFrame implements MouseListener, MouseMotionListener {
    int X_CNT = 20, Y_CNT = 10, WALL_SIZE = 90;
    int mouse_x, mouse_y;
    Map map;
    BufferedImage wall;


    MapEditorFrame() throws IOException {
        addMouseListener(this);
        addMouseMotionListener(this);

        map = new Map(X_CNT, Y_CNT);
        wall = ImageIO.read(new File("data\\wall.jpg"));

        this.setUndecorated(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    int[] get_square(int x, int y) {
        int[] pos = new int[2];
        pos[0] = x / WALL_SIZE;
        pos[1] = y / WALL_SIZE;
        if (pos[0] >= X_CNT || pos[1] >= Y_CNT) {
            pos[0] = pos[1] = -1;
        }
        return pos;
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

        int[] pos = get_square(mouse_x, mouse_y);
        if (pos[0] != -1) {
            g.setColor(new Color(255, 0, 0));
            g.drawRect(pos[0] * WALL_SIZE, pos[1] * WALL_SIZE, WALL_SIZE - 1, WALL_SIZE - 1);
            g.setColor(new Color(0, 0, 0));
        }

        g.dispose();
        bufferStrategy.show();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int[] pos = get_square(e.getX(), e.getY());
        if (pos[0] != -1) {
            map.update(pos[0], pos[1]);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouse_x = 100000000;
        mouse_y = 100000000;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }
}
