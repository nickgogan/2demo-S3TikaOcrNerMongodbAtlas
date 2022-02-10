package com.mongodb.tikaocr2;
/**
 * S3Source is a wrapper around S3Client that allows for the identification and retrieval of PDFs from a given S3 bucket.
 *
 * @author  Nick Gogan (nick.gogan@mongodb.com)
 * @version 1.0
 * @since   01-27-2021
 */

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class S3Source {
    S3Client s3Client; //Note: AWS credentials managed via environment variables.
    public HashMap<String, Vector<S3Object>> filesToProcess; //NOT thread-safe.

    public S3Source() { }

    public void connect() {
        try {
            s3Client = S3Client.builder()
                    .region(Region.US_EAST_1)
                    .build();

            System.out.println("[S3Source.connect] Connected to S3.");
        }
        catch(Exception ex) {
            System.out.println("[S3Source.connect] Failed to connect to S3.");
            ex.printStackTrace();
        }
    }

    public void crawl() {
        filesToProcess = new HashMap<>();
        List<Bucket> buckets = s3Client.listBuckets().buckets();

        for(Bucket bucket : buckets) {
            String bucketName = bucket.name();
            System.out.println("[S3Source.crawl] Bucket: " + bucketName);

            ListObjectsV2Request request = ListObjectsV2Request
                    .builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            List<S3Object> objects = response.contents();
            Vector<S3Object> files = new Vector<>();

            for (ListIterator iterator = objects.listIterator(); iterator.hasNext(); ) {
                try {
                    S3Object file = (S3Object) iterator.next();
                    String filename = file.key();

                    if(filename.contains("pdf"))
                    {
                        System.out.print("[S3Source.crawl] Queued file for processing: " + filename + "\n");
                        files.add(file);
                    }
                    else { System.out.println("[S3Source.crawl] Skipped unsupported file format: " + filename); }
                }
                catch(Exception ex)
                {
                    System.out.println("[S3Source.crawl] Exception analyzing S3 file in bucket " + bucketName);
                }
            }
            filesToProcess.put(bucketName, files);
        }
    }

    public InputStream getInputStream(String bucketName, String filename)
    {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            return s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
        }
        catch(Exception ex)
        {
            System.out.println("[S3Source.fetchInputStream] Error fetching input stream for file " + filename + " in bucket " + bucketName);
            return null;
        }
    }

    public int countFilesToProcess() {
        int i = 0;

        for(String bucket : filesToProcess.keySet())
        {
            i += filesToProcess.get(bucket).size();
        }

        return i;
    }
}
