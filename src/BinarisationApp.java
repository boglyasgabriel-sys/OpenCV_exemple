import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BinarisationApp {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    
    public static BufferedImage matToBufferedImage(Mat original) {
        int width = original.width();
        int height = original.height();
        int channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);
        int type = (channels == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(width, height, type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        return image;
    }

    public static void main(String[] args) {
        String imagePath = "src/image_opencv.jpg";

        // 1. Lecture de l'image d'origine
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Impossible de charger l'image : " + imagePath);
            return;
        }

        // 2. Conversion en niveaux de gris
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // 3. Binarisation (Thresholding)
        Mat binarized = new Mat();
        // Valeur de seuil fixée à 125 (tous les pixels > 125 deviennent 255/blanc, sinon 0/noir)
        Imgproc.threshold(gray, binarized, 125, 255, Imgproc.THRESH_BINARY);

        // 4. Conversion des Mat en BufferedImage pour l'affichage Swing
        BufferedImage imgOriginale = matToBufferedImage(src);
        BufferedImage imgBinarisee = matToBufferedImage(binarized);

        // 5. Création de l'interface Swing pour afficher côte à côte
        JFrame frame = new JFrame("Binarisation OpenCV - Originale  vs Binarisée ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Image d'origine à gauche
        JLabel labelOriginal = new JLabel(new ImageIcon(imgOriginale));
        frame.add(labelOriginal);

        // Image binarisée à droite
        JLabel labelBinarisee = new JLabel(new ImageIcon(imgBinarisee));
        frame.add(labelBinarisee);

        frame.pack();
        frame.setLocationRelativeTo(null); // Centrer la fenêtre
        frame.setVisible(true);
    }
}