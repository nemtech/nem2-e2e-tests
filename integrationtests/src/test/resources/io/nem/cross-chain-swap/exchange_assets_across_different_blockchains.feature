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
      | Keccak    |
      | Hash_160  |
      | Hash_256  |
    And "Alice" owns 999999 "alice:token" units in "MIJIN"
    And "Alice" owns an account in "MAIN_NET"
    And "Bob" owns 999999 bob:token units in "MAIN_NET"
    And "Bob" owns an account in "MIJIN"

  @bvt
  Scenario: An account locks assets
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Tom on the network for 5 blocks
    When Tom proved knowing the secret's seed on the network
    Then she should receive a confirmation message
    And Alice "cat.currency" balance should decrease by 10 units
    And Tom should receive 10 of asset "cat.currency"

  Scenario: An account locks assets with hash 160
    Given Alice derived the secret from the seed using "HASH_160"
    And Alice locked 10 "cat.currency" for Tom on the network for 5 blocks
    When Tom proved knowing the secret's seed on the network
    Then Alice "cat.currency" balance should decrease by 10 units
    And Tom should receive 10 of asset "cat.currency"

  @bvt
  Scenario: An exchange of assets across different blockchain concludes
    Given Alice derived the secret from the seed using "KECCAK_256"
    And Alice locked 10 "cat.currency" for Tom on the network for 10 blocks
    And Sue locked 10 "euros" for Alice on the network for 10 blocks
    When Alice proved knowing the secret's seed on the network
    And Tom proved knowing the secret's seed on the network
    Then Alice should receive 10 of asset "euros"
    And Tom should receive 10 of asset "cat.currency"

  @bvt
  Scenario: An exchange of assets doesn't conclude because the participant decides not locking the assets
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Bob on the network for 1 block
    When the secret lock expires
    Then Alice balance should remain intact

  Scenario: An exchange of assets doesn't conclude because there is not enough time
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Bob on the network for 1 block
    And the secret lock expires
    When Bob tries to prove the secret's seed on the network
    Then Bob should receive the error "FAILURE_LOCKSECRET_INACTIVE_SECRET"

  Scenario: An account tries to lock assets that does not have
    Given Sue derived the secret from the seed using "SHA3_256"
    When Sue tries to lock 10 "cat.currency" for Bob on the network for 1 block
    Then she should receive the error "FAILURE_CORE_INSUFFICIENT_BALANCE"

  Scenario Outline: An account tries to lock assets but the duration set is invalid
    Given Sue derived the secret from the seed using "SHA3_256"
    When Alice tries to lock 10 "cat.currency" for Bob on the network for <numberOfBlocks> blocks
    Then she should receive the error "FAILURE_LOCKSECRET_INVALID_DURATION"

    Examples:
      | numberOfBlocks |
      | -1             |
      | 178560         |

  Scenario: An account tries to lock assets using a used secret
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Bob on the network for 5 blocks
    When Alice tries to lock 10 "cat.currency" for Bob on the network for 5 blocks
    Then she should receive the error "FAILURE_LOCKSECRET_HASH_ALREADY_EXISTS"

  Scenario: An account tries to lock assets but the recipient address used is not valid
    Given Alice derived the secret from the seed using "SHA3_256"
    When Alice tries to lock 10 "cat.currency" for NAIBV5-BKEVGJ-IZQ4RP-224TYE-J3ZIUL-WDHUTI-X3HT on the network for 5 blocks
    Then she should receive the error "FAILURE_CORE_INVALID_ADDRESS"

  Scenario: An account tries to prove knowing a secret's seed that has not been used
    Given Alice derived the secret from the seed using "SHA3_256"
    When Alice tries to prove the secret's seed on the network
    Then she should receive the error "FAILURE_LOCKSECRET_UNKNOWN_COMPOSITE_KEY"

  Scenario: An account tries to unlock assets but the secret doesn't equal the hashed seed
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Bob on the network for 5 blocks
    When Alice tries to prove the secret's seed on the network but use the incorrect seed
    Then she should receive the error "FAILURE_LOCKSECRET_SECRET_MISMATCH"

  Scenario Outline: An account tries to unlock assets but the seed used was too large
    Given Alice generated a <length> characters length seed
    And  Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Bob on the network for 10 blocks
    When Bob tries to prove the secret's seed on the network
    Then she should receive the error "FAILURE_LOCKSECRET_PROOF_SIZE_OUT_OF_BOUNDS"

    Examples:
      | length |
      | 0      |
      | 1001   |

  Scenario: An account tries to unlock assets using a different algorithm
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Bob on the network for 5 blocks
    When Alice tries to prove knowing the secret's seed using "KECCAK_256" as the hashing algorithm
    Then she should receive the error "FAILURE_LOCKSECRET_SECRET_MISMATCH"

    #Restrictions
  Scenario: An account tries to lock assets but the recipient does not allow this asset
    Given Bob only allowed receiving "cat.currency" assets
    And Alice derived the secret from the seed using "SHA_512"
    When "Alice" locks the following asset units using the previous secret:
      | amount | asset       | recipient | network | hours |
      | 10     | alice.token | Bob       | MIJIN   | 96    |
    Then she should receive the error "Failure_RestrictionAccount_Mosaic_Transfer_Not_Allowed"

  Scenario: An account tries to lock assets but the recipient has blocked this asset
    Given Bob blocked receiving "alice.token" assets
    And Alice derived the secret from the seed using "SHA_512"
    When "Alice" locks the following asset units using the previous secret:
      | amount | asset       | recipient | network | hours |
      | 10     | alice.token | Bob       | MIJIN   | 96    |
    Then she should receive the error "Failure_RestrictionAccount_Mosaic_Transfer_Not_Allowed"

  Scenario: An account tries to lock assets but the recipient account does not allow receiving transactions from it
    Given Bob only allowed receiving transactions from Carol
    And Alice derived the secret from the seed using "SHA_512"
    When "Alice" locks the following asset units using the previous secret:
      | amount | asset       | recipient | network | hours |
      | 10     | alice.token | Bob       | MIJIN   | 96    |
    Then she should receive the error "Failure_RestrictionAccount_Address_Interaction_Not_Allowed"

  Scenario: An account tries to lock assets but the recipient has blocked it
    Given Bob blocked receiving transactions from Alice
    And Alice derived the secret from the seed using "SHA_512"
    When "Alice" locks the following asset units using the previous secret:
      | amount | asset       | recipient | network | hours |
      | 10     | alice.token | Bob       | MIJIN   | 96    |
    Then she should receive the error "Failure_RestrictionAccount_Address_Interaction_Not_Allowed"

  Scenario: An account tries to lock assets but has not allowed sending "LOCK_SECRET" transactions
    Given Alice only allowed sending "TRANSFER" transactions
    And Alice derived the secret from the seed using "SHA_512"
    When "Alice" locks the following asset units using the previous secret:
      | amount | asset       | recipient | network | hours |
      | 10     | alice.token | Bob       | MIJIN   | 96    |
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"

  Scenario: An account tries to lock assets but has blocked sending "LOCK_SECRET" transactions
    Given Alice blocked sending "LOCK_SECRET" transactions
    And Alice derived the secret from the seed using "SHA_512"
    When "Alice" locks the following asset units using the previous secret:
      | amount | asset       | recipient | network | hours |
      | 10     | alice.token | Bob       | MIJIN   | 96    |
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"

  # Account Restrictions
  Scenario: An account tries to unlock assets but has not allowed sending "SECRET_PROOF" transactions
    Given Alice only allowed sending "SECRET_PROOF" transactions
    And Bob derived the secret from the seed using "SHA_512"
    And "Bob" locked the following asset units using the previous secret:
      | amount | asset     | recipient | network  | hours |
      | 10     | bob.token | Alice     | MAIN_NET | 84    |
    When "Alice" proved knowing the secret's seed in "MAIN_NET"
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"

  Scenario: An account tries to unlock assets but has blocked sending "SECRET_PROOF" transactions
    Given Alice blocked sending "SECRET_PROOF" transactions
    And Bob derived the secret from the seed using "SHA_512"
    And "Bob" locked the following asset units using the previous secret:
      | amount | asset     | recipient | network  | hours |
      | 10     | bob.token | Alice     | MAIN_NET | 84    |
    When "Alice" proved knowing the secret's seed in "MAIN_NET"
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"
