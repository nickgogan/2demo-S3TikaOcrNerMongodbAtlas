# Text Extraction, Mining, and Search Example

This repo provides an example of text extraction using **Apache Tika** and **Tesseract** for OCR when needed. 
The text is then parsed for the presence of entities such as people and locations, using **OpenNLP**. The final output is stored in **MongoDB Atlas**, where **Atlas Search** (a Lucene-based fulltext search engine) can be configured to query it.

## Prerequisites

### Java & Maven

This is an Apache Maven-based project, which requires a JDK to compile.
All dependencies are handled by Maven, along with the creation of an executable jar, but both a JDK and Maven need to be manually installed.

Install a Java JDK (v17+) and ensure that a `JAVA_HOME` environment variable is configured to point to its root directory.
Add the JDK's `bin` directory to the system `PATH` environment variable.
- Test: `java --version`

Install Apache Maven (v3+). Check that `PATH` has Maven's `bin` directory listed.
- Test: `mvn --version`

### Tesseract OCR Engine

Tesseract is used for OCR'ing files that are detected by Tika as containing primarily images. This is done automatically by Tika.
This dependency is not managed by Maven and must be installed separately. 
Install Tesseract and ensure that its `bin` directory is listed in the system's `PATH`.

Test: `tesseract --version`

### AWS S3

The source files containing the text we extract and mine are pulled from an S3 bucket.
The AWS SDK and S3 dependencies are managed by Maven. They will be installed by Maven when running the jar creation commands. 
Authentication to S3 is done by setting the following system environment variables:
- `AWS_ACCESS_KEY=<AwsAccessKey>`
- `AWS_SECRET_ACCESS_KEY=<AwsSecretAccessKey>`

When creating the client at runtime, the AWS SDK checks for the presence of these environment variables and uses their values to authenticate and authorize access to the S3 resources.
Other methods exist for authentication and authorization, this is just one way to do it.

## Build & Run the Jar

1. Create a `Constants.java` file for the project in `tikaocr2\src\main\java\com\mongodb\tikaocr2` and provide the following:
```
package com.mongodb.tikaocr2;

public class Constants {
    public static String mongoURI = "<MongoDBAtlasConnectionString>";
    public static String strDb = "<MongoDBAtlasDatabaseName>";
    public static String strColl = "<MongoDBAtlasCollectionName>";
}
```
These variables are imported by `App.java` at startup.

2. Open a terminal at the project root.

3. Download dependencies: `mvn dependency:resolve`

4. Build the jar: `mvn clean package shade:shade`

Maven is configured to compile all dependencies, including the named entity recognition models, into a single jar.
Maven outputs other files along with the jar. All are located in `/target`. For this example, only the packaged jar is used.

5. Run the app: `java -jar ./target/application-1.0-shaded.jar`

## Set up Atlas Search

Now that the enriched document data is available in MongoDB Atlas, Atlas Search can index and query it based on your needs. For this example, we will explore the following Atlas Search capabilities: Standard indexing, English indexing, Keyword indexing, fulltext search (with fuzzy matching), faceting, synonymy, and autocomplete.

### Indexes

TBD

### Queries

TBD

## Run from AWS EC2
### VM Setup
sudo apt-get update -y
sudo apt-get upgrade -y
sudo apt-get install git
sudo apt-get install maven
sudo apt-get install imagemagick
sudo apt-get install ttf-mscorefonts-installer
sudo apt-get install tesseract-ocr
vi ~/.bashrc
export AWS_DEFAULT_REGION=us-east-1
export AWS_ACCESS_KEY_ID=<yourKeyId>
export AWS_SECRET_ACCESS_KEY=<yourSecretKey>
save file
source ~/.bashrc
### Repo Setup
git clone https://github.com/nickgogan/2demo-S3TikaOcrNerMongodbAtlas.git
vi 2demo-S3TikaOcrNerMongodbAtlas/tikaocr2/src/main/java/com/mongodb/tikaocr2/Constants.java
Add this code:
```
package com.mongodb.tikaocr2;

public class Constants {
    public static String mongoURI = <clusterConnStr>;
    public static String strDb = "DEMO_TIKA-SEARCH";
    public static String strColl = "pubmed";
}
```
save & quit
### Atlas Setup
Add EC2 instance's public IPv4 address to Atlas's IP Access List
Ensure that the target cluster is up and reachable from the EC2 instance
### Build & Run Uber Jar
cd to top-level tikaocr2/
mvn clean
mvn clean package shade:shade
cd to newly-built target
java -jar application_1.0-shaded.jar
