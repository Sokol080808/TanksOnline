import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Bullet {
    double x, y, alpha;
    Bullet() {
        x = 0;
        y = 0;
        alpha = 0;
    }

    Bullet(double x, double y, double alpha) {
        this.x = x;
        this.y = y;
        this.alpha = alpha;
    }

    void paint(Graphics g, BufferedImage bullet_image) {
        g.drawImage(bullet_image, (int)x, (int)y, null);
    }
}
