@bvt
Feature: Get balance transfers
  As Alice
  I want to know why my balance was decrease
  after registering a mosaic or a namespace

    Given the native currency asset is "network currency"
    And creating a namespace costs 1 network currency per block
    And creating a subnamespace costs 100 network currency
    And registering an asset costs 500 network currency
    And the mean block generation time is 15 seconds
    And Alice has 10000000 "network currency" in her account

  # Mosaic
  @bvt
  Scenario: Alice wants to get the cost of registering an asset
    Given Alice registered the asset "alice_token"
    When Alice checks how much cost registering the asset
    Then Alice should get that registering the asset "alice_token" cost "500" network currency

  # Namespace
  @bvt
  Scenario Outline: Alice wants to get the cost of registering a namespace
    Given Alice registers a namespace named "<name>" for <duration> block
    When she checks how much cost registering the namespace
    Then Alice should get the namespace cost of "<cost>" network currency for registering "<name>"

    Examples:
      | name   | duration | cost |
      | first  | 6        | 6    |
      | second | 10       | 10   |

  Scenario Outline: Alice wants to get the cost of extending a namespace
    Given Alice registers a namespace named "token" for 6 block
    And Alice extends the registration of the namespace named "token" for <duration> block
    When she checks how much cost extending the namespace
    Then Alice should get that extending the namespace cost "<cost>" network currency

    Examples:
      | duration | cost |
      | 7        | 7    |
      | 9        | 9    |

