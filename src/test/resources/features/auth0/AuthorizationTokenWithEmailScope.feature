@Auth0
Feature: Authorization token with email scope

  @TestCaseKey=LPAS-T11
  Scenario: Authorization token with email scope
    When I retrieve a valid token for user 'nlUser' in 'MYLP' service
    Then I should receive status code 'OK'
    And I should receive custom scopes in access token

  @TestCaseKey=LPAS-T123
  Scenario: Authorization token for cpq Tibco
    When I retrieve a valid token for user 'nlUser' in 'CPX_TIBCO' service
    Then I should receive status code 'OK'
    And I should receive custom scopes in access token