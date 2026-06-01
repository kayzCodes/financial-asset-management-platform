import { CURRENCY_SYMBOL } from "../../../constants/currencies";

const AssetStats = ({ asset, stats }) => {
    if (!asset) {
        return (
            <div className="bg-white/60 p-4 rounded-lg shadow border border-white/40 opacity-60">
                <p>Select an asset to see statistics.</p>
            </div>
        );
    }


    const currencySymbol =
        CURRENCY_SYMBOL[asset.currency] ?? asset.currency ?? "";

    // Holding values
    const quantity = asset.quantity ?? asset.amount ?? "—";

    const avgPrice =
        asset.averagePurchasePrice !== undefined
            ? Number(asset.averagePurchasePrice).toFixed(2)
            : "—";

    const lastUpdated = asset.updatedAt
        ? new Date(asset.updatedAt).toLocaleDateString()
        : "—";

    // LIVE DATA FROM BACKEND (correct keys)
    const marketCap = stats?.MarketCapitalization
        ? Number(stats.MarketCapitalization).toLocaleString()
        : null;

    const peRatio = stats?.PERatio ?? null;
    const eps = stats?.EPS ?? null;
    const sector = stats?.Sector ?? null;


    return (
        <div className="bg-white/60 p-4 rounded-lg shadow border border-white/40">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Key Statistics
            </h2>

            <div className="grid grid-cols-2 gap-4 text-gray-700">

                {/* Quantity */}
                <div>
                    <p className="font-medium">Quantity Owned</p>
                    <p className="text-sm text-gray-600">{quantity}</p>
                </div>

                {/* Average purchase price */}
                <div>
                    <p className="font-medium">Avg. Purchase Price</p>
                    <p className="text-sm text-gray-600">{currencySymbol}{avgPrice}</p>
                </div>

                {/* Last updated */}
                <div>
                    <p className="font-medium">Last Updated</p>
                    <p className="text-sm text-gray-600">{lastUpdated}</p>
                </div>

                {/* Market cap */}
                {marketCap && (
                    <div>
                        <p className="font-medium">Market Cap</p>
                        <p className="text-sm text-gray-600">
                            {currencySymbol}{marketCap}
                        </p>
                    </div>
                )}


                {/* P/E Ratio */}
                {peRatio && (
                    <div>
                        <p className="font-medium">P/E Ratio</p>
                        <p className="text-sm text-gray-600">{peRatio}</p>
                    </div>
                )}


                {/* EPS */}
                {eps && (
                    <div>
                        <p className="font-medium">EPS</p>
                        <p className="text-sm text-gray-600">{eps}</p>
                    </div>
                )}


                {/* Sector */}
                {sector && (
                    <div>
                        <p className="font-medium">Sector</p>
                        <p className="text-sm text-gray-600">{sector}</p>
                    </div>
                )}


            </div>
        </div>
    );
};

export default AssetStats;
