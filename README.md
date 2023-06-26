### This is a demo framework for testing REST API

### Technologies
Java, Gradle, RestAssured, Serenity

### Run tests
gradle test 

### Run annotated scenarios
gradle test -Dtags="@frontend"

### Run on required environment
gradle test -Denvironment="preprod"

### For update model
gradle generate -DpackageName=petstore

###  you can generate summary reports by invoking the reports task:
gradle reports







