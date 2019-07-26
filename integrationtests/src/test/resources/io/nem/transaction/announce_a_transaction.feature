Feature: Announce a transaction
  As Alice,
  I want to announce a transaction
  So that I can alter the state of the blockchain

    Given Alice has an account in MAIN_NET
    And the maximum transaction lifetime is 1 day
    And the native currency asset is "cat.currency"

  Scenario: An account announced a valid transaction (max_fee)
    Given Alice announced a valid transaction of size 10 bytes willing to pay 25 cat.currency
    When a node with a fee multiplier of 2 processes the transaction
    Then the node accepts the transaction
    And her "cat.currency" balance is deducted by 20 units

  Scenario Outline: An account tries to announce a transaction with an invalid deadline
    When Alice tries to announces the transaction with a deadline of <deadline> hours
    Then she should receive the error "<error>"

    Examples:
      | deadline | error                        |
      | 25       | Failure_Core_Future_Deadline |
      | 999      | Failure_Core_Future_Deadline |
      | 0        | Failure_Core_Past_Deadline   |
      | -1       | Failure_Core_Past_Deadline   |


  Scenario: An unconfirmed transaction deadline expires
    When Alice announce valid transaction which expires in unconfirmed status
    Then she should receive a confirmation message
    And  she should receive the error "Failure_Core_Past_Deadline"

  Scenario: An account tries to announce a transaction with an invalid signature
    When Alice announces the transaction with invalid signature
    Then she should receive the error "Failure_Signature_Not_Verifiable"

  Scenario: An account tries to announce an already announced transaction
    Given Alice registered the asset "X"
    When Alice sends 2 asset "X" to Bob
    When Alice announces same the transaction
    Then she should receive the error "Failure_Hash_Exists"

  Scenario: An account tries to announce a transaction with an invalid network
    When Alice announces the transaction to the incorrect network
    Then she should receive the error "Failure_Core_Wrong_Network"

  Scenario: A node rejects a transaction because the max_fee value is too low
    Given Alice announced a valid transaction of size 10 bytes willing to pay 10 cat.currency
    When a node with a fee multiplier of 2 processes the transaction
    Then the node rejects the transaction
    And her "cat.currency" balance remains intact

  Scenario: No node accepts the transaction because the max_fee value is too low
    Given Alice announced a valid transaction of size 10 bytes willing to pay 5 cat.currency
    And all the nodes have set the fee multiplier to 2
    When the transaction deadline is reached
    Then the transaction is rejected
    And her "cat.currency" balance should remain intact
