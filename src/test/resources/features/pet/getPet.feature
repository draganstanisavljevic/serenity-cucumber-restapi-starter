@Pet @REQ_NC-6272
Feature: Retrieve pet

  @TEST_NC-12279
  Scenario: Get pet by status
    When I retrieve pet by status available
    Then I should receive status code 'OK'

  @SmokeTest
  @TEST_NC-12278
  Scenario: Get pet by id
    Given There is a pet in database
    When I get pet by newly created id
    Then I should receive status code 'OK'

  Scenario: Get pet by tags
    When I search by tag gygygy
    Then I should receive status code 'OK'


