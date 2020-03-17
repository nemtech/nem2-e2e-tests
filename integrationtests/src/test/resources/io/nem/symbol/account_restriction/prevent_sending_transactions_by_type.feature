Feature: Prevent sending transactions by type
  As Alex
  I want to prevent sending assets from my account
  So that I can ensure I don't send any of my assets while accepting escrow contracts

  Background:
    Given the following accounts exist:
      | Alex  |
      | Bobby |
      | Carol |
    And the following transaction types are available:
      | TRANSFER                       |
      | NAMESPACE_REGISTRATION         |
      | ACCOUNT_PROPERTIES_ENTITY_TYPE |

    # We are using three transaction types for better comprehension.
    # To get all the available transaction types, see the NEM Developer Center/Protocol/Transaction.

  Scenario: An account blocks sending transfer transactions
    Given Alex blocks sending transactions of type:
      | TRANSFER               |
      | NAMESPACE_REGISTRATION |
    When Alex tries to send 1 asset "cat.currency" to Bobby
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_OPERATION_TYPE_PROHIBITED"
    And Alex balance should remain intact
    And Bobby balance should remain intact

  Scenario: An account blocks sending namespace transactions
    Given Alex blocks sending transactions of type:
      | TRANSFER               |
      | NAMESPACE_REGISTRATION |
    When Alex tries to register a new namespace alextest
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_OPERATION_TYPE_PROHIBITED"
    And Alex balance should remain intact

 #      We must try some other transaction type like REGISTER_MOSAIC etc. here and expect a pass
 #   And sending transactions with the stated transaction types should be blocked

  Scenario: An account only allows sending transactions of a given transaction type
    Given Alex only allows sending transactions of type:
      | TRANSFER               |
      | ACCOUNT_OPERATION_RESTRICTION |
      | NAMESPACE_REGISTRATION |
    When Alex sends 1 asset "cat.currency" to Bobby
    Then Bobby should receive 1 of asset "cat.currency"
    And Alex "cat.currency" balance should decrease by 1 units

  Scenario: An account only allows sending transactions of a given transaction type
    Given Alex only allows sending transactions of type:
      | TRANSFER           |
      | NAMESPACE_REGISTRATION |
    When Alex registers new namespace alexexp
    Then Alex should become the owner of the new namespace alexexp
    And Alex "cat.currency" balance should decrease by 1 units

#      We must try some other transaction type like REGISTER_MOSAIC etc. here and expect a failure
#    And  only sending transactions with the stated transaction types should be allowed

  Scenario: An account unblocks transaction type
    Given Alex blocks sending transactions of type:
      | TRANSFER           |
      | NAMESPACE_REGISTRATION |
    And Alex removes TRANSFER from blocked transaction types
    When Alex sends 1 asset "cat.currency" to Bobby
    Then Bobby should receive 1 of asset "cat.currency"
    And Alex "cat.currency" balance should decrease by 1 units

  Scenario: An account unblocks transfer transaction type and the other type still blocks
    Given Alex blocks sending transactions of type:
      | TRANSFER           |
      | NAMESPACE_REGISTRATION |
    And Alex removes TRANSFER from blocked transaction types
    When Alex tries to register a namespace named "alexexp" for 10 blocks
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_OPERATION_TYPE_PROHIBITED"

  Scenario: An account removes a transaction type from the allowed transaction types
    Given Alex only allows sending transactions of type:
      | TRANSFER           |
      | NAMESPACE_REGISTRATION |
    When Alex removes TRANSFER from allowed transaction types
    And Alex sends 1 asset "cat.currency" to Bobby
    Then Bobby should receive 1 of asset "cat.currency"
    And Alex "cat.currency" balance should decrease by 1 units
    And Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_OPERATION_TYPE_PROHIBITED"

  Scenario: An account removes a transaction type from the allowed transaction types
    Given Alex only allows sending transactions of type:
      | TRANSFER           |
      | NAMESPACE_REGISTRATION |
    When Alex removes TRANSFER from allowed transaction types
    And Alex sends 1 asset "cat.currency" to Bobby
    And Alex tries to register a namespace named "alexexp" for 10 blocks
    Then Alex should receive a confirmation message
    And Bobby should receive 1 of asset "cat.currency"
    And Alex "cat.currency" balance should decrease by 1 units
    And Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_OPERATION_TYPE_PROHIBITED"

  @not-implemented
  Scenario: An account tries to register an asset but has not allowed sending "MOSAIC_DEFINITION" transactions
    Given Alex only allows sending transactions of type:
      | TRANSFER |
    When Alex tries to register an asset for 2 seconds
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_TRANSACTION_TYPE_NOT_ALLOWED"

  @not-implemented
  Scenario: An account tries to register an asset but has blocked sending "MOSAIC_DEFINITION" transactions
    Given Alex blocks sending transactions of type:
      | MOSAIC_DEFINITION  |
      | NAMESPACE_REGISTRATION |
    When Alex tries to register an asset for 2 seconds
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_TRANSACTION_TYPE_NOT_ALLOWED"

  Scenario: An account unblocks a not blocked transaction type
    Given Alex blocks sending transactions of type:
      | TRANSFER |
    When Alex tries to remove NAMESPACE_REGISTRATION from blocked transaction types
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION"

  Scenario: An account removes a transaction type that does not exist in the allowed transaction types
    Given Alex only allows sending transactions of type:
      | TRANSFER |
    When Alex tries to remove NAMESPACE_REGISTRATION from allowed transaction types
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION"

  Scenario: An account tries to only allow sending transactions of a given type when it has blocked types
    Given Alex blocks sending transactions of type:
      | TRANSFER |
    When Alex tries to only allow sending transactions of type:
      | NAMESPACE_REGISTRATION |
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION"

  Scenario: An account tries to block sending transactions with a given type when it has allowed types
    Given Alex only allows sending transactions of type:
      | TRANSFER |
    When Alex tries to block sending transactions of type:
      | NAMESPACE_REGISTRATION |
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION"

  Scenario: An account tries to block a transaction type twice
    Given Alex blocks sending transactions of type:
      | TRANSFER |
    When Alex tries to block sending transactions of type:
      | TRANSFER |
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_MODIFICATION_REDUNDANT"

  Scenario: An account tries to allow a transaction type twice
    Given Alex only allows sending transactions of type:
      | TRANSFER |
    When Alex tries to only allow sending transactions of type:
      | TRANSFER |
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_MODIFICATION_REDUNDANT"

  Scenario: An account tries to block a transaction with "ACCOUNT_PROPERTIES_ENTITY_TYPE" type
    When Alex blocks sending transactions of type:
      | ACCOUNT_PROPERTIES_ENTITY_TYPE |
    Then Alex should receive the error "FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION"