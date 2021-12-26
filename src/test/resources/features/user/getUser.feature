Feature: Get user end points

  Scenario: Get user by invalid username
    When I search for username sdfsdf
    Then I should receive response code 404 user

  Scenario: Login with invalid credentials
    When I login with username dsfsd and password sdfsdf
    Then I should receive response code 200 user

  Scenario: Logout
    When I logout
    Then I should receive response code 200 user