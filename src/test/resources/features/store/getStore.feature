Feature: Get order

  Scenario: Get order
    When I search for order 6
    Then I should receive status code 'OK'

  Scenario: I get inventory
    When I get inventory
    Then I should receive status code 'OK'
