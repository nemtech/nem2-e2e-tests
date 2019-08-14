Feature: Edit a multisignature contract
  As Alice,
  I want to edit a multisignature contract,
  so that I have multi-factor authentication up-to-date.

    Given the maximum number of cosignatories per multisignature contract is 10
    And the maximum number of multisignature contracts an account can be cosignatory of is 5
    And  multisignature contracts created have set the minimum number of cosignatures to remove a cosignatory to 1

  @bvt
  Scenario: A cosignatory adds another cosignatory to the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | add       |
    And computer publishes the bonded contract
    And "tablet" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory adds another cosignatory to the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "Alice" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | add       |
    And Alice publishes the bonded contract
    And "tablet" accepts the transaction
    And "computer" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  @bvt
  Scenario: A cosignatory remove cosignatory to the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone       | remove    |
    And computer publishes the contract
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory adds and removes cosignatories from the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone       | remove    |
      | tablet      | add       |
    And computer publishes the bonded contract
    And "tablet" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory accepts the addition of another cosignatory to the multisignature contract
    Given Alice created a 2 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone       | remove    |
      | tablet      | add       |
    And computer publishes the bonded contract
    And "phone" accepts the transaction
    And "tablet" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory account removes itself from the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
    And computer publishes the contract
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: All cosignatories are removed from the multisignature contract
    Given Alice created a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
    When "computer" remove the last cosignatory of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
    And computer publishes the contract
    Then Alice should receive a confirmation message
    And tom become a regular account

 @bvt
 Scenario Outline: A cosignatory updates the minimum approval and removal requirements for a multisignature account
    Given Alice created a 2 of 4 multisignature contract called "tom" with 2 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
      | phone2      |
      | tablet      |
    When phone publishes a contract to change approval by <approval-delta> units and removal by <removal-delta> units
    And phone publishes the bonded contract
    And "computer" accepts the transaction
    Then the multisignature contract should be updated

    Examples:
      | approval-delta   | removal-delta     |
      | -1               | 0                 |
      | 0                | -1                |
      | 2                | 1                 |
      | 0                | 2                 |
      | 1                | 0                 |

  Scenario Outline: A cosignatory tries to set an invalid minimum of cosignatures to approve a transaction
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When phone publishes a contract to change approval by <minimum-approval> units and removal by <minimum-removal> units
    And phone publishes the contract
    Then she should receive the error "<error>"

    Examples:
      | minimum-approval |minimum-removal | error                                                             |
      | -1               | 0              | Failure_Multisig_Modify_Min_Setting_Out_Of_Range                  |
      | 0                | -1             | Failure_Multisig_Modify_Min_Setting_Out_Of_Range                  |
      | 2                | 0              | Failure_Multisig_Modify_Min_Setting_Larger_Than_Num_Cosignatories |
      | 1                | 2              | Failure_Multisig_Modify_Min_Setting_Larger_Than_Num_Cosignatories |

  Scenario: The last cosignatory tries to remove himself from the multisignature contract without
    Given Alice created a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
    And computer publishes the contract
    Then Alice should receive a confirmation message
    Then she should receive the error "Failure_Multisig_Modify_Min_Setting_Out_Of_Range"

  Scenario: A cosignatory tries adding twice another cosignatory to the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
      | phone       | add       |
    And phone publishes the bonded contract
    And "phone" accepts the transaction
    Then she should receive the error "Failure_Multisig_Modify_Already_A_Cosigner"

  Scenario: A cosignatory tries to add more than 10 cosignatories to the multisignature contract
    Given Alice created a 1 of 10 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | phone1      |
      | phone2      |
      | phone3      |
      | phone4      |
      | phone5      |
      | phone6      |
      | phone7      |
      | phone8      |
      | phone9      |
      | phone10     |
    When "phone1" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone11     | add       |
    And computer publishes the bonded contract
    And "phone11" accepts the transaction
    Then "Alice" should receive the error "Failure_Multisig_Modify_Max_Cosigners"

  Scenario: A cosignatory tries to add the multisignature contract as a cosignatory
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tom         | add       |
    And computer publishes the bonded contract
    Then "Alice" should receive the error "Failure_Multisig_Modify_Loop"

  Scenario: A cosignatory tries to add another cosignatory where the multisignature contract is a cosignatory.
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    Given Alice created a 1 of 2 multisignature contract called "phone" with 1 required for removal with cosignatories:
      | cosignatory |
      | app         |
      | browser     |
    When "browser" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tom         | add       |
    And browser publishes the bonded contract
    Then "Alice" should receive the error "Failure_Multisig_Modify_Loop"

  Scenario: A cosignatory tries to delete multiple cosignatories
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
      | phone       | remove    |
    And computer publishes the contract
    Then "Alice" should receive the error "Failure_Multisig_Modify_Multiple_Deletes"

  Scenario: A cosignatory tries to remove a cosignatory that does not exist
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | remove    |
    And computer publishes the contract
    Then "Alice" should receive the error "Failure_Multisig_Modify_Not_A_Cosigner"

  Scenario: A cosignatory tries to add and remove the same account as cosignatory at the same time
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | add       |
      | tablet      | remove    |
    And computer publishes the bonded contract
    And "tablet" accepts the transaction
    Then Alice should receive the error "Failure_Multisig_Modify_Account_In_Both_Sets"

