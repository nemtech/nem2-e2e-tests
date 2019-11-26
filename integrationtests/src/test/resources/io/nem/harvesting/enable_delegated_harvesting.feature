Feature: Enable delegated harvesting
  As Alice
  I want to enable delegated harvesting
  So that I can share my importance with a remote node securely to harvest new blocks.

  Scenario: An account enables delegated harvesting and delegates its importance to a remote account
    When Alice enables delegated harvesting delegating her account importance to D
    Then she should receive a confirmation message
    And D can use Alice's importance to harvest

  Scenario: An account disables delegated harvesting
    Given Alice delegated her account importance to D
    When she disables delegating her account importance to D
    Then she should receive a confirmation message
    And D can not use Alice's importance to harvest

  Scenario: An account tries to enable delegated harvesting twice
    Given Alice delegated his importance to D
    When she enables delegated harvesting delegating her account importance to D
    Then she should receive the error "FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS"

  Scenario: An account tries to disable delegated harvesting but it was not enabled
    When Alice disables delegating her account importance to D
    Then she should receive the error "Failure_AccountLink_Does_Not_Exist"

  Scenario: An account tries to enable delegated harvesting but the remote account was already linked
    Given Bob delegated his importance to D
    When Alice enables delegated harvesting delegating her account importance to D
    Then she should receive the error "FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_INELIGIBLE"

  Scenario: An account tries to enable delegated harvesting but the remote account owns mosaics
    Given D owns 10 cat.currency
    When Alice enables delegated harvesting delegating her account importance to D
    Then she should receive the error "FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_INELIGIBLE"

  Scenario: An account tries to enable delegated harvesting but the remote account is cosignatory
    Given D is cosignatory of an account
    When Alice enables delegated harvesting delegating her account importance to D
    Then she should receive the error "FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_INELIGIBLE"

  Scenario: An account tries to enable delegated harvesting but the remote account is a multisig
    Given D is a multisig account
    When Alice enables delegated harvesting delegating her account importance to D
    Then she should receive the error "FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_INELIGIBLE"

  Scenario: An account tries to enable delegated harvesting but the remote account has delegated its importance
    Given D has delegated its importance to D2
    When Alice enables delegated harvesting delegating her account importance to D
    Then she should receive the error "FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_INELIGIBLE"

  Scenario: An account tries to disable delegated harvesting from another account
    Given Bob has delegated her account importance to D
    When she disables delegating her account importance to D
    Then Alice should receive the error "FAILURE_ACCOUNTLINK_INCONSISTENT_UNLINK_DATA"

  Scenario: A remote account tries to sign a transaction
    Given Alice has delegated her account importance to D
    When D announces a transaction
    Then D should receive the error "Failure_AccountLink_Remote_Account_Signer_Not_Allowed"

  # Account Restrictions
  Scenario: An account tries to enable delegated harvesting but has not allowed sending "ACCOUNT_LINK" transactions
    Given Alice only allowed sending "TRANSFER" transactions
    When she enables delegated harvesting delegating her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"

  Scenario: An account tries to enable delegated harvesting but has blocked sending "ACCOUNT_LINK" transactions
    Given Alice blocked sending "ACCOUNT_LINK" transactions
    And Alice has delegated her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    When she enables delegated harvesting delegating her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"

  Scenario: An account tries to disable delegated harvesting but has not allowed sending "ACCOUNT_LINK" transactions
    Given Alice has delegated her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    And Alice only allowed sending "TRANSFER" transactions
    When she disables delegating her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"

  Scenario: An account tries to disable delegated harvesting but has blocked sending "ACCOUNT_LINK" transactions
    Given Alice has delegated her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    And Alice blocked sending "ACCOUNT_LINK" transactions
    When she disables delegating her account importance to "54BEF898980B8C4EBF81894775994FB0255BA4D5926126865FBA544360A0FDEE"
    Then she should receive the error "Failure_RestrictionAccount_Transaction_Type_Not_Allowed"
