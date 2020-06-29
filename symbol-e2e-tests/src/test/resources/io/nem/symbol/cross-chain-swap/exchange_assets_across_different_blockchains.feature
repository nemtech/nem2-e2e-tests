Feature: Exchange assets across different blockchains
  As Alice,
  I want to exchange assets with Bob across different blockchains,
  so that there is no need to trust in each other.

    Given assets can be locked up to 30 days
    And the mean block generation time is 15 seconds
    And the secret seed length should be between 10 and 1000 bytes
    And the maximum secret lock duration is 30 days
    And the following hashing algorithms are available:
      | hash_type |
      | Sha3_256  |
      | Hash_160  |
      | Hash_256  |
    And "Alice" owns 999999 "alice:token" units in "MIJIN"
    And "Alice" owns an account in "MAIN_NET"
    And "Bob" owns 999999 bob:token units in "MAIN_NET"
    And "Bob" owns an account in "MIJIN"

  @bvt
  Scenario: An account locks assets
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Tom on the network for 5 blocks
    When Tom proved knowing the secret's seed on the network
    Then Alice "network currency" balance should decrease by 10 units
    And Tom should receive 10 of asset "network currency"

  Scenario: An account locks assets with hash 160
    Given Alice derived the secret from the seed using "HASH_160"
    And Alice locked 10 "network currency" for Tom on the network for 5 blocks
    When Tom proved knowing the secret's seed on the network
    Then Alice "network currency" balance should decrease by 10 units
    And Tom should receive 10 of asset "network currency"

  @bvt
  Scenario: An exchange of assets across different blockchain concludes
    Given Alice derived the secret from the seed using "HASH_256"
    And Alice locked 10 "network currency" for Tom on the network for 10 blocks
    And Bob locked 10 "euros" for Alice on the network for 10 blocks
    When Alice proved knowing the secret's seed on the network
    And Tom proved knowing the secret's seed on the network
    Then Alice should receive 10 of asset "euros"
    And Tom should receive 10 of asset "network currency"

  @bvt
  Scenario: An exchange of assets doesn't conclude because the participant decides not locking the assets
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Bob on the network for 1 block
    When the secret lock expires
    Then Alice balance should remain intact

  Scenario: An exchange of assets doesn't conclude because there is not enough time
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Bob on the network for 1 block
    And the secret lock expires
    When Bob tries to prove the secret's seed on the network
    Then Bob should receive the error "FAILURE_LOCKSECRET_INACTIVE_SECRET"

  Scenario: An account tries to lock assets that does not have
    Given Dan derived the secret from the seed using "SHA3_256"
    When Dan tries to lock 10 "network currency" for Bob on the network for 1 block
    Then Dan should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

  Scenario Outline: An account tries to lock assets but the duration set is invalid
    Given Sue derived the secret from the seed using "SHA3_256"
    When Alice tries to lock 10 "network currency" for Bob on the network for <numberOfBlocks> blocks
    Then she should receive the error "FAILURE_LOCKSECRET_INVALID_DURATION"

    Examples:
      | numberOfBlocks |
      | -1             |
      | 178560         |

  Scenario: An account tries to lock assets using a used secret
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Bob on the network for 5 blocks
    When Alice tries to lock 10 "network currency" for Bob on the network for 5 blocks
    Then she should receive the error "FAILURE_LOCKSECRET_HASH_ALREADY_EXISTS"

  Scenario: An account tries to lock assets but the recipient address used is not valid
    Given Alice derived the secret from the seed using "SHA3_256"
    When Alice tries to lock 10 "network currency" for NAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3A on the network for 5 blocks
    Then she should receive the error "FAILURE_CORE_INVALID_ADDRESS"

  Scenario: An account tries to prove knowing a secret's seed that has not been used
    Given Alice derived the secret from the seed using "SHA3_256"
    When Alice tries to prove the secret's seed on the network
    Then she should receive the error "FAILURE_LOCKSECRET_UNKNOWN_COMPOSITE_KEY"

  Scenario: An account tries to unlock assets but the secret doesn't equal the hashed seed
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Bob on the network for 5 blocks
    When Alice tries to prove the secret's seed on the network but use the incorrect seed
    Then she should receive the error "FAILURE_LOCKSECRET_SECRET_MISMATCH"

  Scenario Outline: An account tries to unlock assets but the seed used was too large
    Given Alice generated a <length> characters length seed
    And  Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Bob on the network for 10 blocks
    When Bob tries to prove the secret's seed on the network
    Then she should receive the error "FAILURE_LOCKSECRET_PROOF_SIZE_OUT_OF_BOUNDS"

    Examples:
      | length |
      | 0      |
      | 1001   |

  Scenario: An account tries to unlock assets using a different algorithm
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Bob on the network for 5 blocks
    When Alice tries to prove knowing the secret's seed using "HASH_256" as the hashing algorithm
    Then she should receive the error "FAILURE_LOCKSECRET_SECRET_MISMATCH"