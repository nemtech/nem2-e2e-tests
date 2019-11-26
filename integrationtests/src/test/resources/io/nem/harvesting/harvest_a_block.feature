Feature: Harvest a block
  As Alice
  I want to harvest a block
  So that I obtain the resulting fees.

  Background:
    Given the harvesting mosaic is "cat.harvest"
    And the minimum amount of "cat.harvest" necessary to be eligible to harvest is 10000

  Scenario: An account harvest a block
    Given Alice has 10000 "cat.harvest" in her account
    And is running a node
    When she harvests a block
    Then she should get the resulting fees

  Scenario: An remote account harvests a block
    Given Alice has 10000 "cat.harvest" in her account
    And Alice delegated her account importance to "Bob"
    And "Bob" is running a node
    When "Bob" harvests a block using "Alice" remote account
    Then she should get the resulting fees

  Scenario: An account tries to harvest a block without having enough harvesting asset
    Given Alice has 9999 "cat.harvest" in her account
    When she tries to harvest a block
    Then she should receive the error "Failure_Core_Block_Harvester_Ineligible"
