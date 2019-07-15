Feature: Register an asset
  As Alice
  I want to register an asset
  So that I can send one unit to Bob.

    The native currency asset is "cat.currency"
    and registering an asset costs 500 "cat.currency".
    The mean block generation time is 15 seconds
    and the maximum registration period is 1 year
    and the maximum asset divisibility is 6
    and the maximum number of assets an account can have is 1000
    and the maximum asset supply is 9000000000

  @bvt
  Scenario Outline: An account registers an expiring asset with valid properties and divisibility
    When Alice registers an asset for <duration> in blocks with valid <transferable>, <supplymutable> and <divisibility>
    Then Alice should become the owner of the new asset for at least <duration> blocks
    And Alice "cat.currency" balance should decrease in 500 units

    Examples:
      | duration | transferable | supplymutable | divisibility |
      | 1        | true         | false         | 0            |
      | 2        | false        | true          | 6            |
      | 3        | true         | true          | 1            |
      | 1        | false        | false         | 2            |

  @bvt
  Scenario: An account registers a non-expiring asset
    When Alice registers a non-expiring asset
    And Alice should become the owner of the new asset
    And Alice "cat.currency" balance should decrease in 500 units

  Scenario Outline: An account tries to register an asset with invalid values
    When Alice registers an asset for <duration> in blocks and <divisibility> divisibility 
    Then she should receive the error "<error>"
    And Alice "cat.currency" balance should remain intact

    Examples:
      | duration | divisibility | error                                |
      | 0        | 0            | Failure_Mosaic_Invalid_Duration      |
      | 1        | -1           | Failure_Mosaic_Invalid_Divisibility  |
      | 22000000 | 0            | Failure_Mosaic_Invalid_Duration      |
      | 60       | 7            | Failure_Mosaic_Invalid_Divisibility  |

  Scenario: An account tries to register an asset but does not have enough funds
    Given Sue has spent all her "cat.currency"
    When Sue registers an asset
    Then she should receive the error "Failure_Core_Insufficient_Balance"

  # Assest Transfer
  @bvt
  Scenario: An account register a non-transferable asset and 
    Given Alice registers a non transferable asset which she transfer 10 asset to Sue
    When Sue transfer 1 asset to Bob
    Then she should receive the error "Failure_Mosaic_Non_Transferable"
    When Sue transfer 1 asset to Alice
    Then 1 asset transfered successfully

  @bvt
  Scenario: An account register a transferable asset
    Given Alice registers a transferable asset which she transfer asset to Bob
    When Bob transfer 10 asset to Sue
    Then 10 asset transfered successfully
