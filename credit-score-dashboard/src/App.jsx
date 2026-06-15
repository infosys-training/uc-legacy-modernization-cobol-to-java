import { useState, useMemo } from "react";
import Header from "./components/Header";
import FinancialStatements from "./components/FinancialStatements";
import RatiosDashboard from "./components/RatiosDashboard";
import Questionnaire from "./components/Questionnaire";
import CreditScoreDisplay from "./components/CreditScoreDisplay";
import { calculateRatios } from "./utils/ratioCalculations";
import {
  calculateRatioScore,
  calculateQuestionnaireScore,
  deriveCreditScore,
} from "./utils/creditScoring";

function App() {
  const [activeTab, setActiveTab] = useState("overview");
  const [answers, setAnswers] = useState({});

  const ratios = useMemo(() => calculateRatios(), []);
  const ratioScore = useMemo(() => calculateRatioScore(ratios), [ratios]);
  const questionnaireScore = useMemo(
    () => calculateQuestionnaireScore(answers),
    [answers]
  );
  const creditResult = useMemo(
    () => deriveCreditScore(ratioScore, questionnaireScore),
    [ratioScore, questionnaireScore]
  );

  const tabs = [
    { id: "overview", label: "Overview" },
    { id: "financials", label: "Financial Statements" },
    { id: "ratios", label: "Financial Ratios" },
    { id: "questionnaire", label: "Assessment Questions" },
    { id: "score", label: "Credit Score" },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-7xl mx-auto px-4 py-6">
        {/* Tab Navigation */}
        <nav className="flex space-x-1 bg-white rounded-lg p-1 shadow-sm mb-6">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex-1 py-2.5 px-4 rounded-md text-sm font-medium transition-all ${
                activeTab === tab.id
                  ? "bg-indigo-600 text-white shadow-sm"
                  : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>

        {/* Tab Content */}
        {activeTab === "overview" && (
          <OverviewTab creditResult={creditResult} ratios={ratios} />
        )}
        {activeTab === "financials" && <FinancialStatements />}
        {activeTab === "ratios" && <RatiosDashboard ratios={ratios} />}
        {activeTab === "questionnaire" && (
          <Questionnaire answers={answers} setAnswers={setAnswers} />
        )}
        {activeTab === "score" && (
          <CreditScoreDisplay
            creditResult={creditResult}
            ratios={ratios}
            answers={answers}
          />
        )}
      </main>
    </div>
  );
}

function OverviewTab({ creditResult, ratios }) {
  return (
    <div className="space-y-6">
      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <p className="text-sm text-gray-500">Credit Score</p>
          <p className="text-3xl font-bold mt-1" style={{ color: creditResult.color }}>
            {creditResult.totalScore}/100
          </p>
        </div>
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <p className="text-sm text-gray-500">Credit Rating</p>
          <p className="text-3xl font-bold mt-1" style={{ color: creditResult.color }}>
            {creditResult.rating}
          </p>
        </div>
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <p className="text-sm text-gray-500">Ratio Score</p>
          <p className="text-3xl font-bold mt-1 text-indigo-600">
            {creditResult.ratioScore}/60
          </p>
        </div>
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <p className="text-sm text-gray-500">Assessment Score</p>
          <p className="text-3xl font-bold mt-1 text-indigo-600">
            {creditResult.questionnaireScore}/40
          </p>
        </div>
      </div>

      {/* Quick Ratio Summary */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">
          Key Financial Ratios - ABC Corporation
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-3">
          {ratios.map((ratio) => {
            const status =
              ratio.category === "Liquidity"
                ? ratio.value >= 1.0
                  ? "text-green-600"
                  : "text-red-600"
                : ratio.category === "Leverage"
                ? ratio.value <= (ratio.name.includes("Assets") ? 60 : 2.0)
                  ? "text-green-600"
                  : "text-red-600"
                : ratio.category === "Profitability"
                ? ratio.value >= 5
                  ? "text-green-600"
                  : "text-red-600"
                : "text-blue-600";
            return (
              <div
                key={ratio.name}
                className="p-3 bg-gray-50 rounded-lg"
              >
                <p className="text-xs text-gray-500">{ratio.name}</p>
                <p className={`text-lg font-semibold ${status}`}>
                  {ratio.format === "ratio"
                    ? ratio.value.toFixed(2)
                    : ratio.format === "percent"
                    ? `${ratio.value.toFixed(1)}%`
                    : `${ratio.value.toFixed(2)}x`}
                </p>
              </div>
            );
          })}
        </div>
      </div>

      {/* Application Info */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">
          Loan Application Summary
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div>
            <p className="text-xs text-gray-500">Applicant</p>
            <p className="font-medium">ABC Corporation</p>
          </div>
          <div>
            <p className="text-xs text-gray-500">Industry</p>
            <p className="font-medium">Manufacturing</p>
          </div>
          <div>
            <p className="text-xs text-gray-500">Loan Amount</p>
            <p className="font-medium">$5,000,000</p>
          </div>
          <div>
            <p className="text-xs text-gray-500">Collateral Value</p>
            <p className="font-medium">$7,500,000</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
