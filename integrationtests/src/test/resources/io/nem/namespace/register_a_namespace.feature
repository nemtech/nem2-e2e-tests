Feature: Register a namespace
  As Alice
  I want to register a namespace
  So that I can organize and name assets easily.
  
    Given the native currency asset is "cat.currency"
    And registering a namespace costs 1 "cat.currency" per block
    And the mean block generation time is 15 seconds
    And the maximum registration period is 1 year
    And the namespace name can have up to 64 characters
    And the following namespace names are reserved
      | xem  |
      | nem  |
      | user |
      | org  |
      | com  |
      | biz  |
      | net  |
      | edu  |
      | mil  |
      | gov  |
      | info |
    And Alice has 10000000 "cat.currency" in her account

  @bvt
  Scenario Outline: An account registers a namespace
    When Alice registers a namespace named "<name>" for <duration> block
    Then she should receive a confirmation message
    And Alice should become the owner of the new namespace <name> for least <duration> block
    And Alice pays fee in <cost> units

    Examples:
      | name  | duration | cost |
      | test1 | 5        | 5    |
      | test3 | 6        | 6    |

  Scenario Outline: An account tries to register a namespace with an invalid duration
    When Alice tries to registers a namespace named "alice" for <duration> block
    Then she should receive the error "<error>"
    And Alice "cat.currency" balance should remain intact

    Examples:
      | duration    | error                                         |
      | 0           | FAILURE_NAMESPACE_ETERNAL_AFTER_NEMESIS_BLOCK |
      | -1          | FAILURE_NAMESPACE_INVALID_DURATION            |
      | 40000000000 | FAILURE_NAMESPACE_INVALID_DURATION            |

  Scenario Outline: An account tries to register a namespace with an invalid name
    When Alice tries to registers a namespace named "<name>" for 6 block
    Then she should receive the error "FAILURE_NAMESPACE_INVALID_NAME"
    And Alice "cat.currency" balance should remain intact

    Examples:
      | name                                                              |
      | &€!                                                               |
      | this_is_a_really_long_space_name_this_is_a_really_long_space_name |
      | Upper                                                             |

  Scenario: An account tries to register a namespace with a reserved name
    When Alice tries to registers a namespace named "xem" for 6 block
    Then she should receive the error "FAILURE_NAMESPACE_ROOT_NAME_RESERVED"
    And Alice "cat.currency" balance should remain intact

  Scenario: An account tries to register a namespace which is already registered by another account
    Given Bob registers a namespace named "bob_name" for 5 blocks
    When Alice tries to registers a namespace named "bob_name" for 6 block
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"
    And Alice "cat.currency" balance should remain intact

  Scenario: An account tries to register a namespace which is already registered by another account during the grace period
    Given Bob registers a namespace named "bob_expired" for 6 blocks
    And the namespace is now under grace period
    When Alice tries to registers a namespace named "bob_expired" for 6 block
    Then she should receive the error "FAILURE_NAMESPACE_OWNER_CONFLICT"
    And Alice "cat.currency" balance should remain intact
  
  Scenario: An account tries to register a namespace but does not have enough funds
    Given Sue has has no "cat.currency"
    When Sue tries to registers a namespace named "sue" for 6 block
    Then she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"