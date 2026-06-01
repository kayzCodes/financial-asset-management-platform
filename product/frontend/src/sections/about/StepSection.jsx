import StepCard from "../../components/public/About/StepCard";
import { FaUserPlus, FaChartLine, FaBullseye } from "react-icons/fa";

const StepSection = () => {
    return (
        <section className="py-16 px-8 bg-[#FFECEC]">
            <h2 className="text-3xl font-bold text-center text-gray-900 mb-10">
                How It Works
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-8 max-w-5xl mx-auto">

                <StepCard
                    step="1"
                    icon={<FaUserPlus />}
                    title="Create Your Account"
                    text="Set up your account and access your personal financial dashboard in seconds."
                />

                <StepCard
                    step="2"
                    icon={<FaChartLine />}
                    title="Build Your Portfolio"
                    text="Add your assets, track your portfolio, and organise your financial goals in one place."
                />

                <StepCard
                    step="3"
                    icon={<FaBullseye />}
                    title="Track & Stay Ahead"
                    text="Monitor performance, achieve your goals, and stay informed with personalised news and real-time market insights."
                />

            </div>
        </section>
    );
};

export default StepSection;