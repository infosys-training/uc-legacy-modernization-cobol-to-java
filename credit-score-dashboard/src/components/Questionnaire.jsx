import { creditQuestions } from "../utils/creditScoring";

function Questionnaire({ answers, setAnswers }) {
  const categories = [...new Set(creditQuestions.map((q) => q.category))];
  const answeredCount = Object.keys(answers).length;
  const totalQuestions = creditQuestions.length;

  const handleAnswer = (questionId, score) => {
    setAnswers((prev) => ({ ...prev, [questionId]: score }));
  };

  return (
    <div className="space-y-6">
      {/* Progress */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <div className="flex justify-between items-center mb-2">
          <h3 className="text-lg font-semibold text-gray-800">
            Credit Assessment Questionnaire
          </h3>
          <span className="text-sm text-gray-500">
            {answeredCount}/{totalQuestions} answered
          </span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className="bg-indigo-600 h-2 rounded-full transition-all"
            style={{ width: `${(answeredCount / totalQuestions) * 100}%` }}
          />
        </div>
        <p className="text-sm text-gray-500 mt-2">
          Answer all questions to complete the credit assessment. Each response contributes
          to the overall credit score (max 40 points from questionnaire).
        </p>
      </div>

      {/* Questions by Category */}
      {categories.map((category) => (
        <div
          key={category}
          className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden"
        >
          <div className="px-6 py-4 bg-gray-50 border-b border-gray-200">
            <h4 className="font-semibold text-gray-800">{category}</h4>
          </div>
          <div className="p-6 space-y-6">
            {creditQuestions
              .filter((q) => q.category === category)
              .map((question) => (
                <QuestionItem
                  key={question.id}
                  question={question}
                  selectedScore={answers[question.id]}
                  onAnswer={handleAnswer}
                />
              ))}
          </div>
        </div>
      ))}
    </div>
  );
}

function QuestionItem({ question, selectedScore, onAnswer }) {
  return (
    <div className="space-y-3">
      <p className="font-medium text-gray-800">
        {question.id}. {question.question}
      </p>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
        {question.options.map((option) => (
          <button
            key={option.score}
            onClick={() => onAnswer(question.id, option.score)}
            className={`text-left p-3 rounded-lg border-2 transition-all text-sm ${
              selectedScore === option.score
                ? "border-indigo-500 bg-indigo-50 text-indigo-700"
                : "border-gray-200 hover:border-gray-300 text-gray-600 hover:bg-gray-50"
            }`}
          >
            <span className="font-medium">{option.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

export default Questionnaire;
