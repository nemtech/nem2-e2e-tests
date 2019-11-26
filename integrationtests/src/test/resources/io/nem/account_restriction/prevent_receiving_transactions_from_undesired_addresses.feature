Feature: Prevent receiving transactions from undesired addresses
  As Alex
  I don't want to receive assets from Bobby
  So that I have control of which accounts can send me transactions
#  TODO: this and other restriction features do not have scenarios considering the direction of allow/block.
#   Scenario:
#  ALLOW_INCOMING_ADDRESS - done, ALLOW_OUTGOING_ADDRESS - not done!

  Background:
    Given the following accounts exist:
      | Alex    |
      | Bobby   |
      | Carol   |

  Scenario: 1. An account blocks receiving transactions from a set of addresses
    Given Bobby blocked receiving transactions from:
      | Alex   |
      | Carol  |
    When Alex tries to send 1 asset "cat.currency" to Bobby
    Then Alex should receive the error "Failure_RestrictionAccount_Address_Interaction_Prohibited"

  Scenario: 2. An account only allows receiving transactions from a set of addresses
    Given Bobby only allowed receiving transactions from:
      | Alex   |
    When Alex sends 1 asset "cat.currency" to Bobby
      And Carol tries to send 1 asset "cat.currency" to Bobby
    Then Bobby should receive a confirmation message
      And Bobby should receive 1 of asset "cat.currency"
    And Alex "cat.currency" balance should decrease by 1 unit
      And Carol should receive the error "Failure_RestrictionAccount_Address_Interaction_Prohibited"

  Scenario: 3. An account unblocks an address
    Given Bobby blocked receiving transactions from:
      | Alex  |
      | Carol  |
    When Bobby removes Alex from blocked addresses
    And Alex sends 1 asset "cat.currency" to Bobby
    And Carol tries to send 1 asset "cat.currency" to Bobby
    Then Bobby should receive a confirmation message
    And Bobby should receive 1 of asset "cat.currency"
    And Alex "cat.currency" balance should decrease by 1 unit
    And Carol should receive the error "Failure_RestrictionAccount_Address_Interaction_Prohibited"

  Scenario: 4. An account removes an address from the allowed addresses
    Given Bobby only allowed receiving transactions from:
      | Alex  |
      | Carol |
    When Bobby removes Alex from allowed addresses
    And Carol sends 1 asset "cat.currency" to Bobby
    And Alex tries to send 1 asset "cat.currency" to Bobby
    Then Bobby should receive a confirmation message
    And Bobby should receive 1 of asset "cat.currency"
    And Carol "cat.currency" balance should decrease by 1 unit
    And Alex should receive the error "Failure_RestrictionAccount_Address_Interaction_Prohibited"

  Scenario: 5. An account unblocks a not blocked address
    Given Bobby blocked receiving transactions from:
      | Alex |
    When Bobby tries to remove Carol from blocked addresses
    Then Bobby should receive the error "Failure_RestrictionAccount_Modification_Not_Allowed"

  Scenario: 6. An account removes an address that does not exist in the allowed addresses
    Given Bobby only allowed receiving transactions from:
      | Alex |
    When Bobby tries to remove Carol from allowed addresses
    Then Bobby should receive the error "Failure_RestrictionAccount_Modification_Not_Allowed"

  Scenario: 7. An account tries only to allow receiving transactions from a set of addresses when it has blocked addresses
    Given Bobby blocked receiving transactions from:
      | Alex |
    When Bobby tries to only allow receiving transactions from Carol
    Then Bobby should receive the error "Failure_RestrictionAccount_Modification_Not_Allowed"

  Scenario: 8. An account tries to block receiving transactions from a set of addresses when it has allowed addresses
    Given Bobby only allowed receiving transactions from:
      | Alex |
    When Bobby tries to block receiving transactions from Carol
    Then Bobby should receive the error "Failure_RestrictionAccount_Modification_Not_Allowed"

  Scenario: 9. An account tries to block an address twice
    Given Alex blocked receiving transactions from:
      | Bobby |
    When Alex tries to block receiving transactions from Bobby
    Then Alex should receive the error "Failure_RestrictionAccount_Modification_Redundant"

  Scenario: 10. An account tries to allow an address twice
    Given Alex only allowed receiving transactions from:
      | Bobby |
    When Alex tries to only allow receiving transactions from Bobby
    Then Alex should receive the error "Failure_RestrictionAccount_Modification_Redundant"

  Scenario: 11. An account tries to block self
    When Alex tries to block receiving transactions from herself
    Then Alex should receive the error "Failure_RestrictionAccount_Modification_Address_Invalid"

  Scenario: 12. An account tries to only allow self
    When Alex tries to only allow receiving transactions from herself
    Then she should receive the error "Failure_RestrictionAccount_Modification_Address_Invalid"

  Scenario: 13. An account tries to block too many addresses
    Given Alex has blocked receiving transactions from 512 different addresses
    When Alex tries to block receiving transactions from Bobby
    Then Alex should receive the error "Failure_RestrictionAccount_Values_Count_Exceeded"

  Scenario: 14. An account tries to only allow too many addresses
    Given Alex has allowed receiving transactions from 512 different addresses
    When Alex tries to only allow receiving transactions from Bobby
    Then Alex should receive the error "Failure_RestrictionAccount_Values_Count_Exceeded"

  Scenario: 15. An account tries to block too many addresses in a single transaction
    Given there are at least 515 different addresses registered
    When Alex tries to block receiving transactions from 513 different addresses
    Then Alex should receive the error "Failure_RestrictionAccount_Modification_Count_Exceeded"

  Scenario: 16. An account tries to only allow too many addresses in a single transaction
    Given there are at least 515 different addresses registered
    When Alex tries to only allow receiving transactions from 513 different addresses
    Then Alex should receive the error "Failure_RestrictionAccount_Modification_Count_Exceeded"

  Scenario Outline: 17. An account tries to block an invalid address
    When Alex tries to block receiving transactions from "<address>"
    Then Alex should receive the error "<error>"

    Examples:
      | address                                        | error                        |
      | SAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H  | Failure_Core_Invalid_Address |
      | bo                                             | Failure_Core_Invalid_Address |
      | MAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H5 | Failure_Core_Wrong_Network   |

  Scenario Outline: 18. An account tries only allow transactions from an invalid address
    When Alex tries to only allow receiving transactions from "<address>"
    Then Alex should receive the error "<error>"

    Examples:
      | address                                        | error                        |
      | SAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H  | Failure_Core_Invalid_Address |
      | bo                                             | Failure_Core_Invalid_Address |
      | MAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H5 | Failure_Core_Wrong_Network   |