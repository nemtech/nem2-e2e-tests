
Feature: Link a namespace to an address
  As Alice,
  I want to link a namespace to an address,
  So that it is memorable and easily recognizable

  @bvt
  Scenario: An account links a namespace to an address
    Given Alice registered the namespace "sue"
    And Alice registered the asset "X"
    When Alice links the namespace "sue" to the address of Sue
    Then she should receive a confirmation message
    And Alice can send asset "X" to the namespace "sue" instead of the address of Sue

  @bvt
  Scenario: An account unlink a namespace to an address
    Given Alice registered the namespace "sue"
    And Alice registered the asset "X"
    And Alice links the namespace "sue" to the address of Sue
    When Alice unlinks the namespace "sue" from the address of Sue
    And Alice tries to send asset "X" to the namespace "sue" instead of the address of Sue
    Then she should receive the error "Failure_Core_Invalid_Address"

  @bvt
  Scenario: An account links a subnamespace to an address
    Given Alice registered the subnamespace "accounts.sue"
    And Alice registered the asset "X"
    When Alice links the namespace "accounts.sue" to the address of Sue
    Then she should receive a confirmation message
    And Alice can send asset "X" to the namespace "accounts.sue" instead of the address of Sue

  Scenario: An account unlink a subnamespace to an address
    Given Alice registered the subnamespace "accounts.sue"
    And Alice registered the asset "X"
    And Alice links the namespace "accounts.sue" to the address of Sue
    When Alice unlinks the namespace "accounts.sue" from the address of Sue
    And Alice tries to send asset "X" to the namespace "accounts.sue" instead of the address of Sue
    Then she should receive the error "FAILURE_CORE_INVALID_ADDRESS"

  Scenario: An account link a namespace to an unknown account
    Given Alice registered the namespace "tom"
    And Alice registered the asset "T"
    When Alice tries to link the namespace "tom" to the address of Tom
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_INVALID_ADDRESS"

  Scenario: An account tries to link a namespace already linked
    Given Alice registered the namespace "sue"
    And Alice links the namespace "sue" to the address of Sue
    When Alice tries to link the namespace "sue" to the address of Sue
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_ALREADY_EXISTS"

  Scenario: An account tries to link an unknown namespace to an address
    When Alice tries to link the namespace "unknown" to the address of Sue
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN"

  Scenario: An account tries to unlink an unknown namespace from an address
    When Alice tries to unlink the namespace "unknown" from the address of Sue
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN"

  Scenario: An account tries to link a namespace that it does not own to an address
    Given Bob registered the namespace "bob"
    When Alice tries to link the namespace "bob" to the address of Alice
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"

  Scenario: An account tries to unlink a namespace she does not own from an address
    Given Bob registered the namespace "bob"
    And Bob links the namespace "bob" to the address of Bob
    When Alice tries to unlink the namespace "bob" from the address of Bob
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"

  Scenario: An account tries to unlink an address link that does not exist
    Given Alice registered the namespace "alice"
    When Alice tries to unlink the namespace "alice" from the address of Alice
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN_ALIAS"

  Scenario: An account tries to unlink a namespace from an address but uses an asset instead
    Given Alice registered the asset "X"
    And Alice registered the namespace "alice"
    And Alice links the namespace "alice" to the address of Alice
    When Alice tries to unlink the namespace "alice" from the asset "X"
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_INCONSISTENT_UNLINK_TYPE"

  Scenario: An account tries to link a namespace to an address that does not exist in the network
    Given Alice registered the namespace "alice"
    When Alice tries to link the namespace "alice" to the address of AliceUnknown
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_INVALID_ADDRESS"