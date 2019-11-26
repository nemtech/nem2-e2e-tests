Feature: Can Bob transfer 1 assests to Jill?
  Bob owes Jill some money and wanted to know if he can
  send her 10 XEM on nem to pay his debt

  Scenario: Bob transfers 10 XEM to Jill
    Given Jill has an account on the Nem platform
    When Bob transfer 10 XEM to Jill
    Then Jill should have 10 XEM