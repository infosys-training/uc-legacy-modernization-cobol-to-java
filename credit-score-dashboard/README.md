# Commercial Loan Credit Score Dashboard

A React-based dashboard application for evaluating the creditworthiness of ABC Corporation's commercial loan application.

## Features

- **Financial Statements**: Displays self-generated Balance Sheet, Profit & Loss Statement, and Cash Flow Summary
- **Financial Ratio Analysis**: Calculates and displays 12 key financial ratios:
  - Current Ratio, Quick Ratio, Cash Ratio (Liquidity)
  - Debt to Equity Ratio, Debt to Assets Ratio (Leverage)
  - Interest Coverage, DSCR (Coverage)
  - Gross Margin, Net Profit Margin, ROA, ROE (Profitability)
  - LTV (Loan-Specific)
- **Credit Assessment Questionnaire**: 10 pre-defined questions covering Business Profile, Management Quality, Industry Risk, Credit History, and Collateral Quality
- **Credit Score & Rating**: Derives a composite credit score (0-100) and letter rating (AAA to D) based on financial ratios (60%) and questionnaire responses (40%)

## Tech Stack

- React 19 + Vite
- Tailwind CSS 4
- No external API dependencies (self-contained sample data)

## Getting Started

```bash
cd credit-score-dashboard
npm install
npm run dev
```

Open http://localhost:5173 in your browser.

## Build for Production

```bash
npm run build
npm run preview
```

## Credit Scoring Methodology

| Component | Weight | Max Points |
|-----------|--------|------------|
| Financial Ratios | 60% | 60 |
| Assessment Questionnaire | 40% | 40 |
| **Total** | **100%** | **100** |

### Rating Scale

| Score | Rating | Risk Level |
|-------|--------|------------|
| 85-100 | AAA | Lowest Risk |
| 75-84 | AA | Very Low Risk |
| 65-74 | A | Low Risk |
| 55-64 | BBB | Moderate Risk |
| 45-54 | BB | Moderate-High Risk |
| 35-44 | B | High Risk |
| 25-34 | CCC | Very High Risk |
| 0-24 | D | Default / Extremely High Risk |
