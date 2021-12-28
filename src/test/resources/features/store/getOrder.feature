@Store @REQ_NC-6274
Feature: Get order

  @SmokeTest
  @TEST_NC-12271
  Scenario: Get order
    When I search for order 6
    Then I should receive status code 'OK'

  @TEST_NC-12272
  Scenario: I get inventory
    When I get inventory
    Then I should receive status code 'OK'
