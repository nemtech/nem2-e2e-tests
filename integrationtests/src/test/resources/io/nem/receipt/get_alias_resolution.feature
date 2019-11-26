Feature: Get the alias resolution for a given transaction
  As Alice
  I want to get the real identifier of the account or asset used in a transaction

Background: Create assets and link to namnespaces
        Given Alice registered the namespace "ticket_vendor"
        And Alice links the namespace "ticket_vendor" to the address of Bob

  # Core
  @bvt
  Scenario: An account gets the address of an aliased recipient in a given transaction
    Given "Sue" sent 1 "euros" to "ticket_vendor"
    When "Alice" wants to get the recipient address for the previous transaction
    Then "Alice" should get address of "ticket_vendor" as Bob

  @bvt
  Scenario: Alice wants to get the identifier of the aliased asset used in a given transaction
    Given "Sue" sent 1 "euros" to "ticket_vendor"
    When "Alice" wants to get asset identifier for the previous transaction
    Then "Alice" should get asset for euros
