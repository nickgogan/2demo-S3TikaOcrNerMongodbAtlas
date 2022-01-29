package com.mongodb.tikaocr2;
/**
 * TikaPdfParser is a wrapper around Tika's PdfParser.
 * It includes constructors that, optionally, allow you to set the OCR strategy.
 *
 * @author  Nick Gogan (nick.gogan@mongodb.com)
 * @version 1.0
 * @since   01-27-2021
 */

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.ner.opennlp.OpenNLPNERecogniser;
import java.io.InputStream;
import java.util.*;

public class TikaPdfParser {
    protected UUID id;
    protected PDFParser parser;
    protected ParseContext parseContext;
    public boolean ParseWithNER = false;
    protected HashMap<String, String> nerModels;
    protected OpenNLPNERecogniser nerDetector;

    public TikaPdfParser() {
        id = java.util.UUID.randomUUID();
        parser = new PDFParser();
        parseContext = new ParseContext();

        System.out.println("[TikaPdfParser] Instantiated parser id " + id);
    }

    public TikaPdfParser(String ocrStrategy) {
        id = java.util.UUID.randomUUID();
        parser = new PDFParser();
        parser.setOcrStrategy(ocrStrategy); // Possibilities: no_ocr, ocr_only, "ocr_and_text", "auto".
        parseContext = new ParseContext();
//        PDFParserConfig parserConfig = parser.getPDFParserConfig(); //Method 2
//        parserConfig.setOcrStrategy(PDFParserConfig.OCR_STRATEGY.OCR_AND_TEXT_EXTRACTION);
        System.out.println("[TikaPdfParser] Instantiated parser id " + id);
    }

    public TikaPdfParser(HashMap<String, String> models) {
        id = java.util.UUID.randomUUID();
        parser = new PDFParser();
        parseContext = new ParseContext();
        nerModels = models;
        ParseWithNER = true;
        nerDetector = new OpenNLPNERecogniser(models);

        System.out.println("[TikaPdfParser] Instantiated parser id " + id);
    }

    public TikaPdfParser(String ocrStrategy, HashMap<String, String> models) {
        id = java.util.UUID.randomUUID();
        parser = new PDFParser();
        parser.setOcrStrategy(ocrStrategy); // Possibilities: no_ocr, ocr_only, "ocr_and_text", "auto".
        parseContext = new ParseContext();
//        PDFParserConfig parserConfig = parser.getPDFParserConfig(); //Method 2
//        parserConfig.setOcrStrategy(PDFParserConfig.OCR_STRATEGY.OCR_AND_TEXT_EXTRACTION);
        nerModels = models;
        ParseWithNER = true;

        System.out.println("[TikaPdfParser] Instantiated parser id " + id);
    }

    public ParsedDocument parse(InputStream stream, String filename) {
        if(ParseWithNER) { return parseWithDefaultsNer(stream, filename); }
        else return parseWithDefaults(stream, filename);
    }

    public ParsedDocument parseWithDefaults(InputStream stream, String filename) {
        if (stream == null)
        {
            System.out.println("[TikaPdfParser.parseWithDefaults] Null input stream.");
        }

        BodyContentHandler fulltext = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParsedDocument parsedDocument = new ParsedDocument(filename);

        try {
            parser.parse(stream, fulltext, metadata, parseContext);

            parsedDocument.setContent(fulltext);
            parsedDocument.setMetadata(metadata);

            System.out.println("[TikaPdfParser.parseWithDefaults] Successfully parsed file [" + filename + "] with Apache Tika's PDFParser and Tesseract OCR.");
            return parsedDocument;
        }
        catch(Exception ex)
        {
            System.out.println("[TikaPdfParser.parseWithDefaults] Error parsing document [" + filename + "].");
            ex.printStackTrace();

            return null;
        }
    }

    public ParsedDocument parseWithDefaultsNer(InputStream stream, String filename) {
        if (stream == null)
        {
            System.out.println("[TikaPdfParser.parseWithDefaultsNer] Null input stream.");
        }

        BodyContentHandler fulltext = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParsedDocument parsedDocument = new ParsedDocument(filename);

        try {
            parser.parse(stream, fulltext, metadata, parseContext);

            parsedDocument.setContent(fulltext);
            parsedDocument.setMetadata(metadata);
            parsedDocument.setEntitiesInMetadata(nerDetector.recognise(parsedDocument.getContent()));

            System.out.println("[TikaPdfParser.parseWithDefaultsNer] Successfully parsed file [" + filename + "] with Apache Tika's PDFParser, Tesseract OCR, and OpenNLP NER.");
            return parsedDocument;
        }
        catch(Exception ex)
        {
            System.out.println("[TikaPdfParser.parseWithDefaultsNer] Error parsing document [" + filename + "].");
            ex.printStackTrace();

            return null;
        }
    }
}