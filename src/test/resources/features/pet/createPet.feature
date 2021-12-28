@Pet @REQ_NC-6271
Feature: Create pet

  @SmokeTest
  @TEST_NC-12270
  Scenario: Create pet
    When I create pet
    Then I should see proper details of created pet in response