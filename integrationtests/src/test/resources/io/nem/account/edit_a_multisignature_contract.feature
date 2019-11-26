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
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | add       |
    And computer published the bonded contract
    When "tablet" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  @bvt
  Scenario: A cosignatory remove cosignatory to the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone       | remove    |
    When computer publishes the contract
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory adds and removes cosignatories from the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone       | remove    |
      | tablet      | add       |
    And computer published the bonded contract
    When "tablet" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory accepts the addition of another cosignatory to the multisignature contract
    Given Alice created a 2 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone       | remove    |
      | tablet      | add       |
    And computer published the bonded contract
    And "phone" accepted the transaction
    When "tablet" accepts the transaction
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: A cosignatory account removes itself from the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
    When computer publishes the contract
    Then Alice should receive a confirmation message
    And the multisignature contract should be updated

  Scenario: All cosignatories are removed from the multisignature contract
    Given Alice created a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
    And "computer" remove the last cosignatory of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
    When computer publishes the contract
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
    And phone created a contract to change approval by <approval-delta> units and removal by <removal-delta> units
    And phone published the bonded contract
    When "computer" accepts the transaction
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
    And phone created a contract to change approval by <minimum-approval> units and removal by <minimum-removal> units
    When phone publishes the contract
    Then she should receive the error "<error>"

    Examples:
      | minimum-approval |minimum-removal | error                                                      |
      | -1               | 0              | FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE                  |
      | 0                | -1             | FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE                  |
      | 2                | 0              | FAILURE_MULTISIG_MIN_SETTING_LARGER_THAN_NUM_COSIGNATORIES |
      | 1                | 2              | FAILURE_MULTISIG_MIN_SETTING_LARGER_THAN_NUM_COSIGNATORIES |

  Scenario: The last cosignatory tries to remove himself from the multisignature contract without
    Given Alice created a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
    When computer publishes the contract
    Then Alice should receive a confirmation message
    Then she should receive the error "FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE"

  Scenario: A cosignatory tries adding twice another cosignatory to the multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
      | phone       | add       |
    And phone published the bonded contract
    When "phone" accepts the transaction
    Then she should receive the error "FAILURE_MULTISIG_ALREADY_A_COSIGNATORY"

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
    And "phone1" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | phone11     | add       |
    And computer published the bonded contract
    And "phone11" accepts the transaction
    Then "Alice" should receive the error "FAILURE_MULTISIG_MAX_COSIGNATORIES"

  Scenario: A cosignatory tries to add the multisignature contract as a cosignatory
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tom         | add       |
    When computer publishes the bonded contract
    Then "Alice" should receive the error "FAILURE_MULTISIG_LOOP"

  Scenario: A cosignatory tries to add another cosignatory where the multisignature contract is a cosignatory.
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    Given Alice created a 1 of 2 multisignature contract called "phone" with 1 required for removal with cosignatories:
      | cosignatory |
      | app         |
      | browser     |
    And "browser" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tom         | add       |
    When browser publishes the bonded contract
    Then "Alice" should receive the error "FAILURE_MULTISIG_MAX_MULTISIG_DEPTH"

  Scenario: A cosignatory tries to delete multiple cosignatories
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | computer    | remove    |
      | phone       | remove    |
    When computer publishes the contract
    Then "Alice" should receive the error "FAILURE_MULTISIG_MULTIPLE_DELETES"

  Scenario: A cosignatory tries to remove a cosignatory that does not exist
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | remove    |
    When computer publishes the contract
    Then "Alice" should receive the error "FAILURE_MULTISIG_NOT_A_COSIGNATORY"

  Scenario: A cosignatory tries to add and remove the same account as cosignatory at the same time
    Given Alice created a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And "computer" update the cosignatories of the multisignature:
      | cosignatory | operation |
      | tablet      | add       |
      | tablet      | remove    |
    And computer published the bonded contract
    When "tablet" accepts the transaction
    Then Alice should receive the error "FAILURE_MULTISIG_ACCOUNT_IN_BOTH_SETS"

