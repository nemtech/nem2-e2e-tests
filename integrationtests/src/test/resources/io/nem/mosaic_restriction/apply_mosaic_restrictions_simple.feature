Feature: Apply global restrictions on mosaics
  As Alex I want to put one or more restrictions on mosaics
  So that accounts that do not pass the restriction cannot transact with the mosaic

  Background:
    # This step registers every user with cat.currency
    Given the following accounts exist:
      | Alex                |
      | Bobby               |
      | Carol               |
      | EligibilityProvider |
    And Alex has the following mosaics registered
      | Mosaic                 | Restrictable |
      | MyCompanySharesPrivate | true         |
      | MyCompanySharesPublic  | false        |
    And Bobby has at least 10 MyCompanySharesPrivate balance

  Scenario: 1. An account that doesn't pass the restriction cannot transact with the mosaic
    Given Alex creates the following restriction
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 0                 |
    And Alex gives Carol the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 0                 |
    When Bobby tries to send 1 asset "MyCompanySharesPrivate" to Carol
    Then Bobby should receive the error "Failure_RestrictionMosaic_Account_Unauthorized"

  Scenario: 2. An account that passes the restriction should be able to transact with the mosaic
    Given Alex creates the following restriction
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    And Carol gives Bobby the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    When Bobby sends 1 asset "MyCompanySharesPrivate" to Carol
    Then Carol should receive 1 of asset "MyCompanySharesPrivate"

  Scenario: 3. Make a modification to a mosaic restriction
    Given Alex creates the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 2                 | EQ               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    And Alex gives Carol the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    When Alex makes a modification to the mosaic restriction
      | Mosaic                 | Restriction Key | New Restriction value | New Restriction Type | Previous Restriction Value |
      | MyCompanySharesPrivate | can_hold        | 1                     | EQ                   | 2                          |
    And Bobby sends 1 asset "MyCompanySharesPrivate" to Carol
    Then Carol should receive 1 of asset "MyCompanySharesPrivate"

  Scenario: 4. An account that passes multiple restrictions can interact with the mosaic
    Given Alex creates the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
      | MyCompanySharesPrivate | can_share       | 1                 | EQ               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | Restriction key | Restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
      | MyCompanySharesPrivate | can_share       | 1                 |
    And Alex gives Carol the following restriction keys
      | Mosaic                 | Restriction key | Restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
      | MyCompanySharesPrivate | can_share       | 1                 |
    When Bobby sends 1 asset "MyCompanySharesPrivate" to Carol
    Then Carol should receive 1 of asset "MyCompanySharesPrivate"

#  Scenario: 5. An account that cannot pass the right restriction cannot do the corresponding transaction with mosaic
#    Given Alex creates the following restrictions
#      | Mosaic                  | Restriction Key     |  Restriction value    |   Restriction Type     |
#      | MyCompanySharesPrivate  | can_hold            |  1                    |   EQ                   |
#      | MyCompanySharesPrivate  | can_share           |  1                    |   EQ                   |
#    And only users with following restriction can send mosaics to other users
#      | MyCompanySharesPrivate  | can_share           |  1                    |   EQ                   |
#    And Bobby has the following restriction keys
#      | Mosaic                  | restriction key       | restriction value       |
#      | MyCompanySharesPrivate  | can_hold              |         1               |
#      | MyCompanySharesPrivate  | can_share             |         0               |
#    When Bobby tries to send 1 asset "exp.currency" to Carol
#    Then Bobby should receive the error

  Scenario: 6. Creating restrictions on a non-restrictable mosaic should give an error
    Given Alex tries to create the following restrictions
      | Mosaic                | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPublic | can_hold        | 1                 | EQ               |
    Then Alex should receive the error "FAILURE_MOSAIC_REQUIRED_PROPERTY_FLAG_UNSET"

  Scenario: 7. Creating restrictions on a mosaic that you do not own should give an error
    Given Bobby tries to create the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    Then Bobby should receive the error "FAILURE_MOSAIC_OWNER_CONFLICT"

  Scenario: 8. Creating restrictions on a mosaic with wrong previous value on global restriction should give an error
    Given Alex tries to create the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    And Alex tries to makes a modification to the mosaic restriction
      | Mosaic                 | Restriction Key | New Restriction value | New Restriction Type | Previous Restriction Value |
      | MyCompanySharesPrivate | can_hold        | 3                     | EQ                   | 2                          |
    Then Alex should receive the error "FAILURE_RESTRICTIONMOSAIC_PREVIOUS_VALUE_MISMATCH"

  Scenario: 9. Creating restrictions on a mosaic with wrong previous value on address restriction should give an error
    Given Alex creates the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    When Bobby has modified the following restriction keys
      | Mosaic                 | Restriction Key | New Restriction value | New Restriction Type | Previous Restriction Value |
      | MyCompanySharesPrivate | can_hold        | 3                     | EQ                   | 2                          |
    Then Bobby should receive the error "FAILURE_RESTRICTIONMOSAIC_PREVIOUS_VALUE_MISMATCH"

  Scenario: 10. Creating an address restriction without global restriction should give an error
    When Bobby tries to create the following restriction key
      | Mosaic                 | restriction key | restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    Then Bobby should receive the error "FAILURE_RESTRICTIONMOSAIC_UNKNOWN_GLOBAL_RESTRICTION"

#  This gives mosaic expired error instead of mosaic not found
  Scenario: 11. Creating an address restriction with a non-existing mosaic should give an error
    When Alex tries to create the following restrictions
      | Mosaic            | Restriction Key | Restriction value | Restriction Type |
      | MyCCSharesPrivate | can_hold        | 1                 | EQ               |
    Then Bobby should receive the error "FAILURE_MOSAIC_EXPIRED"

  Scenario: 12. Delete a global restriction on a mosaic
    Given Alex creates the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | Restriction key | Restriction value |
      | MyCompanySharesPrivate | can_hold        | 1                 |
    When Alex deletes the following restrictions
      | Mosaic                 | Restriction Key | Restriction value | Restriction Type |
      | MyCompanySharesPrivate | can_hold        | 1                 | EQ               |
    And Bobby tries to send 1 asset "MyCompanySharesPrivate" to Carol
    Then Bobby should receive the error "FAILURE_RESTRICTIONMOSAIC_ACCOUNT_UNAUTHORIZED"

  Scenario: 13. Delegate mosaic restrictions to a third party
    Given EligibilityProvider has the following mosaics registered
      | Mosaic | Restrictable |
      | kyc    | true         |
    And EligibilityProvider creates the following restrictions
      | Mosaic | Restriction Key | Restriction value | Restriction Type |
      | kyc    | Is_Verified     | 3                 | GT               |
    And Alex delegates the following restrictions keys to EligibilityProvider on mosaic MyCompanySharesPrivate
      | Mosaic | Restriction Key | Restriction value | Restriction Type |
      | kyc    | Is_Verified     | 3                 | GT               |
    And Alex gives Bobby the following restriction keys
      | Mosaic                 | Restriction key | Restriction value |
      | MyCompanySharesPrivate | Is_Verified     | 4                 |
    And EligibilityProvider gives Carol the following restriction keys
      | Mosaic                 | Restriction key | Restriction value |
      | MyCompanySharesPrivate | Is_Verified     | 4                 |
    When Bobby sends 1 asset "MyCompanySharesPrivate" to Carol
    Then Carol should receive 1 of asset "MyCompanySharesPrivate"