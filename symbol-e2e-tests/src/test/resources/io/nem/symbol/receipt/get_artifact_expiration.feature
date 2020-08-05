Feature: Get artifact expiration
  As Alice
  I want to know when a namespace or asset expired

  # Mosaic
  @bvt
  Scenario: An account wants to know when the asset expired
    Given Alice has registered expiring asset "T" for 2 blocks
    And the asset is now expired
    When Alice checks when the asset expired
    Then she should get an estimated time reference

  # Namespace
  @bvt
  Scenario: An account tries to get if a namespace expired
    Given Alice registers a namespace named "token" for 6 blocks
    And the namespace is now under grace period
    When Alice checks if the previous namespace expired
    Then she should get an estimated time for the namespace

  Scenario: An account tries to get if a namespace is deleted
    Given Alice registers a namespace named "token" for 6 blocks
    And the namespace is now deleted
    When Alice checks if the previous namespace was deleted
    Then she should get an estimated time for the namespace
