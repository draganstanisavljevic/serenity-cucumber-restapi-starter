@User @REQ_NC-6275
Feature: Create user

  @SmokeTest
  @TEST_NC-12274
  Scenario: Create user
    When I create user
    Then I should see proper details of created user in response