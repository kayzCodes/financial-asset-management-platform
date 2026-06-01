import FeatureCard from "../../components/public/About/FeatureCard";

const FeatureSection = () => {
    return (
        <section className="py-16 px-8 bg-white/50 backdrop-blur-sm">
            <h2 className="text-3xl font-bold text-center text-gray-900 mb-10">
                What Makes Keystone Portfolio Different
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6 max-w-7xl mx-auto">

                <FeatureCard
                    title="Real-Time Tracking"
                    description="Get instant updates on your stocks, crypto, and total portfolio value."
                />

                <FeatureCard
                    title="Smart Analytics"
                    description="Visualize performance with clean charts and actionable insights."
                />

                <FeatureCard
                    title="Goal-Based Saving"
                    description="Set financial goals and track your progress with ease."
                />

                <FeatureCard
                    title="Personalised News"
                    description="Stay informed with curated financial news tailored to your portfolio."
                />

                <FeatureCard
                    title="Clean Modern Design"
                    description="A simple, elegant dashboard built for clarity and focus."
                />

            </div>
        </section>
    );
};

export default FeatureSection;