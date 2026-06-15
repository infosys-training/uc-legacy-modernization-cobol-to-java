import { balanceSheet, incomeStatement, cashFlowStatement } from "../data/financialData";

function formatCurrency(value) {
  const absValue = Math.abs(value);
  const formatted = `$${absValue.toLocaleString()}`;
  return value < 0 ? `(${formatted})` : formatted;
}

function FinancialStatements() {
  return (
    <div className="space-y-6">
      {/* Balance Sheet */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="bg-indigo-50 px-6 py-4 border-b border-indigo-100">
          <h3 className="text-lg font-semibold text-indigo-900">
            Balance Sheet - ABC Corporation (FY 2023-2024)
          </h3>
        </div>
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Assets */}
            <div>
              <h4 className="font-semibold text-gray-800 mb-3 text-sm uppercase tracking-wide">
                Assets
              </h4>
              <div className="space-y-1">
                <p className="font-medium text-gray-700 mt-2">Current Assets</p>
                <LineItem label="Cash & Equivalents" value={balanceSheet.currentAssets.cashAndEquivalents} />
                <LineItem label="Accounts Receivable" value={balanceSheet.currentAssets.accountsReceivable} />
                <LineItem label="Inventory" value={balanceSheet.currentAssets.inventory} />
                <LineItem label="Prepaid Expenses" value={balanceSheet.currentAssets.prepaidExpenses} />
                <LineItem label="Short-term Investments" value={balanceSheet.currentAssets.shortTermInvestments} />
                <TotalLine label="Total Current Assets" value={balanceSheet.currentAssets.totalCurrentAssets} />

                <p className="font-medium text-gray-700 mt-4">Non-Current Assets</p>
                <LineItem label="Property, Plant & Equipment" value={balanceSheet.nonCurrentAssets.propertyPlantEquipment} />
                <LineItem label="Accumulated Depreciation" value={balanceSheet.nonCurrentAssets.accumulatedDepreciation} />
                <LineItem label="Intangible Assets" value={balanceSheet.nonCurrentAssets.intangibleAssets} />
                <LineItem label="Long-term Investments" value={balanceSheet.nonCurrentAssets.longTermInvestments} />
                <TotalLine label="Total Non-Current Assets" value={balanceSheet.nonCurrentAssets.totalNonCurrentAssets} />
                <TotalLine label="TOTAL ASSETS" value={balanceSheet.totalAssets} bold />
              </div>
            </div>

            {/* Liabilities & Equity */}
            <div>
              <h4 className="font-semibold text-gray-800 mb-3 text-sm uppercase tracking-wide">
                Liabilities & Equity
              </h4>
              <div className="space-y-1">
                <p className="font-medium text-gray-700 mt-2">Current Liabilities</p>
                <LineItem label="Accounts Payable" value={balanceSheet.currentLiabilities.accountsPayable} />
                <LineItem label="Short-term Debt" value={balanceSheet.currentLiabilities.shortTermDebt} />
                <LineItem label="Current Portion of LTD" value={balanceSheet.currentLiabilities.currentPortionLongTermDebt} />
                <LineItem label="Accrued Expenses" value={balanceSheet.currentLiabilities.accruedExpenses} />
                <TotalLine label="Total Current Liabilities" value={balanceSheet.currentLiabilities.totalCurrentLiabilities} />

                <p className="font-medium text-gray-700 mt-4">Non-Current Liabilities</p>
                <LineItem label="Long-term Debt" value={balanceSheet.nonCurrentLiabilities.longTermDebt} />
                <LineItem label="Deferred Tax Liabilities" value={balanceSheet.nonCurrentLiabilities.deferredTaxLiabilities} />
                <TotalLine label="Total Non-Current Liabilities" value={balanceSheet.nonCurrentLiabilities.totalNonCurrentLiabilities} />
                <TotalLine label="TOTAL LIABILITIES" value={balanceSheet.totalLiabilities} />

                <p className="font-medium text-gray-700 mt-4">Shareholders&apos; Equity</p>
                <LineItem label="Common Stock" value={balanceSheet.shareholdersEquity.commonStock} />
                <LineItem label="Retained Earnings" value={balanceSheet.shareholdersEquity.retainedEarnings} />
                <LineItem label="Additional Paid-in Capital" value={balanceSheet.shareholdersEquity.additionalPaidInCapital} />
                <TotalLine label="TOTAL EQUITY" value={balanceSheet.shareholdersEquity.totalEquity} bold />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Income Statement */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="bg-green-50 px-6 py-4 border-b border-green-100">
          <h3 className="text-lg font-semibold text-green-900">
            Profit & Loss Statement - ABC Corporation (FY 2023-2024)
          </h3>
        </div>
        <div className="p-6">
          <div className="max-w-lg space-y-1">
            <LineItem label="Revenue" value={incomeStatement.revenue} />
            <LineItem label="Cost of Goods Sold" value={incomeStatement.costOfGoodsSold} negative />
            <TotalLine label="Gross Profit" value={incomeStatement.grossProfit} />

            <p className="font-medium text-gray-700 mt-4">Operating Expenses</p>
            <LineItem label="Selling Expenses" value={incomeStatement.operatingExpenses.sellingExpenses} />
            <LineItem label="General & Administrative" value={incomeStatement.operatingExpenses.generalAndAdministrative} />
            <LineItem label="Depreciation & Amortization" value={incomeStatement.operatingExpenses.depreciationAmortization} />
            <LineItem label="Research & Development" value={incomeStatement.operatingExpenses.researchAndDevelopment} />
            <TotalLine label="Total Operating Expenses" value={incomeStatement.operatingExpenses.totalOperatingExpenses} />

            <TotalLine label="Operating Income (EBIT)" value={incomeStatement.operatingIncome} />
            <LineItem label="Interest Expense" value={incomeStatement.interestExpense} negative />
            <LineItem label="Other Income" value={incomeStatement.otherIncome} />
            <TotalLine label="Income Before Tax" value={incomeStatement.incomeBeforeTax} />
            <LineItem label="Income Tax Expense" value={incomeStatement.incomeTaxExpense} negative />
            <TotalLine label="NET INCOME" value={incomeStatement.netIncome} bold />
          </div>
        </div>
      </div>

      {/* Cash Flow */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="bg-amber-50 px-6 py-4 border-b border-amber-100">
          <h3 className="text-lg font-semibold text-amber-900">
            Cash Flow Summary - ABC Corporation (FY 2023-2024)
          </h3>
        </div>
        <div className="p-6">
          <div className="max-w-lg space-y-1">
            <LineItem label="Operating Cash Flow" value={cashFlowStatement.operatingCashFlow} />
            <LineItem label="Investing Cash Flow" value={cashFlowStatement.investingCashFlow} />
            <LineItem label="Financing Cash Flow" value={cashFlowStatement.financingCashFlow} />
            <TotalLine label="Net Cash Flow" value={cashFlowStatement.netCashFlow} bold />
          </div>
        </div>
      </div>
    </div>
  );
}

function LineItem({ label, value }) {
  return (
    <div className="flex justify-between py-1 text-sm">
      <span className="text-gray-600 pl-4">{label}</span>
      <span className="text-gray-800 font-mono">{formatCurrency(value)}</span>
    </div>
  );
}

function TotalLine({ label, value, bold }) {
  return (
    <div
      className={`flex justify-between py-1.5 text-sm border-t border-gray-200 ${
        bold ? "font-bold text-gray-900" : "font-semibold text-gray-700"
      }`}
    >
      <span>{label}</span>
      <span className="font-mono">{formatCurrency(value)}</span>
    </div>
  );
}

export default FinancialStatements;
