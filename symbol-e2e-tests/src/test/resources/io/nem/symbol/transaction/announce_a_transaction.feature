Feature: Announce a transaction
  As Alice,
  I want to announce a transaction
  So that I can alter the state of the blockchain

    Given Alice has an account in MAIN_NET
    And the maximum transaction lifetime is 1 day
    And the native currency asset is "network currency"

  @bvt
  Scenario: An account announced a valid transaction (max_fee)
    When Alice announce valid transaction
    Then Alice balance should decrease by transaction fee

  @bvt
  Scenario Outline: An account tries to announce a transaction with an invalid deadline
    When Alice tries to announces the transaction with a deadline of <deadline> hours
    Then she should receive the error "<error>"

    Examples:
      | deadline | error                        |
      | 25       | FAILURE_CORE_FUTURE_DEADLINE |
      | 999      | FAILURE_CORE_FUTURE_DEADLINE |
      | 0        | FAILURE_CORE_PAST_DEADLINE   |
      | -1       | FAILURE_CORE_PAST_DEADLINE   |


  Scenario: An unconfirmed transaction deadline expires
    Given Alice waits for a next block
    And Alice creates a valid transaction with deadline in 5 seconds
    When Alice publishes the contract
    Then she should receive the error "FAILURE_CORE_PAST_DEADLINE"

  Scenario: An expired transaction gets submitted
    Given Alice creates a valid transaction with deadline in 20 seconds
    And Alice waits for 3 blocks
    When Alice publishes the contract
    Then she should receive the error "FAILURE_CORE_PAST_DEADLINE"

  Scenario: Transaction unconfirmed state verification
    When Alice announce valid transaction
    Then she can verify the transaction unconfirmed state in the DB

  Scenario: Transaction confirmed state verification
    When Alice announce valid transaction
    Then she can verify the transaction confirmed state in the DB

  Scenario: An account tries to announce a transaction with an invalid signature
    When Alice announces the transaction with invalid signature
    Then Alice balance should remain intact

  Scenario: An account tries to announce an already announced transaction
    Given Alice registered the asset "X"
    When Alice sends 2 asset of "X" to Bob
    When Alice announces same the transaction
    Then Alice balance should remain intact

  Scenario: An account tries to announce a transaction with an invalid network
    When Alice announces the transaction to the incorrect network
    Then she should receive the error "FAILURE_CORE_WRONG_NETWORK"

  Scenario: No node accepts the transaction because the max_fee value is too low
    Given Alice announced a valid transaction with max fee set below the in require fee
    When the transaction is dropped
    Then Alice balance should remain intact
