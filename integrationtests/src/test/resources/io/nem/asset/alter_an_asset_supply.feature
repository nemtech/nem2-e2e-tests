Feature: Alter an asset supply
  As Alice
  I want to alter an asset supply
  So that it represents the available amount of an item in my shop.

    Given the mean block generation time is 15 seconds
    And the maximum asset supply is 9000000000
    And Alice has 10000000 "cat.currency" in her account

  @bvt
  Scenario Outline: An account alters an asset supply
    Given Alice has registered a <supplymutable> asset with an initial supply of 20 units
    When Alice decides to <direction> the asset supply in <amount> units
    Then the balance of the asset in her account should <direction> in <amount> units

    Examples:
      | supplymutable    | direction | amount |
      | true             | increase  | 5      |
      | false            | increase  | 5      |
      | true             | decrease  | 20     |
      | false            | decrease  | 20     |

  @bvt
  Scenario Outline: An account tries to alter an asset supply incorrectly
    Given Alice has registered an asset with an initial supply of 20 units
    When Alice accidentally <direction> the asset supply in <amount> units
    Then she should receive the error "<error>"

    Examples:
      | direction | amount           | error                                       |
      | increase  | 9000000000000000 | Failure_Mosaic_Supply_Exceeded              |
      | decrease  | 21               | Failure_Mosaic_Supply_Negative              |
      | increase  | 0                | Failure_Mosaic_Invalid_Supply_Change_Amount |

  Scenario: An account tries to alter the supply of a supply mutable asset but does not own all the units
    Given Alice has registered a "supply-mutable" asset with an initial supply of 20 units
    And she transfer 10 units to another account
    When Alice decides to increase the asset supply in 2 units
    Then the balance of the asset in her account should increase in 2 units

  Scenario: An account tries to alter the supply of a supply immutable asset but does not own all the units
    Given Alice has registered a "supply-immutable" asset with an initial supply of 20 units
    And she transfer 10 units to another account
    When Alice tries to increase the asset supply in 2 units
    Then she should receive the error "Failure_Mosaic_Supply_Immutable"

  Scenario: An account tries to alter the supply of an expired asset
    Given Alice has registered expiring asset for 1 block
    When Alice tries to increase the asset supply in 2 units
    Then she should receive the error "Failure_Mosaic_Expired"
