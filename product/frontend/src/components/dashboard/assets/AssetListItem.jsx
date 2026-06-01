import { CURRENCY_SYMBOL } from "../../../constants/currencies";

const AssetListItem = ({
    asset,
    type,
    onClick,
    onContextMenu,
    isSelected,
    activeLiveMetrics,
}) => {
    // currency symbol MUST be inside the component
    const symbol =
        CURRENCY_SYMBOL[asset.currency] ||
        (asset.currency ? asset.currency : "$");

    // Fix ticker display for crypto
    const ticker =
        asset.tickerSymbol ||
        asset.symbol ||
        asset.cryptoSymbol ||
        asset.ticker ||
        "";

    // Name logic
    const name =
        type === "stocks"
            ? asset.companyName
            : asset.cryptoName || asset.name || ticker;

    const isLive = isSelected && activeLiveMetrics;

    return (
        <div
            onClick={onClick}
            onContextMenu={onContextMenu}
            className="
                flex justify-between items-center 
                p-4 mb-3 rounded-lg 
                bg-white/70 hover:bg-white/90 backdrop-blur-sm 
                cursor-pointer shadow border border-white/50 transition
            "
        >
            {/* LEFT — Name + Quantity */}
            <div className="flex flex-col">
                <p className="font-bold text-gray-900 text-lg">{name}</p>
                <p className="text-sm text-gray-600 uppercase">
                    {asset.quantity} {ticker}
                </p>
            </div>

            {/* RIGHT — Static OR Live */}
            <div className="text-right">
                {!isLive ? (
                    <p className="text-sm text-gray-600">
                        {symbol}
                        {Number(asset.averagePurchasePrice).toFixed(2)}
                    </p>
                ) : (
                    <>
                        <p className="font-semibold text-gray-900">
                            {symbol}
                            {Number(activeLiveMetrics.currentValue).toFixed(2)}
                        </p>

                        <p
                            className={`text-sm ${activeLiveMetrics.priceDifference > 0
                                ? "text-green-600"
                                : activeLiveMetrics.priceDifference < 0
                                    ? "text-red-600"
                                    : "text-gray-500"
                                }`}
                        >
                            {activeLiveMetrics.priceDifference > 0 ? "+" : activeLiveMetrics.priceDifference < 0 ? "-" : ""}
                            {symbol}
                            {Math.abs(Number(activeLiveMetrics.priceDifference)).toFixed(2)} (
                            {activeLiveMetrics.percentageChangeFromAveragePrice > 0 ? "+" : ""}
                            {Number(activeLiveMetrics.percentageChangeFromAveragePrice).toFixed(1)}%)
                        </p>

                    </>
                )}
            </div>
        </div>
    );
};

export default AssetListItem;
