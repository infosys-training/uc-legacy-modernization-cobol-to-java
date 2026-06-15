import { balanceSheet, incomeStatement, companyInfo } from "../data/financialData";

export function calculateRatios() {
  const bs = balanceSheet;
  const is_ = incomeStatement;
  const ci = companyInfo;

  // Liquidity Ratios
  const currentRatio = bs.currentAssets.totalCurrentAssets / bs.currentLiabilities.totalCurrentLiabilities;
  const quickRatio = (bs.currentAssets.totalCurrentAssets - bs.currentAssets.inventory) / bs.currentLiabilities.totalCurrentLiabilities;
  const cashRatio = (bs.currentAssets.cashAndEquivalents + bs.currentAssets.shortTermInvestments) / bs.currentLiabilities.totalCurrentLiabilities;

  // Leverage Ratios
  const debtToEquity = bs.totalLiabilities / bs.shareholdersEquity.totalEquity;
  const debtToAssets = (bs.totalLiabilities / bs.totalAssets) * 100;

  // Coverage Ratios
  const interestCoverage = is_.operatingIncome / is_.interestExpense;
  const dscr = (is_.netIncome + is_.operatingExpenses.depreciationAmortization + is_.interestExpense) / ci.annualDebtService;

  // Profitability Ratios
  const grossMargin = (is_.grossProfit / is_.revenue) * 100;
  const netProfitMargin = (is_.netIncome / is_.revenue) * 100;
  const returnOnAssets = (is_.netIncome / bs.totalAssets) * 100;
  const returnOnEquity = (is_.netIncome / bs.shareholdersEquity.totalEquity) * 100;

  // Loan-Specific
  const ltv = (ci.loanAmount / ci.collateralValue) * 100;

  return [
    {
      name: "Current Ratio",
      value: currentRatio,
      format: "ratio",
      category: "Liquidity",
      benchmark: "≥ 1.5",
      description: "Measures ability to pay short-term obligations",
    },
    {
      name: "Quick Ratio",
      value: quickRatio,
      format: "ratio",
      category: "Liquidity",
      benchmark: "≥ 1.0",
      description: "Liquidity excluding inventory",
    },
    {
      name: "Cash Ratio",
      value: cashRatio,
      format: "ratio",
      category: "Liquidity",
      benchmark: "≥ 0.5",
      description: "Most conservative liquidity measure",
    },
    {
      name: "Debt to Equity Ratio",
      value: debtToEquity,
      format: "ratio",
      category: "Leverage",
      benchmark: "≤ 2.0",
      description: "Total liabilities relative to equity",
    },
    {
      name: "Debt to Assets Ratio",
      value: debtToAssets,
      format: "percent",
      category: "Leverage",
      benchmark: "≤ 60%",
      description: "Proportion of assets funded by debt",
    },
    {
      name: "Interest Coverage",
      value: interestCoverage,
      format: "times",
      category: "Coverage",
      benchmark: "≥ 3.0x",
      description: "Ability to meet interest payments",
    },
    {
      name: "Gross Margin",
      value: grossMargin,
      format: "percent",
      category: "Profitability",
      benchmark: "≥ 30%",
      description: "Revenue retained after direct costs",
    },
    {
      name: "Net Profit Margin",
      value: netProfitMargin,
      format: "percent",
      category: "Profitability",
      benchmark: "≥ 5%",
      description: "Percentage of revenue as net income",
    },
    {
      name: "Return on Assets (ROA)",
      value: returnOnAssets,
      format: "percent",
      category: "Profitability",
      benchmark: "≥ 5%",
      description: "Efficiency of asset utilization",
    },
    {
      name: "Return on Equity (ROE)",
      value: returnOnEquity,
      format: "percent",
      category: "Profitability",
      benchmark: "≥ 15%",
      description: "Return generated for shareholders",
    },
    {
      name: "DSCR",
      value: dscr,
      format: "times",
      category: "Coverage",
      benchmark: "≥ 1.25x",
      description: "Cash available to service debt",
    },
    {
      name: "LTV",
      value: ltv,
      format: "percent",
      category: "Loan-Specific",
      benchmark: "≤ 80%",
      description: "Loan amount relative to collateral value",
    },
  ];
}

export function formatRatioValue(value, format) {
  switch (format) {
    case "ratio":
      return value.toFixed(2);
    case "percent":
      return `${value.toFixed(1)}%`;
    case "times":
      return `${value.toFixed(2)}x`;
    default:
      return value.toFixed(2);
  }
}

export function getRatioStatus(ratio) {
  const { name, value } = ratio;
  switch (name) {
    case "Current Ratio":
      return value >= 1.5 ? "good" : value >= 1.0 ? "fair" : "poor";
    case "Quick Ratio":
      return value >= 1.0 ? "good" : value >= 0.7 ? "fair" : "poor";
    case "Cash Ratio":
      return value >= 0.5 ? "good" : value >= 0.3 ? "fair" : "poor";
    case "Debt to Equity Ratio":
      return value <= 1.5 ? "good" : value <= 2.0 ? "fair" : "poor";
    case "Debt to Assets Ratio":
      return value <= 50 ? "good" : value <= 60 ? "fair" : "poor";
    case "Interest Coverage":
      return value >= 3.0 ? "good" : value >= 2.0 ? "fair" : "poor";
    case "Gross Margin":
      return value >= 30 ? "good" : value >= 20 ? "fair" : "poor";
    case "Net Profit Margin":
      return value >= 8 ? "good" : value >= 5 ? "fair" : "poor";
    case "Return on Assets (ROA)":
      return value >= 5 ? "good" : value >= 3 ? "fair" : "poor";
    case "Return on Equity (ROE)":
      return value >= 15 ? "good" : value >= 10 ? "fair" : "poor";
    case "DSCR":
      return value >= 1.5 ? "good" : value >= 1.25 ? "fair" : "poor";
    case "LTV":
      return value <= 70 ? "good" : value <= 80 ? "fair" : "poor";
    default:
      return "fair";
  }
}
