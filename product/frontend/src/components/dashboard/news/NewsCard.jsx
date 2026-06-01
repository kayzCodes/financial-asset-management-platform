const NewsCard = ({
    title,
    description,
    source,
    date,
    sentiment,
    relatedSymbols,
    url
}) => {

    const formattedDate = date
        ? new Date(date).toLocaleString("en-GB", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        })
        : "Unknown Date";

    const sentimentColor =
        sentiment === "POSITIVE"
            ? "text-green-600"
            : sentiment === "NEGATIVE"
                ? "text-red-600"
                : "text-gray-500";

    return (
        <a
            href={url}
            target="_blank"
            rel="noopener noreferrer"
            className="
                block w-full h-full
                bg-white/80 backdrop-blur-md
                rounded-xl shadow-md
                border border-white/40
                p-5
                h-64
                flex flex-col
                hover:shadow-lg
                hover:-translate-y-[2px]
                transition-all duration-300
            "
        >
            {/* Title */}
            <h2 className="
                text-lg font-semibold text-gray-900 mb-2
                line-clamp-2
            ">
                {title}
            </h2>

            {/* Description */}
            <p className="
                text-gray-700 text-sm mb-4
                line-clamp-3
                flex-grow
            ">
                {description}
            </p>

            {/* Symbols */}
            {relatedSymbols && relatedSymbols.length > 0 && (
                <div className="flex flex-wrap gap-2 mb-3">
                    {relatedSymbols.slice(0, 3).map(symbol => (
                        <span
                            key={symbol}
                            className="
                                px-2 py-1 text-xs
                                bg-gray-200 rounded-md text-gray-700
                            "
                        >
                            {symbol}
                        </span>
                    ))}
                </div>
            )}

            {/* Footer */}
            <div className="flex justify-between items-center text-xs mt-auto">
                <span className="text-gray-500 truncate">
                    {source}
                </span>

                <span className={sentimentColor}>
                    {sentiment}
                </span>

                <span className="text-gray-500 whitespace-nowrap">
                    {formattedDate}
                </span>
            </div>
        </a>
    );
};

export default NewsCard;