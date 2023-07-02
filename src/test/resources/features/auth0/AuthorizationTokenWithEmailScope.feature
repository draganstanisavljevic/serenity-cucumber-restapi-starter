@Auth0
Feature: Authorization token with email scope

  @TestCaseKey=LPAS-T11
  Scenario: Authorization token with email scope
    When I retrieve a valid token for user 'nlUser' in 'MYLP' service
    Then I should receive status code 'OK'
    And I should receive custom scopes in access token
