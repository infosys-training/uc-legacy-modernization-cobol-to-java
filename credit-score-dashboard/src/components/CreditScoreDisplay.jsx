import { formatRatioValue, getRatioStatus } from "../utils/ratioCalculations";
import { creditQuestions } from "../utils/creditScoring";

function CreditScoreDisplay({ creditResult, ratios, answers }) {
  const { totalScore, ratioScore, questionnaireScore, rating, ratingDescription, color } =
    creditResult;

  const answeredCount = Object.keys(answers).length;
  const totalQuestions = creditQuestions.length;

  return (
    <div className="space-y-6">
      {/* Main Score Display */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center">
        <h2 className="text-xl font-semibold text-gray-800 mb-6">
          Credit Score & Rating - ABC Corporation
        </h2>

        {/* Score Circle */}
        <div className="inline-flex flex-col items-center">
          <div
            className="w-40 h-40 rounded-full flex items-center justify-center border-8"
            style={{ borderColor: color }}
          >
            <div>
              <p className="text-4xl font-bold" style={{ color }}>
                {totalScore}
              </p>
              <p className="text-sm text-gray-500">/100</p>
            </div>
          </div>
          <div className="mt-4">
            <p className="text-3xl font-bold" style={{ color }}>
              {rating}
            </p>
            <p className="text-sm text-gray-600 mt-1">{ratingDescription}</p>
          </div>
        </div>

        {/* Score Breakdown Bar */}
        <div className="mt-8 max-w-md mx-auto">
          <div className="flex justify-between text-sm text-gray-600 mb-1">
            <span>Score Composition</span>
            <span>{totalScore}/100</span>
          </div>
          <div className="flex h-4 rounded-full overflow-hidden bg-gray-200">
            <div
              className="bg-indigo-600 transition-all"
              style={{ width: `${ratioScore}%` }}
              title={`Ratio Score: ${ratioScore}/60`}
            />
            <div
              className="bg-emerald-500 transition-all"
              style={{ width: `${questionnaireScore}%` }}
              title={`Questionnaire Score: ${questionnaireScore}/40`}
            />
          </div>
          <div className="flex justify-between text-xs text-gray-500 mt-1">
            <span className="flex items-center gap-1">
              <span className="w-3 h-3 bg-indigo-600 rounded-sm inline-block" />
              Financial Ratios: {ratioScore}/60
            </span>
            <span className="flex items-center gap-1">
              <span className="w-3 h-3 bg-emerald-500 rounded-sm inline-block" />
              Assessment: {questionnaireScore}/40
            </span>
          </div>
        </div>
      </div>

      {/* Rating Scale */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 className="font-semibold text-gray-800 mb-4">Credit Rating Scale</h3>
        <div className="grid grid-cols-4 md:grid-cols-8 gap-2">
          {[
            { r: "AAA", min: 85, c: "#10b981" },
            { r: "AA", min: 75, c: "#34d399" },
            { r: "A", min: 65, c: "#6ee7b7" },
            { r: "BBB", min: 55, c: "#fbbf24" },
            { r: "BB", min: 45, c: "#f59e0b" },
            { r: "B", min: 35, c: "#f97316" },
            { r: "CCC", min: 25, c: "#ef4444" },
            { r: "D", min: 0, c: "#991b1b" },
          ].map((item) => (
            <div
              key={item.r}
              className={`p-2 rounded-lg text-center text-white text-sm font-bold ${
                item.r === rating ? "ring-2 ring-offset-2 ring-gray-800 scale-110" : ""
              }`}
              style={{ backgroundColor: item.c }}
            >
              {item.r}
              <p className="text-xs font-normal opacity-80">{item.min}+</p>
            </div>
          ))}
        </div>
      </div>

      {/* Detailed Breakdown */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Ratio Scores */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="font-semibold text-gray-800 mb-4">
            Financial Ratio Assessment ({ratioScore}/60)
          </h3>
          <div className="space-y-2">
            {ratios.map((ratio) => {
              const status = getRatioStatus(ratio);
              const statusColors = {
                good: "text-green-600",
                fair: "text-yellow-600",
                poor: "text-red-600",
              };
              return (
                <div
                  key={ratio.name}
                  className="flex justify-between items-center py-1.5 border-b border-gray-100"
                >
                  <span className="text-sm text-gray-700">{ratio.name}</span>
                  <div className="flex items-center gap-3">
                    <span className="text-sm font-mono font-medium">
                      {formatRatioValue(ratio.value, ratio.format)}
                    </span>
                    <span
                      className={`text-xs font-medium uppercase ${statusColors[status]}`}
                    >
                      {status}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Questionnaire Scores */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="font-semibold text-gray-800 mb-4">
            Assessment Questionnaire ({questionnaireScore}/40)
          </h3>
          {answeredCount === 0 ? (
            <p className="text-sm text-gray-500 italic">
              No questions answered yet. Go to the &ldquo;Assessment Questions&rdquo; tab to
              complete the questionnaire.
            </p>
          ) : (
            <div className="space-y-2">
              {creditQuestions.map((q) => {
                const answer = answers[q.id];
                const selectedOption = answer
                  ? q.options.find((o) => o.score === answer)
                  : null;
                return (
                  <div
                    key={q.id}
                    className="py-1.5 border-b border-gray-100"
                  >
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-700 truncate pr-2">
                        {q.id}. {q.question}
                      </span>
                      <span className="text-sm font-medium text-indigo-600 shrink-0">
                        {answer ? `${answer}/4` : "-"}
                      </span>
                    </div>
                    {selectedOption && (
                      <p className="text-xs text-gray-500 mt-0.5 pl-4">
                        {selectedOption.label}
                      </p>
                    )}
                  </div>
                );
              })}
            </div>
          )}
          <div className="mt-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-xs text-gray-600">
              Questions answered: {answeredCount}/{totalQuestions} |
              Score: {questionnaireScore}/40 points
            </p>
          </div>
        </div>
      </div>

      {/* Recommendation */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 className="font-semibold text-gray-800 mb-3">Lending Recommendation</h3>
        <div
          className="p-4 rounded-lg border-l-4"
          style={{ borderLeftColor: color, backgroundColor: `${color}10` }}
        >
          <p className="font-medium" style={{ color }}>
            Rating: {rating} — {ratingDescription}
          </p>
          <p className="text-sm text-gray-600 mt-2">
            {totalScore >= 65
              ? "Based on the financial analysis and credit assessment, this application demonstrates adequate creditworthiness. The loan may be approved subject to standard terms and conditions."
              : totalScore >= 45
              ? "This application shows moderate credit risk. Additional collateral, guarantees, or modified terms may be required before approval."
              : "This application presents significant credit risk. Careful review and enhanced due diligence is recommended before proceeding."}
          </p>
        </div>
      </div>
    </div>
  );
}

export default CreditScoreDisplay;
