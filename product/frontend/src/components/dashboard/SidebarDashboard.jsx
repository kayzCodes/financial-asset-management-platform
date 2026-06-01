import { useState } from "react";
import {
    FaHome,
    FaChartPie,
    FaBitcoin,
    FaChartLine,
    FaBullseye,
    FaNewspaper,
    FaSignOutAlt,
    FaFolderOpen
} from "react-icons/fa";

const SidebarDashboard = ({ isOpen, onClose }) => {
    const [assetsOpen, setAssetsOpen] = useState(false);

    const hoverClass =
        "hover:bg-[#F6BFBF]/60 rounded-lg px-2 py-1 transition";

    return (
        <>
            {/*Background Overlay */}
            {isOpen && (
                <div
                    onClick={onClose}
                    className="fixed inset-0 bg-black/40 backdrop-blur-sm z-40 transition-opacity"
                />
            )}

            {/* Sidebar */}
            <div
                className={`
                    fixed top-0 left-0 h-full w-64 
                    bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]
                    shadow-xl border-r border-white/30
                    transform ${isOpen ? "translate-x-0" : "-translate-x-full"}
                    transition-transform duration-300 ease-in-out
                    z-50
                `}
            >
                {/* Title */}
                <div className="flex justify-between items-center p-4 border-b border-white/40">
                    <h2 className="text-xl font-semibold text-gray-800">My Dashboard</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-700 hover:text-red-500 text-xl font-bold"
                    >
                        ✕
                    </button>
                </div>

                {/* Links */}
                <div className="flex flex-col mt-4 space-y-4 px-4">

                    {/* Home */}
                    <a
                        href="/dashboard"
                        className={`flex items-center space-x-3 text-gray-800 font-medium ${hoverClass}`}
                    >
                        <FaHome />
                        <span>Home</span>
                    </a>

                    {/* Assets Dropdown */}
                    <div className="w-full">

                        <button
                            onClick={() => setAssetsOpen(!assetsOpen)}
                            className={`w-full flex items-center justify-between text-gray-800 font-medium ${hoverClass}`}
                        >
                            <span className="flex items-center space-x-3">
                                <FaFolderOpen />
                                <span>Assets</span>
                            </span>
                            <span className="text-sm">{assetsOpen ? "▲" : "▼"}</span>
                        </button>

                        <div
                            className={`
                                overflow-hidden transition-all duration-300 
                                ${assetsOpen ? "max-h-40 opacity-100 mt-2" : "max-h-0 opacity-0"}
                            `}
                        >
                            <div className="ml-5 flex flex-col space-y-1">

                                <a
                                    href="/portfolio-overview"
                                    className={`flex items-center space-x-2 text-gray-700 ${hoverClass}`}
                                >
                                    <FaChartPie />
                                    <span>Portfolio Overview</span>
                                </a>

                                <a
                                    href="/assets/stocks"
                                    className={`flex items-center space-x-2 text-gray-700 ${hoverClass}`}
                                >
                                    <FaChartLine />
                                    <span>Stocks</span>
                                </a>

                                <a
                                    href="/assets/crypto"
                                    className={`flex items-center space-x-2 text-gray-700 ${hoverClass}`}
                                >
                                    <FaBitcoin />
                                    <span>Crypto</span>
                                </a>
                            </div>
                        </div>
                    </div>

                    {/* Goals */}
                    <a
                        href="/goals"
                        className={`flex items-center space-x-3 text-gray-800 font-medium ${hoverClass}`}
                    >
                        <FaBullseye />
                        <span>Goals</span>
                    </a>

                    {/* News */}
                    <a
                        href="/news"
                        className={`flex items-center space-x-3 text-gray-800 font-medium ${hoverClass}`}
                    >
                        <FaNewspaper />
                        <span>News</span>
                    </a>
                </div>

                {/* LOG OUT */}
                <div className="absolute bottom-6 w-full px-4">
                    <a
                        href="/logout"
                        className={`flex items-center space-x-3 text-gray-800 font-medium ${hoverClass}`}
                    >
                        <FaSignOutAlt />
                        <span>Log Out</span>
                    </a>
                </div>
            </div>
        </>
    );
};

export default SidebarDashboard;
