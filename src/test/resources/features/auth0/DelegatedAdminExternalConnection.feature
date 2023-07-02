@Auth0 @DelegatedAdmin
Feature: LeasePlan-Internal identities connection

  @ignored
  Scenario Outline: Validate Leaseplan-Internal connection with different roles
    When I attempt to retrieve a valid token for user '<user>' in 'DELEGATEDADMIN' service
    Then I should receive status code '<status>'

    Examples: Leaseplan Internal role
      | user                                | status        |
      | delegatedAdminLeaseplanExternalRole | OK            |
      | delegatedAdminLeaseplanInternalRole | FORBIDDEN     |
