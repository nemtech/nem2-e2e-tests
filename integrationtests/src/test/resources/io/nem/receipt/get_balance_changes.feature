@bvt
Feature: Get balance changes
  As Alice
  I want to know why my balance has changed
  If I have not received or sent any asset recently

    Given the native currency asset is "cat.currency"
    And an escrow contract requires to lock at least 10 "cat.currency" to guarantee that the it will conclude

  # Core
  Scenario: Alice wants to see her resulting fees after harvesting a block
    Given Alice is running a node
    And Alice account has harvested a block
    When she checks the fees obtained
    Then Alice should be able to see the resulting fees

  Scenario: Alice wants to see her resulting fees after harvesting a block using a remote account
    Given Alice delegated her account importance to "Bob"
    And "Bob" is running the node
    And Alice has 10000 "cat.currency" in her account
    When she checks the fees obtained
    Then Alice should be able to see the resulting fees

  # HashLock
  Scenario: An account wants to check if some funds were locked
    Given Alice defined the following bonded escrow contract:
      | type           | sender   | recipient | data             |
      | send-an-asset  | Alice    | Tom       | 1 cat.currency   |
      | send-an-asset  | Bob      | Sue       | 2 cat.currency   |
    And Alice locks 10 "cat.currency" to guarantee that the contract will conclude 5 blocks
    When she checks if the locked mosaics for the previous transaction have been locked
    Then Alice should have 10 "cat.currency" sent from her account

  Scenario: An account wants to check if the escrow contract completed
    Given Alice created a 1 of 2 multisignature contract called "deposit" with 1 required for removal with cosignatories:
      | cosignatory |
      | Tom         |
      | phone       |
    When she checks if the contract has concluded
    Then Alice should get 10 "cat.currency" returned to her account

  Scenario: An account wants to check if the lock expired
    Given Alice defined the following bonded escrow contract:
      | type           | sender   | recipient | data             |
      | send-an-asset  | Alice    | Tom       | 1 cat.currency   |
      | send-an-asset  | Bob      | Sue       | 2 cat.currency   |
    And Alice locks 10 "cat.currency" to guarantee that the contract will conclude 1 blocks
    And the hash lock expires
    When she checks if the lock has expired
    Then harvesting account should get 10 "cat.currency" from the hash lock
    And Alice "cat.currency" balance should decrease by 10 units

  # SecretLock
  Scenario: An account wants to check if assets are locked
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Tom on the network for 5 blocks
    When she checks if the locked mosaics for the previous secret transaction have been locked
    Then Alice should have 10 "cat.currency" in the secret lock

  Scenario: An account wants to check if a lock was proved
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Tom on the network for 5 blocks
    And Tom proved knowing the secret's seed on the network
    When Alice checks if the previous transaction has been proved
    Then Alice can verify that Tom receive 10 "cat.currency"

  Scenario: An account wants to check if a lock expired
    Given Alice derived the secret from the seed using "SHA3_256"
    And Alice locked 10 "cat.currency" for Tom on the network for 1 block
    And the secret lock expires
    When Alice checks if the previous transaction has expired
    Then Alice should have 10 "cat.currency" return from the secret lock
