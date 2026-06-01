import { useState, useEffect } from "react";

const GoalCard = ({ goal, onContextMenu }) => {

    const title = goal.title || goal.goalTitle || "Untitled Goal";
    const target = goal.targetAmount ?? 0;
    const current = goal.currentAmount ?? 0;
    const deadline = goal.deadline ? goal.deadline.split("T")[0] : "No deadline";

    const progress = Math.min(
        (current / target) * 100 || 0,
        100
    );

    // NEW: animated progress state
    const [animatedProgress, setAnimatedProgress] = useState(0);

    useEffect(() => {
        // small delay makes animation smoother
        const timeout = setTimeout(() => {
            setAnimatedProgress(progress);
        }, 100);

        return () => clearTimeout(timeout);
    }, [progress]);

    return (
        <div
            onContextMenu={onContextMenu}
            className="
                bg-white/60 backdrop-blur-xl
                rounded-2xl
                border border-white/30
                shadow-md shadow-black/5
                p-6 cursor-pointer
                transition-all duration-1000
                hover:shadow-lg hover:shadow-black/10
                hover:-translate-y-[2px]
            "
        >
            {/* Title */}
            <h2 className="text-lg font-semibold text-gray-900 tracking-tight">
                {title}
            </h2>

            {/* Amounts */}
            <p className="text-sm text-gray-700 mt-2">
                <span className="text-gray-500">Target:</span> £{target.toLocaleString()}
            </p>

            <p className="text-sm text-gray-700">
                <span className="text-gray-500">Current:</span> £{current.toLocaleString()}
            </p>

            {/* Deadline */}
            <p className="text-xs text-gray-400 mt-2">
                {deadline}
            </p>

            {/* Progress Bar */}
            <div className="mt-4 w-full h-2 bg-gray-200/60 rounded-full overflow-hidden">
                <div
                    className="
                        h-full
                        bg-gradient-to-r from-red-400 to-rose-400
                        rounded-full
                        transition-all duration-1000 ease-out
                    "
                    style={{ width: `${animatedProgress}%` }}
                />
            </div>

            {/* Percentage */}
            <p className="text-right text-xs text-gray-500 mt-2">
                {progress.toFixed(0)}%
            </p>
        </div>
    );
};

export default GoalCard;