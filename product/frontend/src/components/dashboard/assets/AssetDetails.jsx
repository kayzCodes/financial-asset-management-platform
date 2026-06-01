import { useMemo } from "react";
import AssetHeader from "./AssetHeader";
import LineChart from "../../common/LineChart";
import AssetStats from "./AssetStats";
import AssetAbout from "./AssetAbout";
import { sliceByRange } from "../../../utils/chartRange";


const TIMEFRAMES = ["1D", "1W", "1M", "1Y"];

const AssetDetails = ({
    asset,
    liveData,
    timeframe,
    onTimeframeChange,
    onRefresh,
    isRefreshing,
}) => {

    if (!asset) {
        return (
            <div className="bg-white/60 rounded-xl p-6 text-gray-600">
                Select an asset to view details
            </div>
        );
    }

    // ----------------------------
    // PRICE + CHANGE
    // ----------------------------

    console.log("ASSET DETAILS liveData:", liveData);
    console.log("ASSET DETAILS latestClose:", liveData?.latestClose);

    const price =
        liveData?.latestClose ??
        liveData?.currentPrice ??
        asset.averagePurchasePrice;

    const percentageChange =
        liveData?.percentageChange ?? null;


    // ----------------------------
    // STATS + ABOUT
    // ----------------------------
    const stats = liveData?.keyStatistics ?? null;
    const about = liveData?.companyOverview ?? null;

    const slicedChartData = useMemo(() => {
        if (!liveData?.chartData) return [];
        return sliceByRange(liveData.chartData, timeframe);
    }, [liveData?.chartData, timeframe]);

    const chartData = Array.isArray(slicedChartData) ? slicedChartData : [];

    const hasNoChartData = slicedChartData.length === 0;
    const hasSinglePoint = slicedChartData.length === 1;

    return (
        <div
            className="
                bg-white/60 backdrop-blur-md 
                border border-white/40 shadow-md 
                rounded-xl p-6
                flex flex-col space-y-6
            "
        >
            {/* HEADER */}
            <AssetHeader
                asset={asset}
                price={price}
                percentageChange={percentageChange}
                onRefresh={onRefresh}
                isRefreshing={isRefreshing}
            />



            {/* TIMEFRAME SELECTOR */}
            <div className="flex justify-end space-x-2">
                {["1D", "1W", "1M", "1Y"].map(tf => (
                    <button
                        key={tf}
                        onClick={() => onTimeframeChange(tf)}
                        className={`
                        px-3 py-1 rounded-lg text-sm font-medium
                        ${timeframe === tf
                                ? "bg-gray-900 text-white"
                                : "bg-gray-200 text-gray-700 hover:bg-gray-300"}
                    `}
                    >
                        {tf}
                    </button>
                ))}

            </div>

            {timeframe === "1Y" && (
                <p className="text-xs text-gray-500 text-right mt-1">
                    Last available daily data (free tier)
                </p>
            )}


            {/* CHART */}
            {hasNoChartData ? (
                <div className="bg-white/70 rounded-xl border shadow p-6 text-center text-gray-600">
                    No chart data available for this timeframe.
                </div>
            ) : (
                <>
                    <LineChart
                        title="Price Chart"
                        data={chartData}
                    />

                    {hasSinglePoint && (
                        <p className="text-sm text-gray-500 text-center mt-2">
                            Not enough data points to display a trend.
                        </p>
                    )}
                </>
            )}

            {/* STATS */}
            <AssetStats asset={asset} stats={stats} />

            {/* ABOUT */}
            <AssetAbout
                asset={asset}
                text={about}
                stats={stats}
            />
        </div>
    );
};

export default AssetDetails;
