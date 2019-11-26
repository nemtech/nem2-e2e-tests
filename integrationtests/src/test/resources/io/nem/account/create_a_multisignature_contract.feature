Feature: Create a multisignature contract
  As Alice,
  I want to create a multisignature contract between my phone and my computer,
  so that I have multi factor-authentication.

    Given the maximum number of cosignatories per multisignature contract is 10
    And the maximum number of multisignature contracts an account can be cosignatory of is 5

  @bvt
  Scenario Outline: An account creates an M-of-N contract
    Given Alice defined a <minimumApproval> of 2 multisignature contract called "tom" with <minimumRemoval> required for removal with cosignatories:
      | cosignatory |
      | phone       |
      | computer    |
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then she should receive a confirmation message
    And Alice account is convert to multisig

    Examples:
      | minimumApproval | minimumRemoval |
      | 1               | 2              |
      | 2               | 1              |

    @bvt
 Scenario Outline: An account tries to create a multisignature contract, setting an invalid values
    Given Alice defined a <minimumApproval> of 2 multisignature contract called "tom" with <minimumRemoval> required for removal with cosignatories:
      | cosignatory |
      | phone       |
      | computer    |
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then she should receive the error "<error>"

    Examples:
      | minimumApproval | minimumRemoval | error                                                             |
      | 3               | 2              | FAILURE_MULTISIG_MIN_SETTING_LARGER_THAN_NUM_COSIGNATORIES |
      | 2               | 3              | FAILURE_MULTISIG_MIN_SETTING_LARGER_THAN_NUM_COSIGNATORIES |
      | -1              | 1              | FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE                  |
      | 1               | -1             | FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE                  |
      | 1               | 0              | FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE                  |
      | 0               | 1              | FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE                  |

  Scenario: An account tries to create a multisignature contract adding twice the same cosignatory
    Given Alice defined a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | phone       |
      | phone       |
    And Alice published the bonded contract
    When "phone" accepts the transaction
    Then she should receive the error "FAILURE_MULTISIG_REDUNDANT_MODIFICATION"

  Scenario: An account tries to create a multisignature contract with more than 10 cosignatories
    Given Alice defined a 1 of 11 multisignature contract called "tom" with 1 required for removal with cosignatories:
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
      | phone11     |
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then she should receive the error "FAILURE_MULTISIG_MAX_COSIGNATORIES"

  Scenario: An account tries to add as a cosignatory an account which is already cosignatory of 5 multisignature contracts
    Given Bob is cosignatory of 5 multisignature contracts
    And Alice defined a 1 of 2 multisignature contract called "tom5" with 1 required for removal with cosignatories:
      | cosignatory |
      | Bob         |    
      | phone       |
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then she should receive the error "FAILURE_MULTISIG_MAX_COSIGNED_ACCOUNTS"

  Scenario: An account tries to create a multisignature contract adding itself as a cosignatory
    Given Alice defined a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | tom         |
    When Alice published the bonded contract
    Then she should receive the error "FAILURE_MULTISIG_LOOP"

  Scenario: An account tries to create a multisignature contract, adding a multisig cosignatory where the account is a cosignatory.
    Given Alice created a 1 of 2 multisignature contract called "deposit" with 1 required for removal with cosignatories:
      | cosignatory |
      | Tom         |
      | phone       | 
    And Alice defined a 1 of 2 multisignature contract called "Tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | deposit     |
      | phone       | 
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then she should receive the error "FAILURE_MULTISIG_LOOP"

  Scenario: An account tries to turn twice an account to multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "Dan" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And Alice defined a 1 of 2 multisignature contract called "Dan" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When Alice published the bonded contract
    Then she should receive the error "FAILURE_MULTISIG_OPERATION_PROHIBITED_BY_ACCOUNT"

  Scenario: An account creates a multi-level multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "level" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And Alice defined a 1 of 2 multisignature contract called "level2" with 1 required for removal with cosignatories:
      | cosignatory |
      | level       |
      | phone       | 
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then the multisignature contract should become a 2 level multisignature contract

  Scenario: An account tries to exceed three levels of nested multisignature contracts
    Given Alice created a 1 of 2 multisignature contract called "level1" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And Alice created a 1 of 2 multisignature contract called "level2" with 1 required for removal with cosignatories:
      | cosignatory |
      | level1      |
      | phone1      |
    And Alice created a 1 of 2 multisignature contract called "level3" with 1 required for removal with cosignatories:
      | cosignatory |
      | level2      |
      | phone2      |
    And Alice defined a 1 of 2 multisignature contract called "level4" with 1 required for removal with cosignatories:
      | cosignatory |
      | level3      |
      | phone3      |
    And Alice published the bonded contract
    When all the required cosignatories sign the transaction
    Then she should receive the error "FAILURE_MULTISIG_MAX_MULTISIG_DEPTH"