const PortfolioPerformance = ({ topPerformer, worstPerformer }) => {
    if (!topPerformer || !worstPerformer) return null;

    return (
        <div className="bg-white/70 rounded-xl p-6 border shadow mb-8">

            <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Portfolio Performance
            </h2>

            <div className="grid grid-cols-2 gap-6">

                {/* TOP PERFORMER */}
                <div className="bg-white/80 rounded-lg p-4 border">
                    <p className="text-sm text-gray-600 mb-1">
                        Top Performer
                    </p>

                    <p className="text-lg font-bold text-gray-900">
                        {topPerformer.displayName}
                    </p>

                    <p className="text-green-600 font-semibold">
                        +{topPerformer.percentChange.toFixed(2)}%
                    </p>

                    <p className="text-sm text-gray-600">
                        £{Number(topPerformer.valueGbp).toLocaleString()}
                    </p>

                    <p className="text-xs text-gray-500">
                        {topPerformer.allocationPercent.toFixed(2)}% of portfolio
                    </p>
                </div>

                {/* WORST PERFORMER */}
                <div className="bg-white/80 rounded-lg p-4 border">
                    <p className="text-sm text-gray-600 mb-1">
                        Worst Performer
                    </p>

                    <p className="text-lg font-bold text-gray-900">
                        {worstPerformer.displayName}
                    </p>

                    <p className="text-red-600 font-semibold">
                        {worstPerformer.percentChange.toFixed(2)}%
                    </p>

                    <p className="text-sm text-gray-600">
                        £{Number(worstPerformer.valueGbp).toLocaleString()}
                    </p>

                    <p className="text-xs text-gray-500">
                        {worstPerformer.allocationPercent.toFixed(2)}% of portfolio
                    </p>
                </div>

            </div>

        </div>
    );
};

export default PortfolioPerformance;