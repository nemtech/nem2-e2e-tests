Feature: Extend a namespace registration period
  As Alice
  I want to extend the namespace registration
  So that I can continue organizing and naming assets.

    Given the native currency asset is "cat.currency"
    And extending a namespace registration period costs 1 "cat.currency" per block
    And the mean block generation time is 15 seconds
    And the maximum registration period is 1 year
    And Alice has 10000000 "cat.currency" in her account
    And the redemption period of a namespace is 1 day
  
  @bvt
  Scenario Outline: An account extends a namespace registration period
    Given Alice registered the namespace named "alice" for 10 blocks
    When Alice extends the registration of the namespace named "alice" for <duration> blocks
    Then she should receive a confirmation message
    And the namespace registration period should be extended for at least <duration> blocks
    And her "cat.currency" balance should decrease in <cost> units

    Examples:
      | duration | cost |
      | 1        | 1    |
      | 2        | 2    |

  Scenario: An account tries to extend a namespace registration period and this is under redemption period
    Given Alice registered the namespace named "aliceexp" for 1 block
    And   the namespace is now under redemption period
    When Alice extends the registration of the namespace named "aliceexp" for 2 block
    Then she should receive a confirmation message
    And the namespace registration period should be extended for at least 2 blocks
    And her "cat.currency" balance should decrease in 2 units

  Scenario: An account tries to extend a namespace registration period, this is under redemption but the account didn't created it
    Given Bob registered the namespace named "bobnew" for 1 block
    And   the namespace is now under redemption period
    When Alice tries to extends the registration of the namespace named "bobnew" for 1 block
    Then she should receive the error "Failure_Namespace_Owner_Conflict"
    And her "cat.currency" balance should remain intact

  Scenario: An account tries to extend a namespace registration period but does not have enough funds
    Given Bob registered the namespace named "bob" for 2 block
    When  Bob tries to extends the registration of the namespace named "bob" for 1000 blocks
    Then  she should receive the error "Failure_Core_Insufficient_Balance"
