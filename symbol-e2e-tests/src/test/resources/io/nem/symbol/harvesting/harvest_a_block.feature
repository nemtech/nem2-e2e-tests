Feature: Harvest a block
  As Tom
  I want to harvest a block
  So that I obtain the resulting fees.

    Given the harvesting mosaic is "cat.harvest"
    And the minimum amount of "cat.harvest" necessary to be eligible to harvest is 10000

  Scenario: An account harvest a block
    Given Tom is running a node
    When Tom account has harvested a block
    Then Tom should be able to see the resulting fees

  Scenario: An remote account harvests a block
    Given Tom delegated her account importance to Bob
    And Bob is running a node
    When Tom account has harvested a block
    Then Tom should be able to see the resulting fees

  Scenario: An account tries to harvest a block without having enough harvesting asset
    Given Alice has 9999 "cat.harvest" in her account
    When she tries to harvest a block
    Then she should receive the error "Failure_Core_Block_Harvester_Ineligible"

  Scenario: A node which is harvesting a block falls below asset requirement
    Given Alice has 9999 "cat.harvest" in her account
    When she tries to harvest a block
    Then she should receive the error "Failure_Core_Block_Harvester_Ineligible"