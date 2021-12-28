@Store @REQ_NC-6273
Feature: Create order

  @SmokeTest
  @TEST_NC-12273
  Scenario: Create order
    When I create order
    Then I should see proper details of created order in response