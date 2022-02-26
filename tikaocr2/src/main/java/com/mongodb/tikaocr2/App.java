/**
 * The goal of this app is to retrieve a set of documents (PDFs for now) from S3, transform them to plaintext using Tika, (optionally) OCR them using Tesseract, and store the output fulltext + metadata into a specified MongoDB collection.
 * This is a Maven project
 *
 * @author  Nick Gogan (nick.gogan@mongodb.com)
 * @version 1.0
 * @since   01-27-2021
 */
package com.mongodb.tikaocr2;

import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.HashMap;

public class App
{
    public static String strConnectionString = Constants.mongoURI;
    public static String strDb = Constants.strDb;
    public static String strColl = Constants.strColl;

    public static void main(String[] args ) {
        S3Source s3Client = new S3Source();
        s3Client.connect();
        s3Client.crawl();

//        OpenNLP models from: http://opennlp.sourceforge.net/models-1.5/
        HashMap<String, String> models = new java.util.HashMap<>();
        models.put("Persons", "en-ner-person.bin");
        models.put("Locations", "en-ner-location.bin");
        models.put("Dates", "en-ner-date.bin");
        models.put("Times", "en-ner-time.bin");
        models.put("Organisations", "en-ner-organization.bin");
        models.put("Money", "en-ner-money.bin");
        TikaPdfParser pdfParser = new TikaPdfParser(models);

        MongoClientWrapper mongoClient = new MongoClientWrapper(strConnectionString);
        mongoClient.connect(strDb, strColl);

        System.out.println("Beginning to parse documents...");
        for(String bucketName : s3Client.filesToProcess.keySet())
        {
            System.out.println("Processing bucket [" + bucketName + "]...");
            for(S3Object file : s3Client.filesToProcess.get(bucketName))
            {
                String filename = file.key(); //Contains the .pdf extension, e.g. 00123-2020.PMC7569162.pdf
                System.out.println("Processing file [" + filename + "].");
                InputStream filestream = s3Client.getInputStream(bucketName, filename);
                ParsedDocument parsedDocument = pdfParser.parse(filestream, filename);
                if (parsedDocument == null)
                {
                    System.out.println("Null parsedDocument, skipping...");
                    continue;
                }
//                System.out.println(parsedDocument.getContent()); //Test
                mongoClient.insert(parsedDocument.convertToMongo());
            }
        }
        mongoClient.disconnect();
    }
}
