const CTASection = () => {
    return (
        <section className="w-full py-24 px-6 bg-gradient-to-r from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]">
            <div className="max-w-4xl mx-auto text-center">

                {/* Title */}
                <h2 className="text-4xl sm:text-5xl font-extrabold text-gray-900 mb-6">
                    Take Control of Your Financial Future
                </h2>

                {/* Subtitle */}
                <p className="text-gray-700 text-lg max-w-2xl mx-auto mb-10 leading-relaxed">
                    Track your assets, manage your portfolio, achieve your goals, and stay ahead with
                    personalised financial news — all in one powerful, intuitive platform.
                </p>

                {/* Button */}
                <a
                    href="/signup"
                    className="
                        inline-block
                        px-10 py-4
                        bg-red-500 hover:bg-red-600
                        text-white font-semibold text-lg
                        rounded-2xl
                        shadow-lg shadow-red-300/40
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-400/50
                        hover:-translate-y-[2px]
                    "
                >
                    Get Started
                </a>

            </div>
        </section>
    );
};

export default CTASection;