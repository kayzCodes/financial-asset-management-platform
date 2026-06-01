const PortfolioAssetsList = ({ assets }) => {
    if (!assets || assets.length === 0) return null;

    return (
        <div className="bg-white/80 backdrop-blur-md rounded-xl border border-white/40 shadow-md hover:shadow-lg transition-all duration-200 p-6">

            <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Portfolio Assets
            </h2>

            <div className="space-y-3">

                {assets.map((asset) => {

                    const changeColor =
                        asset.percentChange > 0
                            ? "text-green-600"
                            : asset.percentChange < 0
                                ? "text-red-600"
                                : "text-gray-600";

                    return (
                        <div
                            key={asset.holdingId}
                            className="flex justify-between items-center bg-white/80 border rounded-lg p-3"
                        >

                            {/* LEFT */}
                            <div>
                                <p className="font-semibold text-gray-900">
                                    {asset.displayName}
                                </p>

                                <p className="text-xs text-gray-500">
                                    {asset.assetType}
                                </p>
                            </div>

                            {/* RIGHT */}
                            <div className="text-right">

                                <p className="text-sm text-gray-700">
                                    £{Number(asset.valueGbp).toLocaleString()}
                                </p>

                                <p className="text-xs text-gray-500">
                                    {asset.allocationPercent.toFixed(2)}%
                                </p>

                                <p className={`text-sm font-semibold ${changeColor}`}>
                                    {asset.percentChange > 0 ? "+" : ""}
                                    {asset.percentChange.toFixed(2)}%
                                </p>

                            </div>

                        </div>
                    );
                })}

            </div>

        </div>
    );
};

export default PortfolioAssetsList;