Feature: Retrieve pet

  Scenario: Get pet by status
    When I retrieve pet by status available
    Then I should receive status code 'OK'

  Scenario: Get pet by id
    Given Get pet by id 3
    Then I should receive status code 'OK'

  Scenario: Get pet by tags
    When I search by tag gygygy
    Then I should receive status code 'OK'

