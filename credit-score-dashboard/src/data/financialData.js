// Sample Financial Statements for ABC Corporation
// Commercial Loan Application - FY 2023-2024

export const companyInfo = {
  name: "ABC Corporation",
  industry: "Manufacturing",
  loanType: "Commercial Term Loan",
  loanAmount: 5000000,
  collateralValue: 7500000,
  annualDebtService: 750000,
  applicationDate: "2024-03-15",
};

export const balanceSheet = {
  // Assets
  currentAssets: {
    cashAndEquivalents: 1200000,
    accountsReceivable: 2800000,
    inventory: 3500000,
    prepaidExpenses: 350000,
    shortTermInvestments: 500000,
    totalCurrentAssets: 8350000,
  },
  nonCurrentAssets: {
    propertyPlantEquipment: 12000000,
    accumulatedDepreciation: -3200000,
    intangibleAssets: 1500000,
    longTermInvestments: 2000000,
    totalNonCurrentAssets: 12300000,
  },
  totalAssets: 20650000,

  // Liabilities
  currentLiabilities: {
    accountsPayable: 1800000,
    shortTermDebt: 1200000,
    currentPortionLongTermDebt: 600000,
    accruedExpenses: 900000,
    totalCurrentLiabilities: 4500000,
  },
  nonCurrentLiabilities: {
    longTermDebt: 5500000,
    deferredTaxLiabilities: 800000,
    totalNonCurrentLiabilities: 6300000,
  },
  totalLiabilities: 10800000,

  // Equity
  shareholdersEquity: {
    commonStock: 3000000,
    retainedEarnings: 5850000,
    additionalPaidInCapital: 1000000,
    totalEquity: 9850000,
  },
};

export const incomeStatement = {
  revenue: 28500000,
  costOfGoodsSold: 18500000,
  grossProfit: 10000000,
  operatingExpenses: {
    sellingExpenses: 2800000,
    generalAndAdministrative: 2200000,
    depreciationAmortization: 1200000,
    researchAndDevelopment: 800000,
    totalOperatingExpenses: 7000000,
  },
  operatingIncome: 3000000,
  interestExpense: 650000,
  otherIncome: 150000,
  incomeBeforeTax: 2500000,
  incomeTaxExpense: 625000,
  netIncome: 1875000,
};

export const cashFlowStatement = {
  operatingCashFlow: 2800000,
  investingCashFlow: -1500000,
  financingCashFlow: -800000,
  netCashFlow: 500000,
};
