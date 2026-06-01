const PortfolioHeader = ({ data }) => {
    if (!data) return null;

    const {
        totalValueGbp,
        totalCostGbp,
        unrealisedPnlGbp,
        unrealisedPnlPercent,
        baseCurrency,
    } = data;

    const currency = baseCurrency === "GBP" ? "£" : "";

    const pnlColor =
        unrealisedPnlGbp > 0
            ? "text-green-600"
            : unrealisedPnlGbp < 0
                ? "text-red-600"
                : "text-gray-600";

    return (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8">

            <div className="bg-white/80 backdrop-blur-md rounded-xl border border-white/40 shadow-md hover:shadow-lg transition-all duration-200 p-6">
                <p className="text-sm text-gray-600">Portfolio Value</p>
                <p className="text-2xl font-bold text-gray-900">
                    {currency}{Number(totalValueGbp).toLocaleString()}
                </p>
            </div>

            <div className="bg-white/80 backdrop-blur-md rounded-xl border border-white/40 shadow-md hover:shadow-lg transition-all duration-200 p-6">
                <p className="text-sm text-gray-600">Total Cost</p>
                <p className="text-2xl font-bold text-gray-900">
                    {currency}{Number(totalCostGbp).toLocaleString()}
                </p>
            </div>

            <div className="bg-white/80 backdrop-blur-md rounded-xl border border-white/40 shadow-md hover:shadow-lg transition-all duration-200 p-6">
                <p className="text-sm text-gray-600">Unrealised PnL</p>
                <p className={`text-2xl font-bold ${pnlColor}`}>
                    {currency}{Number(unrealisedPnlGbp).toLocaleString()}
                </p>
            </div>

            <div className="bg-white/80 backdrop-blur-md rounded-xl border border-white/40 shadow-md hover:shadow-lg transition-all duration-200 p-6">
                <p className="text-sm text-gray-600">Return</p>
                <p className={`text-2xl font-bold ${pnlColor}`}>
                    {unrealisedPnlPercent.toFixed(2)}%
                </p>
            </div>

        </div>
    );
};

export default PortfolioHeader;