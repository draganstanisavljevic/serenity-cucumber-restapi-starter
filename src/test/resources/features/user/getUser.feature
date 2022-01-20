@User @REQ_NC-6276
Feature: Get user end points

  @SmokeTest
  @TEST_NC-12275
  Scenario: Get user by invalid username
    When I search for username sdfsdf
    Then I should receive status code 'NOT_FOUND'

  @TEST_NC-12276
  Scenario: Login with invalid credentials
    When I login with username dsfsd and password sdfsdf
    Then I should receive status code 'OK'

  @TEST_NC-12277
  Scenario: Logout
    When I logout
    Then I should receive status code 'OK'
