package jpod.leo.imgExtractor;

import org.faceless.pdf2.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by leojpod on 2016-09-03.
 */
public class SimpleExtractor {
    public static void main(String[] args) throws IOException {

        System.out.println("let's start processing the parameters");
        System.out.println("starting with " + args[0]);
        InputStream stream;
        try {
            stream = new URL(args[0]).openStream();
        } catch (IOException e) {
            stream = null;
            System.out.println("we did not receive a URL let's see if it matches a file");
        }
        try {
            if (stream == null) {
                stream = new FileInputStream(args[0]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not locate your PDF. Make sure the file locator you gave is correct");
            return;
        }

        String basePath = "imgs";
        File directory = new File(basePath);
        System.out.println("extracting pics to " + directory.getCanonicalPath());
        if (!directory.exists()) {
            System.out.println("creating extraction directory");
            directory.mkdirs();
        } else {
            System.out.println("extraction directory already exists, emptying it");
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        PDFReader reader = null;
        try {
            reader = new PDFReader(stream);
            PDF pdf = new PDF(reader);
            PDFParser parser = new PDFParser(pdf);
            List<PDFPage> pages = pdf.getPages();
            List<PageExtractor.Image> allImgs = new ArrayList<>();
            System.out.println("there are " + pages.size() + " pages to process");
            for (PDFPage page :
                    pages) {
                PageExtractor extract = parser.getPageExtractor(page);
                Collection<PageExtractor.Image> imgs = extract.getImages();
                allImgs.addAll(imgs);
            }
            System.out.println("we've got " + allImgs.size() + " images");

            int picCount = 0;
            for (PageExtractor.Image img : allImgs) {
                RenderedImage renderedImage = img.getImage();
                try {
                    picCount++;

                    // export to JPEG
                    BufferedImage bufferedImage = new BufferedImage(renderedImage.getWidth(), renderedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawRenderedImage(renderedImage, null);
                    g2d.dispose();
                    ImageIO.write(bufferedImage, "jpg", new FileOutputStream(basePath + "/img" + picCount + ".jpg"));
                    // export to PNG
                    ImageIO.write(renderedImage, "png", new FileOutputStream(basePath + "/img" + picCount + ".png"));
                    ImageIO.write(renderedImage, "gif", new FileOutputStream(basePath + "/img" + picCount + ".gif"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("extracted " + picCount + " pictures to " + directory.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("Cannot read the PDF. Make sure the file locator you gave match a PDF");
        }
        System.out.println("we are done!");
    }
}
