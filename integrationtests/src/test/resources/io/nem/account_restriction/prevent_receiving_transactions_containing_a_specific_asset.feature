Feature: Prevent receiving transactions containing a specific asset
  As Bobby,
  I only want to receive "cat.currency" assets
  So that I can ensure I don't own assets unrelated with my activity

  Background:
    # This step registers every user with cat.currency
    Given the following accounts exist:
      | Alex  |
      | Bobby |
      | Carol |
    And Alex has the following assets registered and active:
      | ticket  |
      | voucher |

  Scenario: 1. An account blocks receiving transactions containing a specific asset
    Given Bobby blocks receiving transactions containing the following assets:
      | ticket  |
      | voucher |
    When Alex tries to send 1 asset "ticket" to Bobby
    Then Bobby should receive a confirmation message
    And Alex should receive the error "Failure_RestrictionAccount_Mosaic_Transfer_Prohibited"
    And Bobby balance should remain intact
    And Alex balance should remain intact

  Scenario Outline: 2. An account allows receiving transactions containing a specific asset
    When Bobby allows receiving transactions containing the following assets:
      | <asset> |
    And Alex sends <amount> asset "<asset>" to Bobby
    Then Bobby should receive a confirmation message
    And Bobby should receive <amount> of asset "<asset>"
    And Alex "<asset>" balance should decrease by <amount> units

    Examples:
      | amount | asset        |
      | 10     | cat.currency |
      | 10     | ticket       |

  Scenario: 3. An account unblocks an asset but tries to transfer a blocked asset
    Given Bobby blocks receiving transactions containing the following assets:
      | ticket  |
      | voucher |
    When Bobby removes ticket from blocked assets
    And Alex tries to send 1 asset "voucher" to Bobby
    Then Bobby should receive a confirmation message
#     And receiving voucher assets should remain blocked
      # This can be confirmed when Alex receives below error when he tries send a voucher asset to Bobby.
    And Alex should receive the error "Failure_RestrictionAccount_Mosaic_Transfer_Prohibited"

  Scenario: 4. An account removes an asset from the allowed assets
    Given Bobby has only allowed receiving the following assets
      | ticket  |
      | voucher |
    When Bobby removes ticket from allowed assets
    And Alex sends 1 asset "voucher" to Bobby
    Then Bobby should receive a confirmation message
#      And only receiving voucher assets should remain allowed
      # This can be confirmed when Alex successfully sends a voucher asset to Bobby.
    And Bobby should receive 1 of asset "voucher"

  Scenario: 5. An account unblocks a not blocked asset
    Given Bobby has blocked receiving ticket assets
    When Bobby tries to remove voucher from blocked assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Invalid_Modification"

  Scenario: 6. An account removes an asset that does not exist in the allowed assets
    Given Bobby has blocked receiving ticket assets
    When Bobby tries to remove voucher from allowed assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Invalid_Modification"

  Scenario: 7. An account tries only to allow receiving transactions containing specific assets when it has blocked assets
    Given Bobby has blocked receiving ticket assets
    When Bobby tries to only allow receiving voucher assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Invalid_Modification"

  Scenario: 8. An account tries to block receiving transactions containing specific assets when it has allowed assets
    Given Bobby has only allowed receiving ticket assets
    When Bobby tries to block receiving voucher assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Invalid_Modification"

  Scenario: 9. An account tries to block an asset twice
    Given Bobby has blocked receiving ticket assets
    When Bobby tries to block receiving ticket assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Invalid_Modification"

  Scenario: 10. An account tries to allow an asset twice
    Given Bobby has only allowed receiving ticket assets
    When Bobby tries to only allow receiving ticket assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Invalid_Modification"

  Scenario: 11. An account tries add too many restrictions in a single transaction
    Given Alex has 515 different assets registered and active
    When Bobby tries to add more than 512 restrictions in a transaction
    Then Bobby should receive the error "Failure_RestrictionAccount_Modification_Count_Exceeded"

  Scenario: 12. An account tries delete too many restrictions in a single transaction
    Given Alex has 515 different assets registered and active
    When Bobby tries to delete more than 512 restrictions in a transaction
    Then Bobby should receive the error "Failure_RestrictionAccount_Modification_Count_Exceeded"

  Scenario: 13. An account tries to block too many mosaics
    Given Bobby has already blocked receiving 512 different assets
    When Bobby tries to block receiving ticket assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Values_Count_Exceeded"

  Scenario: 14. An account tries to only allow too many mosaics
    Given Bobby has already allowed receiving 512 different assets
    When Bobby tries to only allow receiving ticket assets
    Then Bobby should receive the error "Failure_RestrictionAccount_Values_Count_Exceeded"