const HeroSection = () => {
    return (
        <section
            className="
                w-full py-24 px-6
                bg-gradient-to-r from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]
                text-center
            "
        >
            <div className="max-w-3xl mx-auto">

                {/* App Name */}
                <h1 className="text-4xl sm:text-5xl font-extrabold text-gray-900 drop-shadow-lg">
                    Keystone Portfolio
                </h1>

                {/* Subtitle */}
                <p className="mt-4 text-lg sm:text-xl text-gray-700 leading-relaxed max-w-2xl mx-auto">
                    Take control of your finances with a unified platform to track assets,
                    monitor your portfolio, manage goals, and stay ahead with personalised financial news
                    and real-time market insights — all in one place.
                </p>

            </div>
        </section>
    );
};

export default HeroSection;