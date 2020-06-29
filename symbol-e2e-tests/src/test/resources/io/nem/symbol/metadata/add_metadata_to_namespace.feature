Feature: Associate metadata with a namespace
  As Sarah, I want to be able attach custom data to my namespace that
  will be public available to other who need it.

  Sarah is an admin in her company
  Bob works as a digital notary that stamp accounts on Symbol’s public blockchain.
  He notarize a document, then tags the customer’s account with the digitized document as metadata.

  @bvt
  Scenario: Tom wants to add his company information to his namespace
    Given Tom registered the namespace "tom"
    And Tom defined the following escrow contract:
      | type                   | sender | target | data               |
      | add-data-to-namespace  | Alice  | Bob    | 5 network currency |
    And Bob published the bonded contract
    When "Sarah" accepts the transaction
    Then Sarah should have her "college certificate" attached to the account by Bob
