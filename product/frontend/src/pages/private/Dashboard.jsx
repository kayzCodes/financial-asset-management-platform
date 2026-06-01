import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import Sidebar from "../../components/dashboard/SidebarDashboard";

import { useAuth } from "../../context/AuthContext";
import axios from "axios";

import { FaWallet, FaChartPie, FaPiggyBank } from "react-icons/fa";
import { MdSavings, MdAttachMoney } from "react-icons/md";
import { BsGraphUpArrow } from "react-icons/bs";
import SearchBar from "../../components/dashboard/SearchBar";

const Dashboard = () => {

    const navigate = useNavigate();

    const [portfolioData, setPortfolioData] = useState(null);

    // Firebase user info
    const { currentUser, userLoggedIn } = useAuth();

    // Backend user info
    const [backendUser, setBackendUser] = useState(null);

    // UI States
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const [flipped, setFlipped] = useState(Array(6).fill(false));

    const [goals, setGoals] = useState([]);

    const [savings, setSavings] = useState([]);

    const tiles = [
        {
            title: "Portfolio Overview",
            icon: <FaWallet size={38} />,
            renderBack: (data) => {
                if (!data) return <p>Loading...</p>;

                const value = data.totalValueGbp;
                const pnl = data.unrealisedPnlGbp;
                const percent = data.unrealisedPnlPercent;
                const safePercent = percent != null ? percent.toFixed(2) : "0.00";

                return (
                    <div className="flex flex-col justify-between items-center h-full px-4 py-4">

                        {/* TOP SECTION */}
                        <div className="flex flex-col items-center">

                            <p className="text-2xl font-bold text-gray-900 tracking-tight">
                                £{value.toLocaleString(undefined, {
                                    minimumFractionDigits: 2,
                                    maximumFractionDigits: 2
                                })}
                            </p>

                            <div
                                className={`
                                    mt-2 px-3 py-1 rounded-full text-sm font-semibold
                                    ${pnl >= 0
                                        ? "bg-green-100 text-green-600"
                                        : "bg-red-100 text-red-600"}
                                `}
                            >
                                {pnl >= 0 ? "+" : "-"}
                                £{Math.abs(pnl).toLocaleString(undefined, {
                                    minimumFractionDigits: 2,
                                    maximumFractionDigits: 2
                                })}
                                {" "}({safePercent}%)
                            </div>

                            <p className="text-xs text-gray-400 mt-2 tracking-wide">
                                Total Portfolio Value
                            </p>
                        </div>

                        {/* BUTTON */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation(); // prevents flip
                                navigate("/portfolio-overview");
                            }}
                            className="
                            mt-4 px-4 py-2 rounded-lg
                            bg-red-50 text-red-500 hover:bg-red-100                            
                            hover:bg-red-200
                            transition-all duration-200
                            "
                        >
                            View Portfolio →
                        </button>

                    </div>
                );
            }
        },
        {
            title: "Asset Allocation",
            icon: <FaChartPie size={38} />,
            renderBack: (data) => {
                if (!data) return <p>Loading...</p>;

                const stocks = data.stocksPercent ?? 0;
                const crypto = data.cryptoPercent ?? 0;

                return (
                    <div className="flex flex-col justify-between items-center h-full px-4 py-4">

                        {/* CONTENT */}
                        <div className="flex flex-col items-center space-y-3">

                            <div className="w-full flex justify-between text-sm text-gray-700">
                                <span>Stocks</span>
                                <span className="font-semibold">
                                    {stocks.toFixed(1)}%
                                </span>
                            </div>

                            <div className="w-full flex justify-between text-sm text-gray-700">
                                <span>Crypto</span>
                                <span className="font-semibold">
                                    {crypto.toFixed(1)}%
                                </span>
                            </div>

                            {/* SIMPLE BAR VISUAL */}
                            <div className="w-full mt-2">
                                <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-red-400"
                                        style={{ width: `${stocks}%` }}
                                    />
                                </div>
                            </div>

                        </div>

                        {/* BUTTON */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                navigate("/portfolio-overview");
                            }}
                            className="
                                mt-4 px-4 py-2 rounded-lg
                                bg-red-50 text-red-500 text-sm font-medium
                                hover:bg-red-100
                                transition-all duration-200
                            "
                        >
                            View Allocation →
                        </button>

                    </div>
                );
            }
        },
        {
            title: "Investment Growth",
            icon: <BsGraphUpArrow size={38} />,
            renderBack: (data) => {
                if (!data || !data.chart || data.chart.length < 2) {
                    return <p>Not enough data</p>;
                }

                const chart = data.chart;

                const start = chart[0].close;
                const end = chart[chart.length - 1].close;

                const change = end - start;
                const percent = start !== 0 ? (change / start) * 100 : 0;

                return (
                    <div className="flex flex-col justify-between items-center h-full px-4 py-4">

                        {/* CONTENT */}
                        <div className="flex flex-col items-center">

                            {/* VALUE CHANGE */}
                            <p
                                className={`
                                    text-2xl font-bold tracking-tight
                                    ${change >= 0 ? "text-green-600" : "text-red-600"}
                                `}
                            >
                                {change >= 0 ? "+" : "-"}
                                £{Math.abs(change).toLocaleString(undefined, {
                                    minimumFractionDigits: 2,
                                    maximumFractionDigits: 2
                                })}
                            </p>

                            {/* PERCENT BADGE */}
                            <div
                                className={`
                                    mt-2 px-3 py-1 rounded-full text-sm font-semibold
                                    ${change >= 0
                                        ? "bg-green-100 text-green-600"
                                        : "bg-red-100 text-red-600"}
                                `}
                            >
                                {percent.toFixed(2)}%
                            </div>

                            <p className="text-xs text-gray-400 mt-2 tracking-wide">
                                Portfolio Growth
                            </p>

                        </div>

                        {/* BUTTON */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                navigate("/portfolio-overview");
                            }}
                            className="
                                mt-4 px-4 py-2 rounded-lg
                                bg-red-50 text-red-500 text-sm font-medium
                                hover:bg-red-100
                                transition-all duration-200
                            "
                        >
                            View Growth →
                        </button>

                    </div>
                );
            }
        },
        {
            title: "Savings Planner",
            icon: <FaPiggyBank size={38} />,
            renderBack: () => {
                if (!lastSaving) {
                    return (
                        <div className="flex flex-col justify-center items-center h-full px-4 py-4">
                            <p className="text-sm text-gray-500">
                                No savings yet
                            </p>

                            <button
                                onClick={(e) => {
                                    e.stopPropagation();
                                    navigate("/goals");
                                }}
                                className="
                                    mt-4 px-4 py-2 rounded-lg
                                    bg-red-50 text-red-500 text-sm font-medium
                                    hover:bg-red-100
                                    transition-all duration-200
                                "
                            >
                                Start Saving →
                            </button>
                        </div>
                    );
                }

                return (
                    <div className="flex flex-col justify-between items-center h-full px-4 py-4">

                        {/* CONTENT */}
                        <div className="flex flex-col items-center">

                            <p className="text-xs text-gray-400 tracking-wide">
                                Last Saved
                            </p>

                            <p className="text-2xl font-bold text-red-600 mt-1">
                                +£{Number(lastSaving.amount).toLocaleString()}
                            </p>

                            <p className="text-xs text-gray-400 mt-2">
                                Keep building your goals
                            </p>

                        </div>

                        {/* BUTTON */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                navigate("/goals");
                            }}
                            className="
                                mt-4 px-4 py-2 rounded-lg
                                bg-red-50 text-red-500 text-sm font-medium
                                hover:bg-red-100
                                transition-all duration-200
                            "
                        >
                            View Goals →
                        </button>

                    </div>
                );
            }
        },
        {
            title: "Financial Goals",
            icon: <MdSavings size={38} />,
            renderBack: () => {
                if (!goals || goals.length === 0) {
                    return (
                        <div className="flex flex-col justify-center items-center h-full px-4 py-4">
                            <p className="text-sm text-gray-500">
                                No goals yet
                            </p>

                            <button
                                onClick={(e) => {
                                    e.stopPropagation();
                                    navigate("/goals");
                                }}
                                className="
                                    mt-4 px-4 py-2 rounded-lg
                                    bg-red-50 text-red-500 text-sm font-medium
                                    hover:bg-red-100
                                    transition-all duration-200
                                "
                            >
                                Create Goal →
                            </button>
                        </div>
                    );
                }

                const sorted = [...goals].sort(
                    (a, b) => new Date(a.deadline) - new Date(b.deadline)
                );

                const goal = sorted[0];

                const current = Number(goal.currentAmount ?? 0);
                const target = Number(goal.targetAmount ?? 0);

                const percent = target > 0 ? (current / target) * 100 : 0;

                return (
                    <div className="flex flex-col justify-between items-center h-full px-4 py-4">

                        {/* CONTENT */}
                        <div className="flex flex-col items-center w-full">

                            <p className="text-xs text-gray-400 tracking-wide">
                                Next Goal
                            </p>

                            <p className="font-semibold text-gray-900 mt-1 text-center">
                                {goal.goalTitle}
                            </p>

                            <p className="text-sm text-gray-600 mt-1">
                                £{current.toLocaleString()} / £{target.toLocaleString()}
                            </p>

                            {/* PROGRESS BAR */}
                            <div className="w-full mt-3">
                                <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-red-400"
                                        style={{ width: `${Math.min(percent, 100)}%` }}
                                    />
                                </div>
                            </div>

                            <p className="text-xs text-gray-500 mt-2">
                                {percent.toFixed(1)}% complete
                            </p>

                        </div>

                        {/* BUTTON */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                navigate("/goals");
                            }}
                            className="
                                mt-4 px-4 py-2 rounded-lg
                                bg-red-50 text-red-500 text-sm font-medium
                                hover:bg-red-100
                                transition-all duration-200
                            "
                        >
                            View Goals →
                        </button>

                    </div>
                );
            }
        },
        {
            title: "Market Insights",
            icon: <MdAttachMoney size={38} />,
            renderBack: (data) => {
                if (!data) {
                    return <p>Loading...</p>;
                }

                const top = data.topPerformer;
                const worst = data.worstPerformer;

                if (!top || !worst) {
                    return <p>No data</p>;
                }

                const topPercent = Number(top.percentChange ?? 0);
                const worstPercent = Number(worst.percentChange ?? 0);

                return (
                    <div className="flex flex-col justify-between items-center h-full px-4 py-4">

                        {/* CONTENT */}
                        <div className="flex flex-col items-center w-full space-y-3">

                            {/* TOP */}
                            <div className="w-full flex justify-between items-center">
                                <div>
                                    <p className="text-xs text-gray-400">Top</p>
                                    <p className="font-semibold text-gray-900">
                                        {top.displayName}
                                    </p>
                                </div>

                                <span className="
                                    px-2 py-1 rounded-full text-xs font-semibold
                                    bg-green-100 text-green-600
                                ">
                                    +{topPercent.toFixed(2)}%
                                </span>
                            </div>

                            {/* WORST */}
                            <div className="w-full flex justify-between items-center">
                                <div>
                                    <p className="text-xs text-gray-400">Worst</p>
                                    <p className="font-semibold text-gray-900">
                                        {worst.displayName}
                                    </p>
                                </div>

                                <span className="
                                    px-2 py-1 rounded-full text-xs font-semibold
                                    bg-red-100 text-red-600
                                ">
                                    {worstPercent.toFixed(2)}%
                                </span>
                            </div>

                        </div>

                        {/* BUTTON */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                navigate("/assets/stocks");
                            }}
                            className="
                                mt-4 px-4 py-2 rounded-lg
                                bg-red-50 text-red-500 text-sm font-medium
                                hover:bg-red-100
                                transition-all duration-200
                            "
                        >
                            View Markets →
                        </button>

                    </div>
                );
            }
        },
    ];

    const toggleFlip = (index) => {
        setFlipped((prev) => {
            const updated = Array(6).fill(false);
            updated[index] = !prev[index];
            return updated;
        });
    };


    // Redirect if not logged in
    useEffect(() => {
        if (!userLoggedIn) navigate("/login");
    }, [userLoggedIn, navigate]);

    // Fetch backend user using secure Firebase token
    useEffect(() => {
        if (!currentUser) return;

        const loadUser = async () => {
            try {
                // Always use a fresh token
                const token = await currentUser.getIdToken(true);

                // 1️⃣ MARK LOGIN SUCCESS (updates lastLoginAt ONCE)
                await axios.post(
                    "http://localhost:8080/api/user/loginSuccess",
                    {},
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                // 2️⃣ FETCH USER DATA (READ-ONLY)
                const res = await axios.get(
                    "http://localhost:8080/api/user/getUser",
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                setBackendUser(res.data);

            } catch (err) {
                console.error("Failed to load backend user:", err);
            }
        };

        loadUser();
    }, [currentUser]);

    useEffect(() => {
        console.log("portfolio effect ran");
        console.log("currentUser inside effect:", currentUser);

        if (!currentUser) {
            console.log("No currentUser, skipping portfolio fetch");
            return;
        }

        const fetchPortfolio = async () => {
            try {
                console.log("Starting portfolio fetch");

                const token = await currentUser.getIdToken(true);
                console.log("Got token");

                const res = await axios.get(
                    "http://localhost:8080/api/portfolio/overview",
                    {
                        headers: { Authorization: `Bearer ${token}` }
                    }
                );

                console.log("Portfolio response:", res.data);
                setPortfolioData(res.data);
            } catch (err) {
                console.error("Failed to fetch portfolio:", err);
            }
        };

        fetchPortfolio();
    }, [currentUser]);

    useEffect(() => {
        if (!currentUser) return;

        const fetchGoals = async () => {
            try {
                const token = await currentUser.getIdToken(true);

                const res = await axios.get(
                    "http://localhost:8080/api/userGoals/getGoals",
                    {
                        headers: { Authorization: `Bearer ${token}` }
                    }
                );

                setGoals(res.data);

            } catch (err) {
                console.error("Failed to fetch goals:", err);
            }
        };

        fetchGoals();
    }, [currentUser]);

    useEffect(() => {
        if (!currentUser) return;

        const fetchSavings = async () => {
            try {
                const token = await currentUser.getIdToken(true);

                const res = await axios.get(
                    "http://localhost:8080/api/savings/getAllSavings",
                    {
                        headers: { Authorization: `Bearer ${token}` }
                    }
                );

                setSavings(res.data);

            } catch (err) {
                console.error("Failed to fetch savings:", err);
            }
        };

        fetchSavings();
    }, [currentUser]);

    const lastSaving = savings.length > 0
        ? savings[savings.length - 1]
        : null;

    // Get first name safely
    const firstName = backendUser?.firstName || "";


    return (
        <>

            <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

            <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

                <NavbarDashboard
                    onToggleSidebar={() => setSidebarOpen(true)}
                    portfolioData={portfolioData}
                    lastSaving={lastSaving}
                    goals={goals}
                    assets={portfolioData?.assets || []}
                    showSearch={true}
                />

                {/* WELCOME MESSAGE */}
                <div className="text-center mt-6 animate-fade-in">
                    <h1 className="text-3xl font-bold text-black drop-shadow-md">
                        Welcome{firstName ? `, ${firstName}` : ""}!
                    </h1>
                </div>

                {/* DASHBOARD TILES */}
                <div className="flex flex-col items-center justify-center flex-grow -mt-10">
                    <div className="grid grid-cols-3 gap-4">
                        {tiles.map((tile, index) => (
                            <div
                                key={index}
                                className="relative w-60 h-60 cursor-pointer perspective"
                                onClick={() => toggleFlip(index)}
                            >
                                <div
                                    className={`relative w-full h-full transition-transform duration-700 transform-style-preserve-3d ${flipped[index] ? "rotate-y-180" : ""
                                        }`}
                                >
                                    {/* FRONT */}
                                    <div className="
                                        absolute inset-0 
                                        bg-rose-100/70 backdrop-blur-md 
                                        shadow-lg shadow-rose-200/40
                                        rounded-2xl border border-rose-200/50
                                        flex flex-col items-center justify-center
                                        hover:scale-105 hover:bg-rose-100/90
                                        hover:shadow-xl hover:shadow-rose-300/50
                                        backface-hidden
                                    ">
                                        <div className="text-rose-400 mb-3">{tile.icon}</div>
                                        <h2 className="text-lg font-semibold text-gray-800">
                                            {tile.title}
                                        </h2>
                                    </div>

                                    {/* BACK */}
                                    <div className="
                                        absolute inset-0 
                                        bg-rose-50 
                                        shadow-lg shadow-rose-200/40
                                        rounded-2xl border border-rose-200/60
                                        flex items-center justify-center p-4
                                        rotate-y-180 backface-hidden
                                    ">
                                        <div className="text-gray-800 text-sm">
                                            {tile.renderBack
                                                ? tile.renderBack(portfolioData)
                                                : (
                                                    <p>
                                                        Details about <span className="font-semibold">
                                                            {tile.title}
                                                        </span>.
                                                    </p>
                                                )
                                            }
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* CSS */}
            <style>{`
                .perspective { perspective: 1000px; }
                .rotate-y-180 { transform: rotateY(180deg); }
                .backface-hidden { backface-visibility: hidden; }
                .transform-style-preserve-3d { transform-style: preserve-3d; }
            `}</style>
        </>
    );
};

export default Dashboard;
