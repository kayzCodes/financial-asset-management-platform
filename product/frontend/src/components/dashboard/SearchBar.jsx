import { FiSearch } from "react-icons/fi";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

const SearchBar = ({ noMargin = false, goals = [], assets = [] }) => {

    const { t } = useTranslation();
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const navigate = useNavigate();

    console.log("assets in search:", assets);
    console.log("goals: ", goals)

    useEffect(() => {
        if (!query.trim()) {
            setResults([]);
            return;
        }

        const lower = query.toLowerCase();

        const assetResults = assets.map(a => ({
            type: "asset",
            label: (a.symbol || a.displayName || a.name || "").toLowerCase(),
            rawLabel: a.symbol || a.displayName || a.name || "",
            data: a
        }));

        const goalResults = goals.map(g => ({
            type: "goal",
            label: (g.goalTitle || g.title || "").toLowerCase(),
            rawLabel: g.goalTitle || g.title || "",
            data: g
        }));

        const combined = [...assetResults, ...goalResults];

        const filtered = combined.filter(item =>
            item.label.includes(query.toLowerCase())
        );

        setResults(filtered);
    }, [query, assets, goals]);


    return (
        <div className={`w-full flex justify-center ${noMargin ? "" : "mt-4"}`}>
            <div className="relative w-2/3 max-w-2xl">
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder={t("search")} className="
                        w-full px-5 py-3
                        rounded-2xl
                        bg-transparent
                        backdrop-blur-md
                        border border-white/40
                        shadow-md
                        text-gray-700
                        placeholder-gray-400
                        focus:outline-none
                        focus:ring-2 focus:ring-red-300
                        focus:bg-transparent
                        transition
                    "
                />

                {results.length > 0 && (
                    <div className="
                                absolute left-0 right-0 mt-2
                                bg-white/90 backdrop-blur-md
                                border border-white/40
                                rounded-2xl
                                shadow-xl shadow-black/5
                                z-50 overflow-hidden
                            ">
                        {results.map((r, i) => (
                            <div
                                key={i}
                                className="
                                        px-4 py-3
                                        flex items-center justify-between
                                        cursor-pointer
                                        text-sm text-gray-800
                                        transition-all duration-150
                                        hover:bg-red-50
                                        hover:scale-[1.01]
                                    "
                                onClick={() => {
                                    if (r.type === "asset") {
                                        if (r.data.assetType === "STOCK") {
                                            navigate("/assets/stocks");
                                        } else if (r.data.assetType === "CRYPTO") {
                                            navigate("/assets/crypto");
                                        }
                                    }

                                    if (r.type === "goal") {
                                        navigate("/goals");
                                    }

                                    setQuery("");
                                    setResults([]);
                                }}
                            >
                                {/* LEFT: label */}
                                <span className="font-medium">
                                    {r.rawLabel}
                                </span>

                                {/* RIGHT: badge */}
                                <span className="
                                    text-xs px-2 py-0.5 rounded-full
                                    bg-gray-100 text-gray-500
                                ">
                                    {r.type}
                                </span>
                            </div>
                        ))}

                    </div>
                )}


                <FiSearch
                    size={20}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                />
            </div>
        </div>
    );
};

export default SearchBar;
