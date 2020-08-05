@bvt
Feature: Get balance changes
  As Alice
  I want to know why my balance has changed
  If I have not received or sent any asset recently

    Given the native currency asset is "network currency"
    And an escrow contract requires to lock at least 10 "network currency" to guarantee that the it will conclude

  # Core
  @bvt
  Scenario: Tom wants to see her resulting fees after harvesting a block
    Given Tom is running a node
    And Tom account has harvested a block
    When Tom checks the fees obtained
    Then Tom should be able to see the resulting fees

    @Manual
  Scenario: Alice wants to see her resulting fees after harvesting a block using a remote account
    Given Dan is running a node
    And Alice delegated her account importance to "Bob"
    And Alice has 10000 "network currency" in her account
    When she checks the fees obtained
    Then Alice should be able to see the resulting fees

  # HashLock
  @bvt
  Scenario: An account wants to check if some funds were locked
    Given Alice defined the following bonded escrow contract:
      | type           | sender   | recipient | data                 |
      | send-an-asset  | Alice    | Tom       | 1 network currency   |
      | send-an-asset  | Bob      | Sue       | 2 network currency   |
    And Alice locks 10 "network currency" to guarantee that the contract will conclude 5 blocks
    When she checks if the locked mosaics for the previous transaction have been locked
    Then Alice should have 10 "network currency" sent from her account

  @bvt
  Scenario: An account wants to check if the escrow contract completed
    Given Alice created a 1 of 2 multisignature contract called "deposit" with 1 required for removal with cosignatories:
      | cosignatory |
      | Tom         |
      | phone       |
    When she checks if the contract has concluded
    Then Alice should get 10 "network currency" returned to her account

  @bvt
  Scenario: An account wants to check if the lock expired
    Given Alice defined the following bonded escrow contract:
      | type           | sender   | recipient | data                 |
      | send-an-asset  | Alice    | Tom       | 1 network currency   |
      | send-an-asset  | Bob      | Sue       | 2 network currency   |
    And Alice locks 10 "network currency" to guarantee that the contract will conclude 1 blocks
    And the hash lock expires
    When she checks if the lock has expired
    Then harvesting account should get 10 "network currency" from the hash lock
    And Alice "network currency" balance should decrease by 10 units

  # SecretLock
  @bvt
  Scenario: An account wants to check if assets are locked
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Tom on the network for 5 blocks
    When she checks if the locked mosaics for the previous secret transaction have been locked
    Then Alice should have 10 "network currency" in the secret lock

  @bvt
  Scenario: An account wants to check if a lock was proved
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Tom on the network for 5 blocks
    And Tom proved knowing the secret's seed on the network
    When Alice checks if the previous transaction has been proved
    Then Alice can verify that Tom receive 10 "network currency"

  @bvt
  Scenario: An account wants to check if a lock expired
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "network currency" for Tom on the network for 1 block
    And the secret lock expires
    When Alice checks if the previous transaction has expired
    Then Alice should have 10 "network currency" return from the secret lock
