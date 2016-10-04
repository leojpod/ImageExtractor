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
public class Extractor {
    private static Logger logger = Logger.getGlobal();
    private final PDF _pdf;
    private final PDFParser _parser;

    enum PictureFormat {
        PNG, JPG, GIF;

        void export(RenderedImage img, String path) throws IOException {
            if (this == JPG) {
                // OpenJDK hack: We need to change the color model of the image to enable the rendering as JPG.
                // this recipes was adapted from: http://stackoverflow.com/a/8170052/2698327

                BufferedImage bufferedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.drawRenderedImage(img, null);
                g2d.dispose();
                ImageIO.write(bufferedImage, "jpg", new File(path + ".jpg"));
            } else {
                String format = this.toString().toLowerCase();
                ImageIO.write(img, format, new File(path + "." + format));
            }
        }
    }


    Extractor(PDF pdf) {
        this._pdf = pdf;
        this._parser = new PDFParser(pdf);
    }

    void extract(PictureFormat[] formats, String basePath) throws IOException {
        List<PDFPage> pages = _pdf.getPages();
        List<PageExtractor.Image> allImgs = new ArrayList<>();
        Extractor.logger.fine("there are " + pages.size() + " pages to process");
        for (PDFPage page :
                pages) {
            PageExtractor extract = _parser.getPageExtractor(page);
            Collection<PageExtractor.Image> imgs = extract.getImages();
            allImgs.addAll(imgs);
        }
        Extractor.logger.fine("we've got " + allImgs.size() + " images");

        int count = 1;
        for (PageExtractor.Image img : allImgs) {
            if (img.getMetaData() != null) {
                BufferedReader reader = new BufferedReader(img.getMetaData());
                System.out.println("metaData -> ");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            RenderedImage renderedImage = img.getImage();
            try {
                for (PictureFormat f : formats) {
                    f.export(renderedImage, basePath + "/img" + count);
                }
                count ++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // write your code here
        if (args.length < 2) {
            System.err.println("This utility program needs two arguments to work: a path or URI to a PDF and a path to an output folder where the program should store the extracted pictures. You can also pass a third argument to specify the image format(s) to export the pictures to");
            System.exit(1);
        }
        Extractor.logger.setLevel(Level.FINEST);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        Extractor.logger.addHandler(handler);

        Extractor.logger.fine("let's start processing the parameters");
        Extractor.logger.fine("starting with " + args[0]);
        InputStream stream;
        try {
            stream = new URL(args[0]).openStream();
        } catch (IOException e) {
            stream = null;
            Extractor.logger.fine("we did not receive a URL let's see if it matches a file");
        }
        try {
            if (stream == null) {
                stream = new FileInputStream(args[0]);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not locate your PDF. Make sure the file locator you gave is correct");
            System.exit(2);
        }

        PictureFormat[] formats = {PictureFormat.PNG};
        if (args.length > 2) {
            String[] formatsAsString = args[2].split(",");
            List<PictureFormat> tempFormats = new ArrayList<>();
            for (String format: formatsAsString) {
                try {
                    tempFormats.add(PictureFormat.valueOf(format.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    Extractor.logger.warning("skipping '"+ format.trim() + "': unrecognized picture format");
                }
            }
            formats = tempFormats.toArray(new PictureFormat[0]);
        }
        String basePath = "imgs";
        if (args.length > 3) {
            basePath = args[3];
        }
        File directory = new File(basePath);
        for (File f : directory.listFiles()) {
            f.delete();
        }

        PDFReader reader = null;
        try {
            reader = new PDFReader(stream);
            PDF pdf = new PDF(reader);
            Extractor extractor = new Extractor(pdf);
            extractor.extract(formats, basePath);
        } catch (IOException e) {
            System.err.println("Cannot read the PDF. Make sure the file locator you gave match a PDF");
            System.exit(2);
        }
        Extractor.logger.finest("It worked !");
        System.exit(0);
    }
}
