# Text Extraction, Mining, and Search Example

This repo provides an example of text extraction using **Apache Tika** and **Tesseract** for OCR when needed. 
The text is then parsed for the presence of entities such as people and locations, using **OpenNLP**. The final output is stored in **MongoDB Atlas**, where **Atlas Search** (a Lucene-based fulltext search engine) can be configured to query it.

## Prerequisites

### Java & Maven

This is an Apache Maven-based project, which requires a JDK to compile.
All dependencies are handled by Maven, along with the creation of an executable jar.

Install a Java JDK v17+ and ensure that a `JAVA_HOME` environment variable is configured to point to its root directory.
- Test: `java --version`

Install Apache Maven v3+. Check that `PATH` has Maven's bin directory listed along with the JDK's bin directory.
- Test: `mvn --version`

### Tesseract OCR Engine

Tesseract is used for OCR'ing files that are detected by Tika as containing primarily image files. 
Install Tesseract and ensure that its bin directory is listed in the system `PATH`.

Test: `tesseract --version`

### AWS S3

Source files are pulled from S3. Authentication to S3 is done by setting the following environment variables:
- `AWS_ACCESS_KEY=<AwsAccessKey>`
- `AWS_SECRET_ACCESS_KEY=<AwsSecretAccessKey>`

When creating the client, S3 checks for the presence of these environment variables and uses their values to authenticate.
Other methods exist for authentication, this is just one way to do it.

## Build & Run the Jar

Open a terminal to the project root.

Download dependencies: `mvn dependency:resolve`

Build the jar: `mvn clean package shade:shade`

Maven is configured to compile all dependencies, including the named entity recognition models, into a single jar.
Maven outputs other files along with the jar. All are located in `/target`.

Run: `java -jar ./target/application-1.0-shaded.jar`

## Set up Atlas Search

Now that the enriched document data is available in MongoDB Atlas, Atlas Search can index and query it based on your needs. For this example, we will explore the following Atlas Search capabilities: TBD.

### Indexes

TBD

### Queries

TBD
