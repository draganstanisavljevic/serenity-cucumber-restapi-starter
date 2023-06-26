Feature: Home page

  @TEST_NGCOMCC-112233 @frontend
  Scenario: All elements are shown on home page
    When I open home_page page
    Then the following elements should be present on "Home page" Page:
      | Sign In       |
      | My Batis Link |
      | Banner        |

  @TEST_NGSMP1-549 @frontend
  Scenario: Sign in text is shown as title on home page
    When I open home_page page
    And I should see "Sign In" text as "title" on "Home page" Page



