package com.mongodb.tikaocr2;
/**
 * ParsedDocument is a construct for holding and working with the output of a Tika parser.
 *
 * @author  Nick Gogan (nick.gogan@mongodb.com)
 * @version 1.0
 * @since   01-27-2021
 */

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.sax.BodyContentHandler;
import org.bson.Document;

import java.util.*;
import java.util.regex.Pattern;

public class ParsedDocument {
    public String filename;
    private String fulltext;
    private Map<String, Set<String>> entities;
    private Metadata metadata;

    public ParsedDocument(String name) {
        filename = name;
    }

    public void setContent(String content)
    {
        fulltext = content;
    }
    public void setContent(BodyContentHandler content)
    {
        fulltext = content.toString();
    }
    public void appendContent(String content) { fulltext += content; }

    public String getContent() {
        return this.fulltext;
    }

    public void setMetadata(Metadata meta)
    {
        metadata = meta;
    }

    public void addMetadata(String name, String value)
    {
        this.metadata.add(name, value);
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    public Map<String, Set<String>> getEntities() {
        return this.entities;
    }

    public void setEntitiesInMetadata(Map<String, Set<String>> entitiesMap) {
        entities = entitiesMap;
        Set<String> entityNames = entities.keySet();

        for(String entityName : entityNames) {
            Set<String> entityMatches = entities.get(entityName);
            String[] entityMatchedValues = new String[entityMatches.size()];
            Property prop = Property.externalText("OpenNLP-NER-" + entityName);
            metadata.set(prop, entityMatches.toArray(entityMatchedValues));
        }
    }

    public Document convertToMongo()
    {
        Document doc = new Document("filename", this.filename); // Example filename: 00123-2020.PMC7569162.pdf
        String[] filenameParts = this.filename.split(Pattern.quote("."), -1); //Split on the '.' char and return all parts.
        String articleId = filenameParts[1];
        String docId = filenameParts[0];
        doc.append("addedDate", Calendar.getInstance().getTime());
        doc.append("fulltext", this.fulltext);
        doc.append("pubmedPageUrl", "https://www.ncbi.nlm.nih.gov/pmc/articles/"+articleId);
        doc.append("pubmedPdfUrl", "https://www.ncbi.nlm.nih.gov/pmc/articles/"+articleId+"/pdf/"+docId+".pdf");

        String[] metadataKeys = this.metadata.names();
        Arrays.sort(metadataKeys);

        for(String key : metadataKeys)
        {
            List<String> entityValues = new ArrayList<>(Arrays.asList(this.metadata.getValues(key)));
            doc.append(key, entityValues);
        }

        System.out.println("[ParsedDocument.convertToMongo] Converted file [" + this.filename + "] to MongoDB document.");

        return doc;
    }
}
