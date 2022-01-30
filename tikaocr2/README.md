# Text Extraction and Mining Example

This repo provides an example of text extraction using Apache Tika and Tesseract for OCR when needed. 
The text is then parsed for the presence of entities such as people and locations. The final output is stored in MongoDB Atlas, where Lucene-based fulltext search can be set up to query it.

## Prerequisites

### Java & Maven

This is an Apache Maven-based project, which requires a JDK to compile.
All dependencies are handled by Maven, along with the creation of an executable jar.

Install a Java JDK v17+ and ensure that a JAVA_HOME environment variable is configured.
- Test: java --version
Install Apache Maven v3+. Check that PATH has Maven's bin directory listed along with the JDK's bin directory.\
- Test: mvn --version

### Tesseract OCR Engine

Tesseract is used for OCR'ing files that are detected by Tika as containing primarily image files. 
Install Tesseract and ensure that its root directory is listed in the system PATH.

Test: tesseract --version

### AWS S3

Source files are pulled from S3. Authentication to S3 is done by setting the following environment variables:
- AWS_ACCESS_KEY=<AwsAccessKey>
- AWS_SECRET_ACCESS_KEY=<AwsSecretAccessKey>

When creating the client, S3 checks for the presence of these environment variables and uses their values to authenticate.
Other methods exist for authentication, this is just one way to do it.

## Build & Run the Jar

Open a terminal to the project root.
Download dependencies: mvn dependency:resolve
Build the jar: mvn clean package shade:shade
Run: java -jar ./target/application-1.0-shaded.jar
