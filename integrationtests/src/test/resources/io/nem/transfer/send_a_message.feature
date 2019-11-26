Feature: Send a message
  As Alice
  I want to send a message to Bob
  So that there is evidence that I have sent the message.

    Given the mean block generation time is 15 seconds
    And the maximum message length is 1024

  @bvt
  Scenario Outline: An account sends a message to another account

    When Alice sends "<message>" to "<recipient>"
    Then she should receive a confirmation message
    And the "<recipient>" should receive the message "<message>"
 
    Examples:
      | message | recipient    |
      | Hello   | Sue          |
      | Hi      | Bob          |

  Scenario Outline: An account tries to send a message to an invalid account

    When Alice tries to send "<message>" to "<recipient>"
    Then she should receive the error "<error>"

    Examples:
      | message | recipient                                      | error                        |
      | Hello   | NAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H5 | FAILURE_CORE_INVALID_ADDRESS |
      | Hello   | MAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3H5 | FAILURE_CORE_INVALID_ADDRESS |

  Scenario: An account tries to send a message to another account but the message is too large

    When Alice tries to send a 1024 character message to Bob
    Then she should receive the error "Failure_Transfer_Message_Too_Large"
