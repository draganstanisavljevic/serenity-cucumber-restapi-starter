Feature: Home page


  @TEST_NGCOMCC-112233
  Scenario: User can type open home page
    When I open home_page page
    Then the following elements should be present on "Home page" Page:
      | Sign In       |
      | My Batis Link |
      | Banner        |

  @TEST_NGSMP1-549
  Scenario: [GB] Check existing vehicle contract from contract tab
    When I open home_page page
    And I should see "Sign In" text as "title" on "Home page" Page



