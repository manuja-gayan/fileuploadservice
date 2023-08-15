# File Upload Service

This service serves the purpose of facilitating the seamless uploading of a diverse range of file types to either an AWS S3 bucket or a Google Cloud Storage bucket. The allowed file types exhibit a fully dynamic nature, contingent upon compatibility with the designated cloud provider's specifications. Notably, the capability to control and determine permissible file types is executed through configurable parameters within the designated configuration database.

## Features

* Facilitates seamless utilization of both AWS S3 buckets and Google Cloud Storage buckets.
* Designed with extensibility in mind, allowing seamless integration with various other cloud storage providers through a standardized interface.
* Enables the concurrent upload of multiple files, enhancing efficiency and productivity.
* Empowers configurable settings for file attributes such as size, type, and format through integration with a comprehensive and adaptable database system.

## Supported versions

"fileupload-service" supports the following versions.  
Other versions might also work, but we have not tested it.

* Java 8, 11
* Spring Boot 2.7.5

## Building and running

To build and test, you can run:

```sh
$ cd fileuploadmgtservice
# build the application
$ mvn clean install
# run the application
$ java -jar target/fileuploadmgtservice-0.0.1.jar
```
Note: cloud provider's service-account should in resources folder
## Contributing

Bug reports and pull requests are welcome :)