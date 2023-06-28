Feature: Search page using Screenplay

  @TEST_NGSMP1-561 @screenplay
  Scenario: Check search using screenplay
    When I open home_page page
    And I search for "dog" in "searchBox" element in the "Header" using screenplay

  @TEST_NGSMP1-561 @screenplay
  Scenario: Check empty search using screenplay
    When I open home_page page
    And I click "Search Button" element in the "Header" using screenplay
    Then I should see "NO_SEARCH_RESULT" text as "error message" on "Search Result Page" Page


