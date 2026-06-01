import dashboardImg from "/src/images/dashboardPreview.png";
import portfolioImg1 from "/src/images/portfolioHalfPreview1.png";
import portfolioImg2 from "/src/images/portfolioHalfPreview2.png";
import goalsImg from "/src/images/goalsPreview.png";
import newsImg from "/src/images/newsPreview.png";


const PreviewSection = () => {
    return (
        <section className="w-full py-20 px-6 bg-[#FFF6F6]">
            <div className="max-w-7xl mx-auto text-center">

                {/* Title */}
                <h2 className="text-4xl font-extrabold text-gray-900 mb-4">
                    Product Preview
                </h2>

                {/* Subtitle */}
                <p className="text-gray-700 text-lg max-w-2xl mx-auto mb-12">
                    Take a quick look at what Keystone Portfolio has to offer. Clean dashboards,
                    real-time analytics, and intuitive tracking tools built for clarity.
                </p>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 xl:grid-cols-4 gap-8">

                    {/* Dashboard */}
                    <div className="
                        bg-white/70 backdrop-blur-md rounded-xl 
                        shadow-md shadow-black/5
                        p-6 border border-white/40
                        h-[320px] flex flex-col justify-between
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-300/30
                        hover:-translate-y-1
                        group
                    ">
                        <div className="w-full h-56 flex items-center justify-center bg-gray-50 rounded-lg mb-4 overflow-hidden">
                            <img
                                src={dashboardImg}
                                alt="Preview"
                                className="
                            max-h-full max-w-full
                            object-contain
                        "
                            />
                        </div>

                        <h3 className="text-2xl font-semibold text-gray-900">Dashboard</h3>
                        <p className="text-gray-600 text-sm mt-1">
                            Your financial overview at a glance.
                        </p>
                    </div>

                    {/* Portfolio (SPLIT VIEW) */}
                    <div className="
                        bg-white/70 backdrop-blur-md rounded-xl 
                        shadow-md shadow-black/5
                        p-6 border border-white/40
                        h-[320px] flex flex-col justify-between
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-300/30
                        hover:-translate-y-1
                        group
                    ">
                        <div className="w-full h-56 flex gap-2 mb-4">
                            <div className="w-1/2 h-full flex items-center justify-center bg-gray-50 rounded-lg overflow-hidden">
                                <img
                                    src={portfolioImg1}
                                    alt="Portfolio 1"
                                    className="max-h-full max-w-full object-contain"
                                />
                            </div>
                            <div className="w-1/2 h-full flex items-center justify-center bg-gray-50 rounded-lg overflow-hidden">
                                <img
                                    src={portfolioImg2}
                                    alt="Portfolio 2"
                                    className="max-h-full max-w-full object-contain"
                                />
                            </div>
                        </div>

                        <h3 className="text-2xl font-semibold text-gray-900">Portfolio</h3>
                        <p className="text-gray-600 text-sm mt-1">
                            Track assets, stocks, and crypto instantly.
                        </p>
                    </div>

                    {/* Goals */}
                    <div className="
                        bg-white/70 backdrop-blur-md rounded-xl 
                        shadow-md shadow-black/5
                        p-6 border border-white/40
                        h-[320px] flex flex-col justify-between
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-300/30
                        hover:-translate-y-1
                        group
                    ">
                        <div className="w-full h-56 flex items-center justify-center bg-gray-50 rounded-lg mb-4 overflow-hidden">
                            <img
                                src={goalsImg}
                                alt="Preview"
                                className="
            max-h-full max-w-full
            object-contain
        "
                            />
                        </div>
                        <h3 className="text-2xl font-semibold text-gray-900">Goals</h3>
                        <p className="text-gray-600 text-sm mt-1">
                            Create, edit, and track your financial goals.
                        </p>
                    </div>

                    {/* News */}
                    <div className="
    bg-white/70 backdrop-blur-md rounded-xl 
    shadow-md shadow-black/5
    p-6 border border-white/40
    h-[320px] flex flex-col justify-between
    transition-all duration-300
    hover:shadow-xl hover:shadow-red-300/30
    hover:-translate-y-1
    group
">
                        <div className="w-full h-56 flex items-center justify-center bg-gray-50 rounded-lg mb-4 overflow-hidden">
                            <img
                                src={newsImg}
                                alt="Preview"
                                className="
                                    max-h-full max-w-full
                                    object-contain
                                "
                            />
                        </div>
                        <h3 className="text-2xl font-semibold text-gray-900">News</h3>
                        <p className="text-gray-600 text-sm mt-1">
                            Stay updated with personalised financial news.
                        </p>
                    </div>

                </div>
            </div>
        </section>
    );
};

export default PreviewSection;