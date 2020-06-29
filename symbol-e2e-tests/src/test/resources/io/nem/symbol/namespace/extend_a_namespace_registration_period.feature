  Feature: Extend a namespace registration period
  As Alice
  I want to extend the namespace registration
  So that I can continue organizing and naming assets.

    Given the native currency asset is "network currency"
    And extending a namespace registration period costs 1 "network currency" per block
    And the mean block generation time is 15 seconds
    And the maximum registration period is 1 year
    And Alice has 10000000 "network currency" in her account
    And the grace period of a namespace is 1 day

  @bvt
  Scenario Outline: An account extends a namespace registration period
    Given Alice registered the namespace "alice"
    When Alice extends the registration of the namespace named "alice" for <duration> blocks
    Then Alice extended the namespace registration period for at least <duration> blocks
    And Alice pays rental fee in <cost> units

    Examples:
      | duration | cost |
      | 10       | 10   |
      | 20       | 20   |

  @bvt
  Scenario: An account tries to extend a namespace registration period and this is under grace period
    Given Alice registered the namespace "aliceexp"
    And the namespace is now under grace period
    When Alice extends the registration of the namespace named "aliceexp" for 6 block
    Then Alice extended the namespace registration period for at least 6 blocks
    And Alice pays rental fee in 6 units

    @bvt
    Scenario: An account is able to send an asset using a namespace alias after namespace extension
      Given Alice registered the namespace named "token" for 20 blocks
      And Alice registered the asset "X"
      And Alice links the namespace "token" to the asset "X"
      And Alice can send "token" instead of asset "X" to Bob
      When Alice extends the registration of the namespace named "token" for 6 blocks
      Then Alice can send "token" instead of asset "X" to Bob

    @bvt
    Scenario: An account is able to send an asset to a namespace alias after the namespace is extended
      Given Alice registered the namespace named "sue" for 20 blocks
      And Alice registered the asset "X"
      And Alice links the namespace "sue" to the address of Sue
      And Alice can send asset "X" to the namespace "sue" instead of the address of Sue
      When Alice extends the registration of the namespace named "sue" for 6 blocks
      Then Alice can send asset "X" to the namespace "sue" instead of the address of Sue

    @bvt
    Scenario: An account is able to send an asset to a namespace alias after the namespace is extended in grace period
      Given Alice registered the namespace named "sue" for 10 blocks
      And Alice registered the asset "X"
      And Alice links the namespace "sue" to the address of Sue
      And Alice can send asset "X" to the namespace "sue" instead of the address of Sue
      And the namespace is now under grace period
      When Alice extends the registration of the namespace named "sue" for 6 blocks
      Then Alice can send asset "X" to the namespace "sue" instead of the address of Sue

    @bvt
    Scenario: An account is able to send an asset using a subnamespace alias after the root namespace renewal
      Given Alice registered the namespace named "alice" for 10 blocks
      And Alice registered the subnamespace "alice.token"
      And Alice registered the asset "X"
      And Alice links the namespace "alice.token" to the asset "X"
      And Alice can send "alice.token" instead of asset "X" to Bob
      When Alice extends the registration of the namespace named "alice" for 6 blocks
      Then Alice can send "alice.token" instead of asset "X" to Bob

  Scenario: An account tries to extend a namespace registration period, this is under grace but the account didn't created it
    Given Bob registered the namespace "bobnew"
    And the namespace is now under grace period
    When Alice tries to extends the registration of the namespace named "bobnew" for 6 block
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"
    And Alice balance should remain intact

  Scenario: An account tries to extend a namespace registration period but does not have enough funds
    Given Rob registered the namespace "rob_nofunds"
    And Rob ran out of funds
    When  Rob tries to extends the registration of the namespace named "rob_nofunds" for 1728000 blocks
    Then  Rob should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"
