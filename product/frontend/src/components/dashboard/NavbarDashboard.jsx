import { useState, useRef, useEffect } from "react";
import { FaBell } from "react-icons/fa";
import { FiUser } from "react-icons/fi";
import SearchBar from "./SearchBar";
import { useTranslation } from "react-i18next";

const NavbarDashboard = ({
    onToggleSidebar,
    portfolioData,
    lastSaving,
    goals = [],
    assets = [],
    showSearch = false

}) => {

    const { t } = useTranslation();

    const notificationRef = useRef(null);
    const profileRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (
                notificationRef.current &&
                !notificationRef.current.contains(event.target)
            ) {
                setNotificationsOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (
                profileRef.current &&
                !profileRef.current.contains(event.target)
            ) {
                setMenuOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const getPercent = (obj) =>
        Number(
            obj?.percentChange ??
            obj?.unrealisedPnlPercent ??
            obj?.unrealisedPnLPercent ??
            0
        ).toFixed(1);

    const getName = (obj) =>
        obj?.symbol ?? obj?.displayName ?? obj?.name ?? "Unknown";

    const notifications = [
        portfolioData?.topPerformer &&
        `Top: ${getName(portfolioData.topPerformer)} up ${getPercent(
            portfolioData.topPerformer
        )}%`,

        portfolioData?.worstPerformer &&
        `Worst: ${getName(portfolioData.worstPerformer)} down ${getPercent(
            portfolioData.worstPerformer
        )}%`,

        lastSaving?.amount > 0 &&
        `+£${Number(lastSaving.amount).toLocaleString()} added to savings`
    ].filter(Boolean);

    const [menuOpen, setMenuOpen] = useState(false);
    const [notificationsOpen, setNotificationsOpen] = useState(false);

    return (
        <nav className="bg-[#FFDADA] shadow-md py-2 px-6 flex justify-between items-center relative h-14">

            {/* LEFT */}
            <div className="mr-4 cursor-pointer" onClick={onToggleSidebar}>
                <span className="text-gray-700 font-semibold hover:text-red-500 transition text-sm">
                    {t("My Dashboard")}
                </span>
            </div>

            {/* MIDDLE */}
            {showSearch && (
                <div className="flex flex-1 justify-center items-center">
                    <div className="flex items-center w-full max-w-2xl">
                        <SearchBar
                            noMargin
                            goals={goals}
                            assets={assets}
                        />
                    </div>
                </div>
            )}

            {/* RIGHT */}
            <div className="flex items-center space-x-4">

                <div className="text-gray-800 font-bold text-md tracking-wide">
                    Keystone Portfolio
                </div>

                {/* Notifications */}
                <div ref={notificationRef} className="relative">
                    <div
                        className="
                            relative p-2 rounded-full
                            cursor-pointer text-gray-700
                            transition-all duration-200
                            hover:text-red-500 hover:bg-white/40 hover:scale-110
                            active:scale-95
                        "
                        onClick={() => setNotificationsOpen(!notificationsOpen)}
                    >
                        <FaBell size={18} />
                    </div>

                    {notificationsOpen && (
                        <div className="
                            absolute right-0 top-10 w-72
                            bg-white/90 backdrop-blur-md
                            shadow-xl shadow-black/5
                            rounded-2xl border border-white/40
                            py-2 z-50 text-sm overflow-hidden
                        ">

                            <p className="px-4 py-2 text-xs text-gray-400 uppercase tracking-wide">
                                {t("notifications")}
                            </p>

                            {notifications.length === 0 ? (
                                <div className="px-4 py-3 text-gray-400">
                                    {t("noUpdates")}
                                </div>
                            ) : (
                                notifications.map((n, i) => (
                                    <div
                                        key={i}
                                        className="
                                            px-4 py-3 text-gray-700
                                            hover:bg-red-50
                                            transition-colors duration-150
                                            cursor-pointer
                                        "
                                    >
                                        {n}
                                    </div>
                                ))
                            )}

                        </div>
                    )}
                </div>

                {/* Profile */}
                <div ref={profileRef} className="relative">
                    <button
                        onClick={() => setMenuOpen(!menuOpen)}
                        className="
                            flex items-center p-2 rounded-full
                            cursor-pointer text-gray-700
                            transition-all duration-200
                            hover:text-red-500 hover:bg-white/40 hover:scale-110
                            active:scale-95
                        "
                    >
                        <FiUser size={20} />
                    </button>

                    {menuOpen && (
                        <div className="
                            absolute right-0 mt-3 w-44
                            bg-white/90 backdrop-blur-md
                            shadow-xl shadow-black/5
                            rounded-2xl border border-white/40
                            py-2 z-50 text-sm overflow-hidden
                        ">

                            <a
                                href="/account"
                                className="block px-4 py-3 text-gray-700 hover:bg-red-50 transition-colors duration-150"
                            >
                                {t("account")}
                            </a>

                            <a
                                href="/settings"
                                className="block px-4 py-3 text-gray-700 hover:bg-red-50 transition-colors duration-150"
                            >
                                {t("settings")}
                            </a>

                            <a
                                href="/logout"
                                className="block px-4 py-3 text-red-500 hover:bg-red-50 transition-colors duration-150"
                            >
                                {t("logout")}
                            </a>

                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default NavbarDashboard;