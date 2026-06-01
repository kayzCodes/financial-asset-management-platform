import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";
import NewsCard from "../../components/dashboard/news/NewsCard";

const News = () => {
    const { userLoggedIn, currentUser } = useAuth();
    const navigate = useNavigate();

    const [sidebarOpen, setSidebarOpen] = useState(false);

    const [news, setNews] = useState([]);
    const [loading, setLoading] = useState(false);

    const [refreshing, setRefreshing] = useState(false);

    // Redirect if NOT logged in
    useEffect(() => {
        if (!userLoggedIn) {
            navigate("/login");
        }
    }, [userLoggedIn, navigate]);

    const fetchNews = async () => {
        if (!currentUser) return;

        try {
            setLoading(true);

            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                "http://localhost:8080/api/news/getDigest",
                { headers: { Authorization: `Bearer ${token}` } }
            );

            console.log("NEWS RESPONSE:", res.data);

            setNews(res.data.articles);
        } catch (err) {
            console.error("Failed to fetch news:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (currentUser) {
            fetchNews();
        }
    }, [currentUser]);

    const handleRefreshNews = async () => {
        if (!currentUser) return;

        try {
            setRefreshing(true);

            const token = await currentUser.getIdToken(true);

            const res = await axios.post(
                "http://localhost:8080/api/news/refresh",
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );

            console.log("REFRESH RESPONSE:", res.data);

            setNews(res.data.articles);
        } catch (err) {
            console.error("Failed to refresh news:", err);
        } finally {
            setRefreshing(false);
        }
    };

    const sortedNews = [...news].sort(
        (a, b) => new Date(b.publishedAt) - new Date(a.publishedAt)
    );

    return (
        <div className="relative min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

            {/* SIDEBAR */}
            <SidebarDashboard
                isOpen={sidebarOpen}
                onClose={() => setSidebarOpen(false)}
            />

            {/* NAVBAR — Full width */}
            <NavbarDashboard onToggleSidebar={() => setSidebarOpen(true)} />

            {/* PAGE CONTENT */}
            <div className="flex flex-col flex-1 px-6 py-10">

                {/* HEADER */}
                <div className="max-w-5xl mx-auto mb-12">
                    <div className="
                                    relative
                                    bg-white/60 backdrop-blur-xl
                                    border border-white/30
                                    rounded-3xl
                                    shadow-lg shadow-black/5
                                    p-8 md:p-10
                                    overflow-hidden
                                ">

                        {/* SUBTLE GRADIENT GLOW (premium feel) */}
                        <div className="
                                        absolute inset-0
                                        bg-gradient-to-r from-red-200/20 via-transparent to-red-200/20
                                        pointer-events-none
                                    " />

                        {/* CONTENT */}
                        <div className="relative z-10">

                            {/* TOP ROW */}
                            <div className="flex items-center justify-between mb-6">

                                <div className="flex items-center gap-3">

                                    <h1 className="text-2xl md:text-3xl font-semibold text-gray-900 tracking-tight">
                                        Latest Financial News
                                    </h1>

                                    {/* LIVE INDICATOR */}
                                    <div className="flex items-center gap-2">

                                        {/* Pulsing dot */}
                                        <span className="relative flex h-2.5 w-2.5">
                                            <span className="
                                                                animate-ping absolute inline-flex h-full w-full
                                                                rounded-full bg-red-400 opacity-75
                                                            ">
                                            </span>

                                            <span className="
                                                            relative inline-flex rounded-full h-2.5 w-2.5
                                                            bg-red-500
                                                        ">
                                            </span>
                                        </span>

                                        {/* Text */}
                                        <span className="text-xs font-medium text-red-500 tracking-wide">
                                            LIVE
                                        </span>
                                    </div>

                                </div>

                                <button
                                    onClick={handleRefreshNews}
                                    disabled={refreshing}
                                    className="
                                            px-4 py-2 rounded-lg text-sm font-medium
                                            bg-red-400 text-white
                                            hover:bg-red-500
                                            shadow-sm hover:shadow-md
                                            transition-all duration-200
                                            disabled:opacity-50
                                        "
                                >
                                    {refreshing ? "Refreshing..." : "Refresh"}
                                </button>

                            </div>

                            {/* DESCRIPTION */}
                            <div className="space-y-3">
                                <p className="text-gray-700 text-sm leading-relaxed max-w-2xl">
                                    Stay up to date with the latest financial trends, stock market activity,
                                    and global economic events that matter.
                                </p>

                                <p className="text-gray-600 text-sm max-w-2xl">
                                    Curated articles tailored to your investment interests.
                                </p>
                            </div>

                        </div>
                    </div>
                </div>

                {/* NEWS GRID */}
                <div className="max-w-6xl mx-auto w-full">
                    <div className="
                                    grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3
                                    gap-6
                                    auto-rows-fr
                                ">

                        {loading && (
                            <p className="text-center col-span-full text-gray-600">
                                Loading news...
                            </p>
                        )}

                        {!loading && sortedNews.map(article => (
                            <NewsCard
                                key={article.url}
                                title={article.title}
                                description={article.summary}
                                source={article.source}
                                date={article.publishedAt}
                                sentiment={article.sentiment}
                                relatedSymbols={article.relatedSymbols}
                                url={article.url}
                            />
                        ))}

                    </div>
                </div>

            </div>
        </div>
    );
};

export default News;
