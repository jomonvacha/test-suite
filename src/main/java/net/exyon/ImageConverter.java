package net.exyon;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class ImageConverter {
    private static final Logger logger = LoggerFactory.getLogger(ImageConverter.class);

    public static void main(String[] args) {
        String sourceImagePath = "test.jpg"; // Default source image
        String targetImagePath = "output.tif"; // Default target image name

        if (args.length >= 1) {
            sourceImagePath = args[0];
            if (args.length == 2) {
                targetImagePath = args[1];
            }
        }

        try {
            URL resourceUrl = ImageConverter.class.getClassLoader().getResource(sourceImagePath);
            if (resourceUrl == null) {
                logger.error("Source image file not found in resources.");
                return;
            }
            File sourceImageFile = new File(resourceUrl.toURI());
            File targetImageFile = new File(sourceImageFile.getParentFile(), targetImagePath);

            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            logger.info("Image read successfully from {}", sourceImagePath);

            BufferedImage resizedImage = Scalr.resize(sourceImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, 100, 200);
            logger.info("Image resized to 100x200 pixels");

            // Add watermark
            BufferedImage watermarkedImage = addTextWatermark(resizedImage);

            ImageIO.write(watermarkedImage, "TIFF", targetImageFile);
            logger.info("Image conversion, resizing, and watermarking completed successfully. Output file: {}", targetImagePath);
        } catch (IOException | URISyntaxException e) {
            logger.error("An error occurred during image processing", e);
        }
    }

    private static BufferedImage addTextWatermark(BufferedImage sourceImage) {
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2d.setComposite(alphaChannel);
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds("Test", g2d);

        // Calculate the coordinate where the String is painted
        int centerX = (sourceImage.getWidth() - (int) rect.getWidth()) / 2;
        int centerY = sourceImage.getHeight() / 2;

        g2d.drawString("Test", centerX, centerY);
        g2d.dispose();

        return sourceImage;
    }
}
