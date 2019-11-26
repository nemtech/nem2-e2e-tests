

Feature: Link a namespace to an asset
  As Alice,
  I want to link a namespace to an asset,
  So that it is memorable and easily recognizable

  @bvt
  Scenario: An account is able to send an asset using a namespace alias
    Given Alice registered the namespace "token"
    And Alice registered the asset "X"
    When Alice links the namespace "token" to the asset "X"
    Then she should receive a confirmation message
    And Alice can send "token" instead of asset "X" to Bob

  @bvt
  Scenario: An account tries to send an asset using namespace alias to an assest after unlinking it
    Given Alice registered the namespace "asset"
    And Alice registered the asset "T"
    And Alice links the namespace "asset" to the asset "T"
    When Alice unlinks the namespace "asset" from the asset "T"
    And Alice tries to send "token" instead of asset "X" to Bob
    Then she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

  @bvt
  Scenario: An account unlink an asset from a namespace after the asset expired
    Given Alice registered the namespace "assetexpire"
    And Alice has registered expiring asset "T" for 6 blocks
    And Alice links the namespace "assetexpire" to the asset "T"
    And the asset is now expired
    When Alice unlinks the namespace "assetexpire" from the asset "T"
    Then she should receive a confirmation message

  @bvt
  Scenario: An account is able to send an asset using a subnamespace alias
    Given Alice registered the subnamespace "alice.token"
    And Alice registered the asset "X"
    When Alice links the namespace "alice.token" to the asset "X"
    Then she should receive a confirmation message
    And Alice can send "alice.token" instead of asset "X" to Bob

  @bvt
  Scenario: An account tries to send an asset using namespace alias to an assest after unlinking it
    Given Alice registered the subnamespace "alice.asset"
    And Alice registered the asset "T"
    And Alice links the namespace "alice.asset" to the asset "T"
    When Alice unlinks the namespace "alice.asset" from the asset "T"
    And Alice tries to send "alice.asset" instead of asset "T" to Bob
    Then she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

  @bvt
  Scenario: An account tries to send an asset using invalid namespace alias
    Given Alice registered the namespace "unknownasset"
    When Alice tries to send "unknownasset" instead of asset "T" to Bob
    Then she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

  Scenario: An account tries to link a namespace already in use (asset) to an asset
    Given Alice registered the namespace "token"
    And Alice registered the asset "X"
    And Alice links the namespace "token" to the asset "X"
    And Alice registered the asset "Z"
    When Alice tries to link the namespace "token" to the asset "Z"
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_ALREADY_EXISTS"

  Scenario: An account tries to link a namespace already in use (account) to an asset
    Given Alice registered the namespace "alice"
    And Alice registered the asset "X"
    And Alice links the namespace "alice" to the address of Alice
    When Alice tries to link the namespace "alice" to the asset "X"
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_ALREADY_EXISTS"

  Scenario: An account tries to link a namespace twice to an asset
    Given Alice registered the namespace "token"
    And Alice registered the asset "X"
    Given Alice links the namespace "token" to the asset "X"
    When Alice tries to link the namespace "token" to the asset "X"
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_ALREADY_EXISTS"

  Scenario: An account tries to link an unknown namespace to an asset
    When Alice tries to link the namespace "unknown" to the asset "X"
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN"

  Scenario: An account tries to unlink an unknown namespace to an asset
    Given Alice registered the asset "X"
    When Alice tries to unlink the namespace "unknown" from the asset "X"
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN"

  Scenario: An account tries to link a namespace to an unknown asset
    Given Alice registered the namespace "token"
    When Alice tries to link the namespace "token" to the asset "unknown"
    Then she should receive the error "FAILURE_MOSAIC_EXPIRED"

  Scenario: An account tries to unlink a namespace from an unknown asset
    Given Alice registered the namespace "token"
    When Alice tries to unlink the namespace "token" from the asset "unknown"
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN_ALIAS"

  Scenario: An account tries to link a namespace to an asset that it does not own
    Given Alice registered the namespace "token"
    And Alice registered the asset "Y"
    When Bob tries to link the namespace "token" to the asset "Y"
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"

  Scenario: An account tries to unlink a namespace from an asset that it does not own
    Given Alice registered the namespace "token"
    And Alice registered the asset "Y"
    And Alice links the namespace "token" to the asset "Y"
    When Bob tries to unlink the namespace "token" from the asset "Y"
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"

  Scenario: An account tries to link a namespace it does not own to an asset
    Given Bob registered the namespace "bob"
    And Alice registered the asset "X"
    When Alice tries to link the namespace "bob" to the asset "X"
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"

  Scenario: An account tries to unlink a namespace she does not own from an asset
    Given  Alice registered the namespace "alice"
    And Alice registered the asset "Y"
    And Alice links the namespace "alice" to the asset "Y"
    When Bob tries to unlink the namespace "alice" from the asset "Y"
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"

  Scenario: An account tries to unlink an asset link that does not exist
    Given Alice registered the namespace "token"
    And Alice registered the asset "D"
    When Alice tries to unlink the namespace "token" from the asset "D"
    Then she should receive the error "FAILURE_NAMESPACE_UNKNOWN_ALIAS"

  Scenario: An account tries to unlink a namespace from an asset but uses an address instead
    Given Alice registered the namespace "token"
    And Alice registered the asset "D"
    And Alice links the namespace "token" to the asset "D"
    When Alice tries to unlink the namespace "token" from the address of Alice
    Then she should receive the error "FAILURE_NAMESPACE_ALIAS_INCONSISTENT_UNLINK_TYPE"