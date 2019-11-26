  Feature: Extend a namespace registration period
  As Alice
  I want to extend the namespace registration
  So that I can continue organizing and naming assets.

    Given the native currency asset is "cat.currency"
    And extending a namespace registration period costs 1 "cat.currency" per block
    And the mean block generation time is 15 seconds
    And the maximum registration period is 1 year
    And Alice has 10000000 "cat.currency" in her account
    And the grace period of a namespace is 1 day

  @bvt
  Scenario Outline: An account extends a namespace registration period
    Given Alice registered the namespace named "alice" for 10 blocks
    When Alice extends the registration of the namespace named "alice" for <duration> blocks
    Then she should receive a confirmation message
    And Alice extended the namespace registration period for at least <duration> blocks
    And Alice pays fee in <cost> units

    Examples:
      | duration | cost |
      | 10       | 10   |
      | 20       | 20   |

  @bvt
  Scenario: An account tries to extend a namespace registration period and this is under grace period
    Given Alice registered the namespace named "aliceexp" for 6 block
    And the namespace is now under grace period
    When Alice extends the registration of the namespace named "aliceexp" for 6 block
    Then she should receive a confirmation message
    And Alice extended the namespace registration period for at least 6 blocks
    And Alice pays fee in 6 units

  Scenario: An account tries to extend a namespace registration period, this is under grace but the account didn't created it
    Given Bob registered the namespace named "bobnew" for 5 block
    And the namespace is now under grace period
    When Alice tries to extends the registration of the namespace named "bobnew" for 6 block
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"
    And Alice "cat.currency" balance should remain intact

  Scenario: An account tries to extend a namespace registration period but does not have enough funds
    Given Bob registered the namespace named "bob_nofunds" for 5 block
    When  Bob tries to extends the registration of the namespace named "bob_nofunds" for 1000 blocks
    Then  she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"
