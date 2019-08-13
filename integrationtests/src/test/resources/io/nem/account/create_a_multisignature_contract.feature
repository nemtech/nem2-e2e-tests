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
    When Alice publishes the bonded contract
    And cosignatories sign the transaction
    Then she should receive a confirmation message
    And her account is convert to multisig

    Examples:
      | minimumApproval | minimumRemoval |
      | 1               | 2              |
      | 2               | 1              |

 Scenario Outline: An account tries to create a multisignature contract, setting an invalid values
    Given Alice defined a <minimumApproval> of 2 multisignature contract called "tom" with <minimumRemoval> required for removal with cosignatories:
      | cosignatory |
      | phone       |
      | computer    |
    When Alice publishes the bonded contract
    And cosignatories sign the transaction
    Then she should receive the error "<error>"

    Examples:
      | minimumApproval | minimumRemoval | error                                                             |
      | 3               | 2              | Failure_Multisig_Modify_Min_Setting_Larger_Than_Num_Cosignatories |
      | 2               | 3              | Failure_Multisig_Modify_Min_Setting_Larger_Than_Num_Cosignatories |
      | -1              | 1              | Failure_Multisig_Modify_Min_Setting_Out_Of_Range                  |
      | 1               | -1             | Failure_Multisig_Modify_Min_Setting_Out_Of_Range                  |
      | 1               | 0              | Failure_Multisig_Modify_Min_Setting_Out_Of_Range                  |
      | 0               | 1              | Failure_Multisig_Modify_Min_Setting_Out_Of_Range                  |

  Scenario: An account tries to create a multisignature contract adding twice the same cosignatory
    Given Alice defined a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | phone       |
      | phone       |
    When Alice publishes the bonded contract
    Then she should receive the error "Failure_Multisig_Modify_Redundant_Modifications"

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
    When Alice publishes the bonded contract
    And cosignatories sign the transaction
    Then she should receive the error "Failure_Multisig_Modify_Max_Cosigners"

  Scenario: An account tries to add as a cosignatory an account which is already cosignatory of 5 multisignature contracts
    Given Bob is cosignatory of 5 multisignature contracts
    When Alice created a 1 of 2 multisignature contract called "tom5" with 1 required for removal with cosignatories:
      | cosignatory |
      | Bob         |    
      | phone       |  
    Then she should receive the error "Failure_Multisig_Modify_Max_Cosigned_Accounts"

  Scenario: An account tries to create a multisignature contract adding itself as a cosignatory
    Given Alice defined a 1 of 1 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | tom         |
    When Alice publishes the bonded contract
    Then she should receive the error "Failure_Multisig_Modify_Loop"

  Scenario: An account tries to create a multisignature contract, adding a multisig cosignatory where the account is a cosignatory.
    Given Alice created a 1 of 2 multisignature contract called "deposit" with 1 required for removal with cosignatories:
      | cosignatory |
      | Tom         |
      | phone       | 
    When Alice defined a 1 of 2 multisignature contract called "Tom" with 1 required for removal with cosignatories:
      | cosignatory |
      | deposit     |
      | phone       | 
    And Alice publishes the bonded contract
    And cosignatories sign the transaction
    Then she should receive the error "Failure_Multisig_Modify_Loop"

  Scenario: An account tries to turn twice an account to multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "Dan" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When Alice defined a 1 of 2 multisignature contract called "Dan" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    And Alice publishes the bonded contract
    Then she should receive the error "Failure_Multisig_Operation_Not_Permitted_By_Account"

  Scenario: An account tries to create a multisignature contract using an invalid address
    Given Alice defined a 1 of 2 multisignature contract called "tom" with 1 required for removal with cosignatories:
      | cosignatory                                    |
      | phone                                          |
      | LAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H5 |
    When Alice publishes the bonded contract
    And cosignatories sign the transaction
    Then she should receive the error "Failure_Core_Wrong_Network"

  Scenario: An account creates a multi-level multisignature contract
    Given Alice created a 1 of 2 multisignature contract called "level" with 1 required for removal with cosignatories:
      | cosignatory |
      | computer    |
      | phone       |
    When Alice defined a 1 of 2 multisignature contract called "level2" with 1 required for removal with cosignatories:
      | cosignatory |
      | level       |
      | phone       | 
    And Alice publishes the bonded contract
    And cosignatories sign the transaction
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
    When Alice created a 1 of 2 multisignature contract called "level4" with 1 required for removal with cosignatories:
      | cosignatory |
      | level3      |
      | phone3      | 
    Then she should receive the error "Failure_Multisig_Modify_Max_Multisig_Depth"