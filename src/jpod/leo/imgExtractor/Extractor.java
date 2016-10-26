package jpod.leo.imgExtractor;

import org.faceless.pdf2.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
            System.out.println("exporting to: " + path + "." + this.toString().toLowerCase());
            if (this == JPG) {
                // OpenJDK hack: We need to change the color model of the image to enable the rendering as JPG.
                // this recipes was adapted from: http://stackoverflow.com/a/8170052/2698327

                BufferedImage bufferedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.drawRenderedImage(img, null);
                g2d.dispose();
                ImageIO.write(bufferedImage, "jpg", new FileOutputStream(path + ".jpg"));
            } else {
                String format = this.toString().toLowerCase();
                ImageIO.write(img, format, new FileOutputStream(path + "." + format));
            }
        }
    }


    Extractor(PDF pdf) {
        this._pdf = pdf;
        this._parser = new PDFParser(pdf);
    }

    int extract(PictureFormat[] formats, String basePath) throws IOException {
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

        int count = 0;
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
                count++;
                for (PictureFormat f : formats) {
                    f.export(renderedImage, basePath + "/img" + count);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("This utility program needs two arguments to work: " +
                    "a path or URI to a PDF and a path to an output folder where the program should store the extracted pictures." +
                    " You can also pass a third argument to specify the image format(s) to export the pictures to");
            System.exit(1);
        }
        if (args.length > 3 && args[3] != "false") {
            Extractor.logger.setLevel(Level.ALL);
        } else {
            Extractor.logger.setLevel(Level.OFF);
        }
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
        if (args.length > 1) {
            String[] formatsAsString = args[1].split(",");
            System.out.println("reading requested formats: " + Arrays.toString(formatsAsString));
            List<PictureFormat> tempFormats = new ArrayList<>();
            for (String format : formatsAsString) {
                try {
                    tempFormats.add(PictureFormat.valueOf(format.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    Extractor.logger.warning("skipping '" + format.trim() + "': unrecognized picture format");
                }
            }
            formats = tempFormats.toArray(new PictureFormat[0]);
            System.out.println("exporting to these formats: " + Arrays.toString(formats));
        }
        String basePath = "imgs";
        if (args.length > 2) {
            basePath = args[2];
        }
        File directory = new File(basePath);
        Extractor.logger.fine("extracting pics to " + directory.getCanonicalPath());
        if (!directory.exists()) {
            directory.mkdir();
        } else {
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
            Extractor extractor = new Extractor(pdf);
            int picCount = extractor.extract(formats, basePath);
            System.out.println("extracted " + picCount + " pictures to " + directory.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("Cannot read the PDF. Make sure the file locator you gave match a PDF");
            System.exit(2);
        }
        Extractor.logger.finest("It worked !");
        System.exit(0);
    }
}
