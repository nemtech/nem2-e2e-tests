Feature: Associate metadata with a namespace
  As Sarah, I want to be able attach custom data to my namespace that
  will be public available to other who need it.

  Sarah is an admin in her company
  Bob works as a digital notary that stamp accounts on Symbol’s public blockchain.
  He notarize a document, then tags the customer’s account with the digitized document as metadata.

  Background:
    Given Sarah has 30 units of the network currency
    And Sarah registered the namespace "sto_token"

  @bvt
  Scenario: Bob wants to add a name to Sarah's namespace
    Given Bob request Sarah to add a document "name" to namespace "sto_token"
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah namespace "sto_token" should have document "name" attached by Bob

  Scenario Outline: Bob wants to update the name on Sarah's namespace
    Given Bob added a document "name" to Sarah namespace "sto_token"
    And Bob request to update the "name" on Sarah namespace "sto_token" with change of <difference> characters
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah namespace "sto_token" should have document "name" attached by Bob

    Examples:
      | difference |
      | -7         |
      |  0         |
      | 11         |

  @bvt
  Scenario: Sarah wants to add multiple documents to her namespace
    Given Sarah added a document "name" to namespace "sto_token"
    And Bob request Sarah to add a document "name" to namespace "sto_token"
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah namespace "sto_token" should have document "name" attached by Bob

  @bvt
  Scenario: Sarah wants to add information to her namespace
    Given Sarah adds a document "name" to namespace "sto_token"
    When Sarah published the contract
    Then Sarah should have document "name" attached to namespace "sto_token"

  Scenario Outline: Sarah wants to update the information on her namespace
    Given Sarah added a document "name" to namespace "sto_token"
    And Sarah updates document "name" on namespace "sto_token" with change of <difference> characters
    Then Sarah namespace "sto_token" should have document "name" attached by Sarah

    Examples:
      | difference |
      | -2         |
      | 0          |
      | 8          |

  @bvt
  Scenario: Sarah wants to add a document to namespace using an alias
    Given Sarah registered the namespace "sarah"
    And Sarah links the namespace "sarah" to the address of Sarah
    When Bob adds document "name" to namespace "sto_token" using Sarah alias "sarah"
    Then Sarah namespace "sto_token" should have document "name" attached by Sarah

  Scenario: Sarah can overwrite a document already attached to her namespace
    Given Sarah added a document "name" to namespace "sto_token"
    When Sarah added a document "name" to namespace "sto_token"
    Then Sarah namespace "sto_token" should have document "name" attached by Sarah

  Scenario: Sarah tries to add a document where the length does not match the document
    When Sarah tries to add a document with invalid length to namespace "sto_token"
    Then Sarah should receive the error "FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH"

  Scenario: Sarah tries to update a document where the delta does not match the document
    Given Sarah added a document "name" to namespace "sto_token"
    When Sarah tries to update document "name" with invalid length to namespace "sto_token"
    Then Sarah should receive the error "FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH"

  Scenario: Sarah tries to add a document without using aggregate transaction
    When Sarah tries to add a document to namespace "sto_token" without embedded in aggregate transaction
    Then Sarah balance should remain intact
