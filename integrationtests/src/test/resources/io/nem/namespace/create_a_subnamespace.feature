
Feature: Create a subnamespace
  As Alice
  I want to create a subnamespace
  So that I can organize and name assets.

    Given the native currency asset is "cat.currency"
    And creating a subnamespace costs 100 cat.currency
    And the mean block generation time is 15 seconds
    And the maximum level of nested namespaces is 3
    And the subnamespace name can have up to 64 characters
    And Alice has 10000000 "cat.currency" in her account

  @bvt
  Scenario: An account creates a subnamespace
    Given Alice registered the namespace named "one" for 10 blocks
    When Alice registered the subnamespace "one.two"
    Then she should receive a confirmation message
    And she should become the owner of the new subnamespace "one.two"
    And her "cat.currency" balance should decrease in 100 units

  Scenario Outline: An account tries to create a subnamespace with an invalid name
    Given Alice registered the namespace named "alice" for 5 blocks
    When Alice tries to creates a subnamespace named "<subnamespace-name>"
    Then she should receive the error "Failure_Namespace_Invalid_Name"
    And her "cat.currency" balance should remain intact

    Examples:
      | subnamespace-name                                                                     |
      | alice.?€!                                                                             |
      | alice.this_is_a_really_long_subnamespace_name_this_is_a_really_long_subnamespace_name |

  Scenario: An account tries to create a subnamespace with a parent namespace registered by another account
    Given Alice registered the namespace named "alicetoo" for 5 blocks
    When Bob tries to creates a subnamespace named "alicetoo.subnamespace"
    Then she should receive the error "Failure_Namespace_Owner_Conflict"
    And her "cat.currency" balance should remain intact
    
  Scenario: An account tries to create a subnamespace and exceeds the number of allowed nested levels
    Given Alice registered the namespace named "one" for 10 blocks
    When Alice tries to creates a subnamespace "one.two.three.four" which is too deep
    Then she should receive the error "Failure_Namespace_Too_Deep"
    And her "cat.currency" balance should remain intact

  Scenario: An account tries to create a subnamespace that already exists
    Given Alice registered the namespace named "one" for 5 blocks
    And  Alice registered the subnamespace "one.dup"
    When Alice tries to creates a subnamespace named "one.dup"
    Then she should receive the error "Failure_Namespace_Already_Exists"
    And her "cat.currency" balance should remain intact

  Scenario: An account tries to create a subnamespace with parent namespace expired
    Given Alice registered the namespace named "alice" for 6 block
    When Alice tries to creates a subnamespace named "alice.subnamespace"
    Then she should receive the error "Failure_Namespace_Expired"
    And her "cat.currency" balance should remain intact

  Scenario: An account tries to create a subnamespace with an unknown parent namespace
    When Alice tries to creates a subnamespace named "unknown.subnamespace"
    Then she should receive the error "Failure_Namespace_Parent_Unknown"
    And her "cat.currency" balance should remain intact

  Scenario: An account tries to create a subnamespace but does not have enough funds
    Given Bob registered the namespace named "bob" for 5 blocks
    When Bob tries to creates a subnamespace named "bob.subnamespace"
    Then she should receive the error "Failure_Core_Insufficient_Balance"
