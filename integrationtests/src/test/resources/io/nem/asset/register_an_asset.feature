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
  Scenario Outline: An account registers an expiring asset with valid properties with divisibility
    When Alice registers <transferability>, supply <supply-mutability> with divisibility <divisibility> asset for <duration> in blocks
    Then Alice should become the owner of the new asset for at least <duration> blocks
    And Alice pays fee in 500 units

    Examples:
      | duration | transferability    | supply-mutability | divisibility |
      | 1        | transferable       | immutable         | 0            |
      | 2        | nontransferable    | mutable           | 6            |
      | 3        | transferable       | mutable           | 1            |
      | 1        | nontransferable    | immutable         | 2            |

  @bvt
  Scenario: An account registers a non-expiring asset
    When Alice registers a non-expiring asset
    And Alice should become the owner of the new asset
    And Alice pays fee in 500 units

  Scenario Outline: An account tries to register an asset with invalid values
    When Alice registers an asset for <duration> in blocks with <divisibility> divisibility
    Then she should receive the error "<error>"
    And Alice "cat.currency" balance should remain intact

    Examples:
      | duration | divisibility | error                                |
      | -1       | 0            | FAILURE_MOSAIC_INVALID_DURATION      |
      | 1        | -1           | FAILURE_MOSAIC_INVALID_DIVISIBILITY  |
      | 22000000 | 0            | FAILURE_MOSAIC_INVALID_DURATION      |
      | 60       | 7            | FAILURE_MOSAIC_INVALID_DIVISIBILITY  |

  Scenario: An account tries to register an asset but does not have enough funds
    Given Sue has spent all her "cat.currency"
    When Sue registers an asset
    Then she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

