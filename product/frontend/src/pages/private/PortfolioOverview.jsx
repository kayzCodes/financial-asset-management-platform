import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext"

import LineChart from "../../components/common/LineChart";
import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";
import PortfolioPerformance from "../../components/dashboard/portfolio/PortfolioPerformance";
import PortfolioAssetsList from "../../components/dashboard/portfolio/PortfolioAssetsList";
import PortfolioAllocationChart from "../../components/dashboard/portfolio/PortfolioAllocationChart";
import PortfolioKPIs from "../../components/dashboard/portfolio/PortfolioKPIs";

import { sliceByRange } from "../../utils/chartRange";

const PortfolioOverview = () => {

    const { userLoggedIn, currentUser } = useAuth();
    const navigate = useNavigate();

    const [sidebarOpen, setSidebarOpen] = useState(false);

    const [timeframe, setTimeframe] = useState("1Y");

    const [portfolioData, setPortfolioData] = useState(null);
    const [loading, setLoading] = useState(true);

    // -----------------------------
    // FETCH PORTFOLIO
    // -----------------------------
    const fetchPortfolio = async () => {
        try {
            setLoading(true);

            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                "http://localhost:8080/api/portfolio/overview",
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setPortfolioData(res.data);
        } catch (err) {
            console.error("Failed to fetch portfolio overview:", err);
        } finally {
            setLoading(false);
        }
    };

    console.log("this is the portfolio overview ", portfolioData);

    // -----------------------------
    // LOAD ON PAGE START
    // -----------------------------
    useEffect(() => {
        if (currentUser) {
            fetchPortfolio();
        }
    }, [currentUser]);



    // Redirect if not logged in
    useEffect(() => {
        if (!userLoggedIn) {
            navigate("/login");
        }
    }, [userLoggedIn, navigate]);

    const slicedChartData = useMemo(() => {
        if (!portfolioData?.chart) return [];
        return sliceByRange(portfolioData.chart, timeframe);
    }, [portfolioData?.chart, timeframe]);

    const chartPerformance = useMemo(() => {
        if (!slicedChartData || slicedChartData.length < 2) {
            return null;
        }

        const start = slicedChartData[0].close;
        const end = slicedChartData[slicedChartData.length - 1].close;

        const change = end - start;
        const percent = (change / start) * 100;

        return {
            currentValue: end,
            change,
            percent,
        };
    }, [slicedChartData]);

    return (
        <>
            {/* Sidebar */}
            <SidebarDashboard
                isOpen={sidebarOpen}
                onClose={() => setSidebarOpen(false)}
            />

            <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col relative">

                {/* Navbar */}
                <NavbarDashboard onToggleSidebar={() => setSidebarOpen(true)} />

                {/* CONTENT */}
                <div className="flex flex-col flex-1 px-8 mt-10 pb-10">

                    {/* PAGE HEADER */}
                    <div className="max-w-7xl w-full mx-auto mb-8 flex justify-between items-center">

                        <div>
                            <h1 className="text-3xl font-bold text-gray-800 mb-2">
                                Portfolio Overview
                            </h1>

                            <p className="text-gray-700">
                                Track the performance of your entire portfolio over time.
                            </p>
                        </div>

                        <button
                            onClick={fetchPortfolio}
                            disabled={loading}
                            className="
                                    px-4 py-2 rounded-lg
                                    bg-red-500 hover:bg-red-600
                                    text-white font-medium
                                    disabled:opacity-50
                                "
                        >
                            {loading ? "Refreshing..." : "Refresh"}
                        </button>

                    </div>


                    {/* DASHBOARD GRID */}
                    <div className="grid grid-cols-12 gap-6 max-w-7xl w-full mx-auto">

                        {/* KPI SUMMARY */}
                        <div className="col-span-12">
                            {portfolioData && <PortfolioKPIs data={portfolioData} />}
                        </div>

                        {/* ALLOCATION */}
                        <div className="col-span-12 md:col-span-6">
                            {portfolioData && (
                                <PortfolioAllocationChart
                                    stocksPercent={portfolioData.stocksPercent}
                                    cryptoPercent={portfolioData.cryptoPercent}
                                />
                            )}
                        </div>

                        {/* PERFORMANCE */}
                        <div className="col-span-12 md:col-span-6">
                            {portfolioData && (
                                <PortfolioPerformance
                                    topPerformer={portfolioData.topPerformer}
                                    worstPerformer={portfolioData.worstPerformer}
                                />
                            )}
                        </div>

                        {/* ASSETS */}
                        <div className="col-span-12 md:col-span-6">
                            {portfolioData && (
                                <PortfolioAssetsList assets={portfolioData.assets} />
                            )}
                        </div>

                        {/* PORTFOLIO CHART */}
                        <div className="col-span-12 md:col-span-6">

                            {!loading && (
                                <div className="bg-white/70 rounded-xl border shadow p-5">

                                    {/* CHART HEADER */}
                                    <div className="flex justify-between items-center mb-4">

                                        <div>
                                            <p className="text-sm text-gray-600">
                                                Portfolio Value
                                            </p>

                                            <p className="text-3xl font-bold text-gray-900">
                                                £{chartPerformance?.currentValue?.toLocaleString()}
                                            </p>

                                            {chartPerformance && (
                                                <p
                                                    className={`text-sm font-semibold ${chartPerformance.change >= 0
                                                        ? "text-green-600"
                                                        : "text-red-600"
                                                        }`}
                                                >
                                                    {chartPerformance.change >= 0 ? "+" : ""}
                                                    £{chartPerformance.change.toFixed(2)} (
                                                    {chartPerformance.percent.toFixed(2)}%)
                                                </p>
                                            )}
                                        </div>

                                        {/* TIMEFRAME BUTTONS */}
                                        <div className="flex space-x-2">
                                            {["1D", "1W", "1M", "1Y"].map((tf) => (
                                                <button
                                                    key={tf}
                                                    onClick={() => setTimeframe(tf)}
                                                    className={`px-3 py-1 text-sm rounded-md ${timeframe === tf
                                                        ? "bg-gray-900 text-white"
                                                        : "bg-gray-200 text-gray-700 hover:bg-gray-300"
                                                        }`}
                                                >
                                                    {tf}
                                                </button>
                                            ))}
                                        </div>

                                    </div>

                                    <LineChart
                                        data={slicedChartData}
                                        color={chartPerformance?.change >= 0 ? "#16a34a" : "#dc2626"}
                                    />

                                </div>
                            )}

                        </div>

                    </div>
                </div>

            </div>
        </>
    );
};

export default PortfolioOverview;
