
Feature: Create a subnamespace
  As Alice
  I want to create a subnamespace
  So that I can organize and name assets.

    Given the native currency asset is "network currency"
    And creating a subnamespace costs 100 network currency
    And the mean block generation time is 15 seconds
    And the maximum level of nested namespaces is 3
    And the subnamespace name can have up to 64 characters
    And Alice has 10000000 "network currency" in her account

  @bvt
  Scenario: An account creates a subnamespace
    Given Alice registered the namespace "one"
    When Alice registered the subnamespace "one.two"
    Then Alice should become the owner of the new subnamespace "one.two"
    And Alice pays child namespace fee

  Scenario Outline: An account tries to create a subnamespace with an invalid name
    Given Alice registered the namespace "alice"
    When Alice tries to creates a subnamespace named "<subnamespace-name>"
    Then she should receive the error "FAILURE_NAMESPACE_INVALID_NAME"
    And Alice balance should remain intact

    Examples:
      | subnamespace-name                                                                     |
      | alice.?€!                                                                             |
      | alice.this_is_a_really_long_subnamespace_name_this_is_a_really_long_subnamespace_name |

  Scenario: An account tries to create a subnamespace with a parent namespace registered by another account
    Given Alice registered the namespace "alicetoo"
    When Bob tries to creates a subnamespace named "alicetoo.subnamespace"
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"
    And Bob balance should remain intact
    
  Scenario: An account tries to create a subnamespace and exceeds the number of allowed nested levels
    Given Alice registered the namespace "one"
    When Alice tries to creates a subnamespace "one.two.three.four" which is too deep
    Then she should receive the error "FAILURE_NAMESPACE_TOO_DEEP"
    And Alice balance should remain intact

  Scenario: An account tries to create a subnamespace that already exists
    Given Alice registered the namespace "one"
    And  Alice registered the subnamespace "one.dup"
    When Alice tries to creates a subnamespace named "one.dup"
    Then she should receive the error "FAILURE_NAMESPACE_ALREADY_EXISTS"
    And Alice balance should remain intact

  Scenario: An account tries to create a subnamespace with parent namespace expired
    Given Alice registered the namespace "alice"
    And the namespace is now under grace period
    When Alice tries to creates a subnamespace named "alice.subnamespace"
    Then she should receive the error "FAILURE_NAMESPACE_EXPIRED"
    And Alice balance should remain intact

  Scenario: An account tries to create a subnamespace with an unknown parent namespace
    When Alice tries to creates a subnamespace named "unknown.subnamespace"
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN_PARENT"
    And Alice balance should remain intact

  Scenario: An account tries to create a subnamespace but does not have enough funds
    Given Tom has 1 units of the network currency
    And Tom registered the namespace named "tom" for 200 blocks
    When Tom tries to creates a subnamespace named "tom.subnamespace"
    Then Tom should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"
