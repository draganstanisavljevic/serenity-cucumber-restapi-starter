Feature: Employee Details


  @GetValidUserDetails
  Scenario Outline: Send a Request to get valid user details

    Given I send a request to the URL <id> to get user details
    Then the response will return statuscode <status> and id <id> and salary <employee_salary> and name '<employee_name>' and age <employee_age> and message '<message>'

    Examples:
      |id  |status  |employee_salary|employee_name |employee_age  |message                                  |
      |1   |200     |320800         |Tiger Nixon   |61            |Successfully! Record has been fetched.   |



  @GetInvalidUserDetails
  Scenario Outline: Send a Request to get invalid user details

    Given I send a request to the URL <id> to get user details
    Then the response will return statuscode <statusCode> and status '<statusMessage>' and and message '<message>'

    Examples:
      |id     |statusCode  |statusMessage    |message                                  |
      |9999   |200         |success          |Successfully! Record has been fetched.   |

  @manual
  @manual-result:passed
  @manual-last-tested:sprint-2
  @manual-test-evidence:assets/db.jpg
  Scenario: Verify credentials present in Master Database not older than 30 days

    Given User is connected to Master Database
    Then Username "Admin" and password "admin123" are present in Master Database not older than 30 days
