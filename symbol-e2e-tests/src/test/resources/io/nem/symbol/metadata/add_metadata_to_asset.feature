Feature: Associate metadata with an account
  As Alice, I want to be able attach documents my account that it
  will be public available to other who need it.

  Sarah is a recent graduate with a multiple certificates
  Bob works as a digital notary that stamp accounts on Symbol’s public blockchain.
  He notarize a document, then tags the customer’s account with the digitized document as metadata.

  Background:
    Given Sarah has 30 units of the network currency
    And Sarah registered the asset "sto_token"

  @bvt
  Scenario: Bob wants to add a name to Sarah's asset
    Given Bob request Sarah to add a document "name" to asset "sto_token"
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah asset "sto_token" should have document "name" attached by Bob

  Scenario Outline: Bob wants to update the name on Sarah's asset
    Given Bob added a document "name" to Sarah asset "sto_token"
    And Bob request to update the "name" on Sarah asset "sto_token" with change of <difference> characters
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah asset "sto_token" should have document "name" attached by Bob

    Examples:
      | difference |
      | -7         |
      |  0         |
      | 11         |

  @bvt
  Scenario: Sarah wants to add multiple documents to her asset
    Given Sarah added a document "name" to asset "sto_token"
    And Bob request Sarah to add a document "name" to asset "sto_token"
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah asset "sto_token" should have document "name" attached by Bob

  @bvt
  Scenario: Sarah wants to add information to her asset
    Given Sarah adds a document "name" to asset "sto_token"
    When Sarah published the contract
    Then Sarah should have document "name" attached to asset "sto_token"

  Scenario Outline: Sarah wants to update the information on her asset
    Given Sarah added a document "name" to asset "sto_token"
    And Sarah updates document "name" on asset "sto_token" with change of <difference> characters
    Then Sarah asset "sto_token" should have document "name" attached by Sarah

    Examples:
      | difference |
      | -2         |
      | 0          |
      | 8          |

  @bvt
  Scenario: Sarah wants to add a document to asset using an alias
    Given Sarah registered the namespace "sarah"
    And Sarah links the namespace "sarah" to the address of Sarah
    When Bob adds document "name" to asset "sto_token" using Sarah alias "sarah"
    Then Sarah asset "sto_token" should have document "name" attached by Sarah

  Scenario: Sarah can overwrite a document already attached to her asset
    Given Sarah added a document "name" to asset "sto_token"
    When Sarah added a document "name" to asset "sto_token"
    Then Sarah asset "sto_token" should have document "name" attached by Tom

  Scenario: Sarah tries to add a document where the length does not match the document
    When Sarah tries to add a document with invalid length to asset "sto_token"
    Then Sarah should receive the error "FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH"

  Scenario: Sarah tries to update a document where the delta does not match the document
    Given Sarah added a document "name" to asset "sto_token"
    When Sarah tries to update document "name" with invalid length to asset "sto_token"
    Then Sarah should receive the error "FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH"

  Scenario: Sarah tries to add a document without using aggregate transaction
    When Sarah tries to add a document to asset "sto_token" without embedded in aggregate transaction
    Then Sarah balance should remain intact

#  Scenario: Sarah tries to update a document with invalid length
#    Given Sarah creates a document call "account info" to add to her account
#    When Sarah tries to update the document of length <length>
#    Then Sarah should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"
#      | length        | error |
#      | 0 | FAILURE_CORE_INSUFFICIENT_BALANCE  |
#      | -1 | FAILURE_CORE_INSUFFICIENT_BALANCE    |
#      | -1 | FAILURE_CORE_INSUFFICIENT_BALANCE    |
#
#  Scenario: Sarah tries to add a document with invalid length
#    When Sarah tries to add a document of length <length>
#    Then Sarah should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"
#      | length        | error |
#      | 0 | Failure_Metadata_Value_Too_Small  |
#      | -1 | FAILURE_CORE_INSUFFICIENT_BALANCE    |
#      | -1 | FAILURE_CORE_INSUFFICIENT_BALANCE    |
