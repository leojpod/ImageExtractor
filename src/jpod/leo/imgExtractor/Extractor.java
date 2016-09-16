package jpod.leo.imgExtractor;

import org.faceless.pdf2.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by leojpod on 2016-09-03.
 */
public class Extractor {
    private static Logger logger = Logger.getGlobal();
    private final PDF _pdf;
    private final PDFParser _parser;

    Extractor(PDF pdf) {
        this._pdf = pdf;
        this._parser = new PDFParser(pdf);
    }
    void extract() {
        List<PDFPage> pages = _pdf.getPages();
        List<PageExtractor.Image> allImgs = new ArrayList<>();
        for (PDFPage page :
                pages) {
            PageExtractor extract = _parser.getPageExtractor(page);
            Collection<PageExtractor.Image> imgs = extract.getImages();
            allImgs.addAll(imgs);
        }
        Extractor.logger.fine("we've got " + allImgs.size() + " images");
        PageExtractor.Image img = allImgs.get(0);
        RenderedImage renderedImage = img.getImage();

        try {
            ImageIO.write(renderedImage, "png", new File("imgs/stuff.png"));
            ImageIO.write(renderedImage, "gif", new File("imgs/stuff.gif"));
//            ImageIO.write(renderedImage, "jpg", new File("imgs/stuff.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // write your code here
        if (args.length < 2) {
            System.err.println("This utility program needs two arguments to work: a path or URI to a PDF and a path to an output folder where the program should store the extracted pictures");
            System.exit(1);
        }

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

        PDFReader reader = null;
        try {
            reader = new PDFReader(stream);
            PDF pdf = new PDF(reader);
            Extractor extractor = new Extractor(pdf);
            extractor.extract();
        } catch (IOException e) {
            System.err.println("Cannot read the PDF. Make sure the file locator you gave match a PDF");
            System.exit(2);
        }
        System.out.println("It worked !");
        System.exit(0);
    }
}
