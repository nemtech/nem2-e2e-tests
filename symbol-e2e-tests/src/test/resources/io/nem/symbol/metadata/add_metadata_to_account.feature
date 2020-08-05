Feature: Associate metadata with an account
  As Alice, I want to be able attach documents my account that it
  will be public available to other who need it.

  Sarah is a recent graduate with a multiple certificates
  Bob works as a digital notary that stamp accounts on Symbol’s public blockchain.
  He notarize a document, then tags the customer’s account with the digitized document as metadata.

  Background:
    Given Sarah has 30 units of the network currency

  @bvt
  Scenario: Sarah wants to add a notarized certificate to her account
    Given Sarah request Bob to notarized her "college certificate"
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah should have her "college certificate" attached to the account by Bob

  Scenario Outline: Sarah wants to update her notarized certificate on her account
    Given Sarah added "college certificate" notarized by Bob to account
    And Sarah requested Bob to update the "college certificate" on account with change of <difference> characters
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah should have her "college certificate" attached to the account by Bob

    Examples:
      | difference |
      | -6         |
      |  0         |
      | 10         |

  @bvt
  Scenario: Sarah wants to add multiple notarized certificate to her account
    Given Sarah added "college certificate" notarized by Bob to account
    And Sarah request Tom to notarized her "college certificate"
    And Tom published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah should have her "college certificate" attached to the account by Tom

  @bvt
  Scenario: Sarah wants to add information to her account
    Given Sarah adds a document "account info" to her account
    When Sarah published the contract
    Then Sarah should have her "account info" attached to her account

  Scenario Outline: Sarah wants to update information attached to her account
    Given Sarah added a document "account info" to her account
    When Sarah updates document "account info" on her account with change of <difference> characters
    Then Sarah should have her "account info" attached to her account

    Examples:
      | difference |
      | -2         |
      | 0          |
      | 8          |

  @bvt
  Scenario: Sarah wants to add account information to her account using an alias
    Given Sarah registered the namespace "sarah"
    And Sarah links the namespace "sarah" to the address of Sarah
    When Sarah adds document "college certificate" notarized by Bob using her alias "sarah"
    Then Sarah should have her "college certificate" attached to the account by Bob

  Scenario: Sarah tries to add account information to her account using an incorrect alias
    Given Sarah registered the namespace "sarah"
    And Sarah registered the asset "T"
    And Sarah links the namespace "sarah" to the asset "T"
    When Sarah tries to add "college certificate" notarized by Bob using her alias "sarah"
    Then Sarah should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

  Scenario: Sarah can overwrite a document already attached to her account
    Given Sarah added "college certificate" notarized by Bob to account
    When Sarah added "college certificate" notarized by Bob to account
    Then Sarah should have her "college certificate" attached to the account by Bob

  Scenario: Sarah tries to add a document using her alias account which is not linked
    Given Sarah registered the namespace "sarah"
    When Sarah tries to add "college certificate" notarized by Bob using her alias "sarah"
    Then she can verify the transaction partial state in the DB

  Scenario: Sarah add a document using her alias account which forgot to linked
    Given Sarah registered the namespace "sarah"
    And Sarah tries to add "college certificate" notarized by Bob using her alias "sarah"
    And she can verify the transaction partial state in the DB
    When Sarah links the namespace "sarah" to the address of Sarah
    And "Sarah" resign the bonded transaction from Bob
    Then Sarah should have her "college certificate" attached to the account by Bob

  Scenario: Sarah tries to add a document where the length does not match the document
    When Sarah tries to add a document with invalid length
    Then Sarah should receive the error "FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH"

  Scenario: Sarah tries to update a document where the delta does not match the document
    Given Sarah added "college certificate" notarized by Bob to account
    When Sarah tries to update document "college certificate" with invalid length
    Then Sarah should receive the error "FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH"

  Scenario: Sarah tries to add a document without using aggregate transaction
    When Sarah tries to add a document without embedded in aggregate transaction
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
