# Mastercard Technical Evaluation (Bin Range Application)

## Preamble
BIN (Bank Identification Number) Ranges are used to provide information such as issuer currency code, 
bank name along with other relevant information for a given PAN (Primary account number)
In Mastercard we often use BIN Ranges for enriching our decision making capabilities. It is therefore essential to have an
up to date copy of the latest BIN Range information. 

This application provides a HTTP REST interface for retrieving BIN Range info for a given PAN. BIN Ranges are loaded from a 
file on disk which is updated on a fixed schedule. This file is then stored in a simple cache so as to provide fast lookup for the HTTP interface.

## Prerequisites
You will need the following installed on your development machine:

* Java 8
* Git
* Maven 3

Please clone this repository to your local workspace and develop your solution on a feature branch. 
When finished submit a single merge request containing all your changes.

N.B) if you want to clone the project using SSH you will need to add you public key to your gitlab profile,
alternatively you can clone the project using HTTPS 

## The Challenge
- Your challenge will be broken down in to 3 parts of varying difficulty. Try to complete **ALL** parts. 
- Please focus on the basics like ensuring you write good unit tests and perform data validation. 
- Please rad through all instructions fully to ensure you understand what deliverables are expected. 
- Your solution will be assessed based on your relevant skills and experience. 


**N.B. Important Instruction: Please complete ALL parts**


### Part 1. Approximately 20 minutes
We have recently pushed some code to the repository which seems to have broken a unit test
This unit test has since been marked as ignored to fix the build temporarily. Your job is to fix the functionality 
according to the failing test cases in `CachingBinRangeServiceTest.java`. 
You can assume that the BIN Ranges do not change, however the data they refer to I.E. Bank Name and Currency Code can change
You should also expect that a BIN Range can be removed from the cache.

N.B. The test should not be modified however additional tests would be welcomed to ensure this functionality does not break again.


### Part 2. Approximately 50 minutes
Update the existing `BinRangeInfoCacheController.java` controller to add additional endpoints to handle 
the `CREATE`, `UPDATE` and `DELETE` actions on the BIN Range cache entries in case we need to modify the cache in between 
the scheduled period whereby the cache is refreshed from the file. 

The endpoints should handle the cache entries individually I.E. this is not a batch API, 
Please note that a range start and end cannot change however the data they refer to can also there is the possibility of 
sub ranges I.E. a range that fits within a wider range however no ranges should share a start or end.

The `CREATE` endpoint should take a JSON representation of a `BinRangeInfo.class` in the body, please note the ref is generated on the server side
The `UPDATE` endpoint should take a JSON representation of a `BinRangeInfo.class` in the body and the ref as a path param.
The `DELETE` endpoint should take the ref associated with the desired `BinRangeInfo.class` entry in the cache as a path param, please ensure the index gets updated also.

Please ensure to create / update any relevant test cases to verify this new functionality,

### Part 3. Approximately 50 minutes
We have recently received a new requirement to maintain an audit log for all changes to the cache for 
historical audit and debugging purposes. Some of the work for this has already been completed with the creation of the `EventManager.java`
and the `AuditSubscriber.java` this event bus will be used to asynchronously update the audit log. 
It is your task to complete the functionality of this audit log to match the requirement in the JavaDoc found at `AuditSubscriber.handleEvent()`
please ensure to have this functionality covered by adequate unit tests.


## The Deliverable
Please ensure that this code is buildable by running `mvn clean install` and the application 
runs successfully with `java -jar target/mastercard-evaluation-bin-range-application-0.0.1-SNAPSHOT.jar`

* Please include documentation in SOLUTION.md about your approach and any assumptions you may have made while completing the above steps.
* Please include sample `curl` requests in your SOLUTION.md for each endpoint you develop.
* Create one Merge Request containing your changes for all of your work and notify the hiring manager by email when it is ready for review.

## References
* [How to create a Merge Request](https://docs.gitlab.com/ee/gitlab-basics/add-merge-request.html)
