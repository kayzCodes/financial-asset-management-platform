
import { CURRENCY_SYMBOL } from "../../../constants/currencies";

const AssetHeader = ({ asset, price, percentageChange, onRefresh, isRefreshing }) => {
    if (!asset) {
        return (
            <div className="opacity-50 text-gray-600">
                <p>Select an asset to view details</p>
            </div>
        );
    }

    const currencySymbol =
        CURRENCY_SYMBOL[asset.currency] ?? asset.currency ?? "";

    // Ticker
    const ticker =
        asset.tickerSymbol ||
        asset.symbol ||
        asset.cryptoSymbol ||
        asset.ticker ||
        "";

    // Name
    const name =
        asset.companyName ||
        asset.cryptoName ||
        asset.name ||
        "Unnamed Asset";

    return (
        <div className="flex justify-between items-start mb-6">

            {/* LEFT — TICKER + NAME */}
            <div>
                <p className="text-lg font-semibold text-gray-700 uppercase tracking-wide">
                    {ticker}
                </p>

                <h1 className="text-3xl font-bold text-gray-900">
                    {name}
                </h1>
            </div>

            {/* RIGHT — REFRESH + PRICE */}
            <div className="text-right flex flex-col items-end">

                {/* Refresh */}
                {onRefresh && (
                    <button
                        onClick={onRefresh}
                        disabled={isRefreshing}
                        className="
                            mb-2 px-3 py-1.5 rounded-md text-sm font-medium
                            bg-red-400 hover:bg-red-500
                            text-white
                            disabled:opacity-50
                            disabled:cursor-not-allowed
                        "
                    >
                        {isRefreshing ? "Refreshing…" : "Refresh"}
                    </button>
                )}

                {/* Price */}
                <p className="text-4xl font-bold text-gray-900">
                    {currencySymbol}{Number(price).toFixed(2)}
                </p>


                {/* Percentage change */}
                {percentageChange !== null && percentageChange !== undefined && (
                    <p
                        className={`font-semibold text-lg ${percentageChange >= 0
                            ? "text-green-600"
                            : "text-red-600"
                            }`}
                    >
                        {percentageChange > 0 ? "+" : ""}
                        {percentageChange.toFixed(2)}%
                    </p>
                )}

                <p className="text-sm text-gray-600 mt-1">
                    Current price
                </p>
            </div>
        </div>
    );
};

export default AssetHeader;
