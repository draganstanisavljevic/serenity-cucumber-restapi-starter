@Auth0 @DelegatedAdmin
Feature: Internal identities connection

  @ignored
  Scenario Outline: Validate connection with different roles
    When I attempt to retrieve a valid token for user '<user>' in 'DELEGATEDADMIN' service
    Then I should receive status code '<status>'

    Examples: Internal role
      | user     | status    |
      | testUser | OK        |
      | nlUser   | FORBIDDEN |
