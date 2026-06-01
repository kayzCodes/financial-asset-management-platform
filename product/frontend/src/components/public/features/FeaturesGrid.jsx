import FeatureCard from "./FeatureCard";
import { FaWallet, FaChartLine, FaListUl, FaBullseye, FaNewspaper, FaLock } from "react-icons/fa";

const FeaturesGrid = () => {
    const features = [
        {
            icon: <FaWallet />,
            title: "Portfolio Tracking",
            description: "Track stocks, crypto, and assets with real-time performance metrics."
        },
        {
            icon: <FaChartLine />,
            title: "Advanced Stock Charts",
            description: "Trading212-style charts with time ranges and indicators."
        },
        {
            icon: <FaListUl />,
            title: "Asset List & Details",
            description: "Scrollable list with detailed charts, stats, and news."
        },
        {
            icon: <FaBullseye />,
            title: "Goal Setting",
            description: "Set financial goals and track progress visually."
        },
        {
            icon: <FaNewspaper />,
            title: "News & Insights",
            description: "Market news feed and stock-specific updates."
        },
        {
            icon: <FaLock />,
            title: "Secure Account Management",
            description: "Protected with Firebase authentication and encrypted user data."
        }
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 px-6 mb-16">
            {features.map((feature, index) => (
                <FeatureCard
                    key={index}
                    icon={feature.icon}
                    title={feature.title}
                    description={feature.description}
                />
            ))}
        </div>
    );
};

export default FeaturesGrid;
