Feature: Search page

  @TEST_NGSMP1-561 @frontend
  Scenario: Check empty search
    When I open home_page page
    And I click "Search Button" element in the "Header"
    Then I should see "NO_SEARCH_RESULT" text as "error message" on "Search Result Page" Page

  @TEST_NGSMP1-562 @frontend
  Scenario: When I search for dog I see result item
    When I open home_page page
    And I search for "dog" in "Search Box" element in the "Header"
