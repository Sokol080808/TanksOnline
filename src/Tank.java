import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Tank implements Serializable {
    double x, y, alpha;
    Tank() {
        x = y = 500;
        alpha = Math.PI / 2;
    }
    Tank(int x, int y, double alpha) {
        this.x = x;
        this.y = y;
        this.alpha = alpha;
    }

    void paint(Graphics g, BufferedImage tank_image) {
        double locationX = tank_image.getWidth() / 2;
        double locationY = tank_image.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(alpha - Math.PI / 2, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(op.filter(tank_image, null), (int)x, (int)y, null);
    }
}
