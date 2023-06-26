Feature: Login page

  @login
  Scenario: Check valid login
    When I open login_page page
    And I log in as testUser user
