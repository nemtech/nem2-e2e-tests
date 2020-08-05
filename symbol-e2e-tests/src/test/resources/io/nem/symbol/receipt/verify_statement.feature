Feature: Verify the statements in block
  As Alice I want to verify that my receipts or transaction was included in the block.

  Background: Create assets and link to namespaces
    Given Alice registered the namespace "ticket_vendor"
    And Alice links the namespace "ticket_vendor" to the address of Bob
    And Alice registered the namespace "token"
    And Alice registered the asset "X"
    And Alice links the namespace "token" to the asset "X"

  @bvt
  Scenario: Alice wants verify that an address resolution was included in the block
    Given "Alice" sent 1 "token" to "ticket_vendor"
    Then "Alice" can verify that "ticket_vendor" was resolved as Bob for the previous transaction

  @bvt
  Scenario: Alice wants verify that a mosaic resolution was included in the block
    Given "Alice" sent 1 "token" to "ticket_vendor"
    Then "Alice" can verify that "token" was resolved as asset "X" for the previous transaction

  @bvt
  Scenario: Alice wants verify that her transaction was included in the block
    Given "Alice" sent 1 "token" to "ticket_vendor"
    Then "Alice" can verify that her transaction was included in the block