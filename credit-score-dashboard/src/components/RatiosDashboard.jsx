import { formatRatioValue, getRatioStatus } from "../utils/ratioCalculations";

function RatiosDashboard({ ratios }) {
  const categories = [...new Set(ratios.map((r) => r.category))];

  return (
    <div className="space-y-6">
      {categories.map((category) => (
        <div
          key={category}
          className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden"
        >
          <div className="px-6 py-4 bg-gray-50 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-800">{category} Ratios</h3>
          </div>
          <div className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {ratios
                .filter((r) => r.category === category)
                .map((ratio) => (
                  <RatioCard key={ratio.name} ratio={ratio} />
                ))}
            </div>
          </div>
        </div>
      ))}

      {/* Ratio Summary Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="px-6 py-4 bg-gray-50 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-800">Complete Ratio Summary</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Ratio
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Category
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Value
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Benchmark
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {ratios.map((ratio) => {
                const status = getRatioStatus(ratio);
                return (
                  <tr key={ratio.name} className="hover:bg-gray-50">
                    <td className="px-6 py-3 font-medium text-gray-900">
                      {ratio.name}
                    </td>
                    <td className="px-6 py-3 text-gray-600">{ratio.category}</td>
                    <td className="px-6 py-3 font-mono font-semibold">
                      {formatRatioValue(ratio.value, ratio.format)}
                    </td>
                    <td className="px-6 py-3 text-gray-500">{ratio.benchmark}</td>
                    <td className="px-6 py-3">
                      <StatusBadge status={status} />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function RatioCard({ ratio }) {
  const status = getRatioStatus(ratio);
  const statusColors = {
    good: "border-green-200 bg-green-50",
    fair: "border-yellow-200 bg-yellow-50",
    poor: "border-red-200 bg-red-50",
  };
  const valueColors = {
    good: "text-green-700",
    fair: "text-yellow-700",
    poor: "text-red-700",
  };

  return (
    <div className={`p-4 rounded-lg border-2 ${statusColors[status]}`}>
      <div className="flex justify-between items-start mb-2">
        <h4 className="font-medium text-gray-800 text-sm">{ratio.name}</h4>
        <StatusBadge status={status} />
      </div>
      <p className={`text-2xl font-bold ${valueColors[status]}`}>
        {formatRatioValue(ratio.value, ratio.format)}
      </p>
      <p className="text-xs text-gray-500 mt-1">{ratio.description}</p>
      <p className="text-xs text-gray-400 mt-1">Benchmark: {ratio.benchmark}</p>
    </div>
  );
}

function StatusBadge({ status }) {
  const styles = {
    good: "bg-green-100 text-green-800",
    fair: "bg-yellow-100 text-yellow-800",
    poor: "bg-red-100 text-red-800",
  };
  const labels = { good: "Good", fair: "Fair", poor: "Poor" };

  return (
    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${styles[status]}`}>
      {labels[status]}
    </span>
  );
}

export default RatiosDashboard;
