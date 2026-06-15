import { companyInfo } from "../data/financialData";

function Header() {
  return (
    <header className="bg-gradient-to-r from-indigo-700 to-indigo-900 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 py-5">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold">Commercial Loan Credit Score Dashboard</h1>
            <p className="text-indigo-200 text-sm mt-1">
              {companyInfo.name} | {companyInfo.industry} | Application Date:{" "}
              {companyInfo.applicationDate}
            </p>
          </div>
          <div className="text-right">
            <p className="text-sm text-indigo-200">Loan Amount Requested</p>
            <p className="text-xl font-bold">
              ${companyInfo.loanAmount.toLocaleString()}
            </p>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
