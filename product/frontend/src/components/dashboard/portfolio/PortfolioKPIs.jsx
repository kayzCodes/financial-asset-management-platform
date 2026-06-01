const PortfolioKPIs = ({ data }) => {
    if (!data) return null;

    const {
        totalValueGbp,
        totalCostGbp,
        unrealisedPnlGbp,
        unrealisedPnlPercent,
        lastUpdatedAt,
    } = data;

    const pnlColor =
        unrealisedPnlGbp > 0
            ? "text-green-600"
            : unrealisedPnlGbp < 0
                ? "text-red-600"
                : "text-gray-600";

    return (
        <div className="bg-white/70 rounded-xl p-6 border shadow mb-8">

            <h2 className="text-xl font-semibold text-gray-800 mb-6">
                Portfolio Summary
            </h2>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-6">

                {/* Portfolio Value */}
                <div>
                    <p className="text-sm text-gray-600 mb-1">
                        Portfolio Value
                    </p>
                    <p className="text-2xl font-bold text-gray-900">
                        £{Number(totalValueGbp).toLocaleString()}
                    </p>
                </div>

                {/* Total Cost */}
                <div>
                    <p className="text-sm text-gray-600 mb-1">
                        Total Cost
                    </p>
                    <p className="text-2xl font-semibold text-gray-900">
                        £{Number(totalCostGbp).toLocaleString()}
                    </p>
                </div>

                {/* Unrealised PnL */}
                <div>
                    <p className="text-sm text-gray-600 mb-1">
                        Unrealised PnL
                    </p>
                    <p className={`text-2xl font-bold ${pnlColor}`}>
                        {unrealisedPnlGbp > 0 ? "+" : ""}
                        £{Number(unrealisedPnlGbp).toLocaleString()}
                    </p>
                </div>

                {/* Return % */}
                <div>
                    <p className="text-sm text-gray-600 mb-1">
                        Return
                    </p>
                    <p className={`text-2xl font-bold ${pnlColor}`}>
                        {unrealisedPnlPercent > 0 ? "+" : ""}
                        {Number(unrealisedPnlPercent).toFixed(2)}%
                    </p>
                </div>

            </div>

            {/* Last Updated */}
            <p className="text-xs text-gray-500 mt-6 text-right">
                Last updated:{" "}
                {lastUpdatedAt
                    ? new Date(lastUpdatedAt).toLocaleString()
                    : "—"}
            </p>

        </div>
    );
};

export default PortfolioKPIs;