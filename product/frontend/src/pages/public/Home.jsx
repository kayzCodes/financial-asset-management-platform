import { useState } from "react";
import NavbarPublic from "../../components/public/NavbarPublic";

const Home = () => {

    const tiles = [
        {
            title: "Track All Your Assets",
            text: "Manage all your financial accounts: bank, investments, crypto, property, vehicles, and more — all in one place."
        },
        {
            title: "Visualize Your Growth",
            text: "View charts, graphs, and financial insights including net worth, investment trends, and long-term performance."
        },
        {
            title: "Smart Budget & Savings",
            text: "Plan your monthly budget, set savings goals, and track your progress automatically with intelligent tools."
        },
        {
            title: "Personalized Financial Goals",
            text: "Set goals like buying a car or saving for a trip, monitor progress, and receive personalized suggestions."
        },
        {
            title: "Real-Time Market Insights",
            text: "Stay updated with the latest market news, stock activity, and economic trends — all in real time."
        },
        {
            title: "Secure & Private",
            text: "Your data is protected with end-to-end encryption, strong security, and a privacy-first design."
        }
    ];

    const [flipped, setFlipped] = useState(Array(6).fill(false));

    // 🔥 PARALLAX STATE
    const [mousePos, setMousePos] = useState({ x: 0, y: 0 });

    const handleMouseMove = (e) => {
        const x = (e.clientX / window.innerWidth - 0.5) * 15;
        const y = (e.clientY / window.innerHeight - 0.5) * 15;
        setMousePos({ x, y });
    };

    const toggleFlip = (index) => {
        setFlipped((prev) => {
            const updated = Array(6).fill(false);
            updated[index] = !prev[index];
            return updated;
        });
    };

    return (
        <>
            <div
                onMouseMove={handleMouseMove}
                className="relative min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col overflow-hidden"
            >

                {/* BACKGROUND GLOW BLOBS */}
                <div className="absolute top-[-120px] left-[-120px] w-[320px] h-[320px] bg-red-300/30 rounded-full blur-3xl animate-floatSlow"></div>
                <div className="absolute bottom-[-140px] right-[-140px] w-[380px] h-[380px] bg-rose-400/30 rounded-full blur-3xl animate-float"></div>
                <div className="absolute top-[40%] left-[60%] w-[260px] h-[260px] bg-pink-300/20 rounded-full blur-3xl animate-floatReverse"></div>

                {/* PARALLAX BACKGROUND */}
                <div
                    className="absolute inset-0 overflow-hidden z-0 pointer-events-none transition-transform duration-300 ease-out"
                    style={{
                        transform: `translate(${mousePos.x}px, ${mousePos.y}px)`
                    }}
                >

                    {/* MAIN CHART */}
                    <svg className="absolute top-24 left-10 w-[650px] opacity-[0.08] blur-[1px]" viewBox="0 0 600 200" fill="none">
                        <path d="M0 150 C100 50, 200 180, 300 80, 400 160, 500 60, 600 120" stroke="#ef4444" strokeWidth="3" />
                    </svg>

                    {/* SECOND CHART */}
                    <svg className="absolute bottom-16 right-10 w-[520px] opacity-[0.06] blur-[2px]" viewBox="0 0 500 200" fill="none">
                        <path d="M0 120 C80 30, 160 140, 240 60, 320 150, 400 40, 500 100" stroke="#fb7185" strokeWidth="2" />
                    </svg>

                    {/* SIDE CHARTS */}
                    <svg className="absolute top-[30%] left-[-80px] w-[500px] opacity-[0.04] blur-[2px]" viewBox="0 0 500 200" fill="none">
                        <path d="M0 100 C100 140, 200 60, 300 120, 400 80, 500 110" stroke="#ef4444" strokeWidth="2" />
                    </svg>

                    <svg className="absolute bottom-[20%] right-[-80px] w-[500px] opacity-[0.04] blur-[2px]" viewBox="0 0 500 200" fill="none">
                        <path d="M0 120 C120 60, 240 140, 360 80, 480 120" stroke="#fb7185" strokeWidth="2" />
                    </svg>

                    {/* TICKERS */}
                    <div className="absolute top-32 right-20 text-gray-800/20 text-xs font-mono animate-float">AAPL +1.24%</div>
                    <div className="absolute top-60 left-20 text-gray-800/20 text-xs font-mono animate-floatSlow">TSLA -0.82%</div>
                    <div className="absolute bottom-40 right-32 text-gray-800/20 text-xs font-mono animate-floatReverse">BTC +3.12%</div>
                    <div className="absolute bottom-20 left-40 text-gray-800/20 text-xs font-mono animate-float">AMZN +0.54%</div>

                    {/* EXTRA TICKERS */}
                    <div className="absolute top-20 left-6 text-gray-800/15 text-xs font-mono animate-floatSlow">MSFT +0.98%</div>
                    <div className="absolute top-[50%] right-6 text-gray-800/15 text-xs font-mono animate-float">NVDA +2.11%</div>
                    <div className="absolute bottom-10 left-10 text-gray-800/15 text-xs font-mono animate-floatReverse">ETH +1.87%</div>

                    {/* BUBBLES */}
                    <div className="absolute top-40 left-[30%] w-14 h-14 rounded-full bg-white/20 backdrop-blur-xl flex items-center justify-center text-xs text-gray-600 shadow-md animate-floatSlow">AAPL</div>
                    <div className="absolute bottom-32 right-[25%] w-14 h-14 rounded-full bg-white/20 backdrop-blur-xl flex items-center justify-center text-xs text-gray-600 shadow-md animate-float">TSLA</div>

                    {/* EXTRA BUBBLES */}
                    <div className="absolute top-24 left-10 w-10 h-10 rounded-full bg-white/10 backdrop-blur-md flex items-center justify-center text-[10px] text-gray-500 animate-float">BTC</div>
                    <div className="absolute bottom-24 right-12 w-10 h-10 rounded-full bg-white/10 backdrop-blur-md flex items-center justify-center text-[10px] text-gray-500 animate-floatSlow">ETH</div>
                    <div className="absolute top-[60%] left-6 w-8 h-8 rounded-full bg-white/10 backdrop-blur-md flex items-center justify-center text-[9px] text-gray-500 animate-floatReverse">NVDA</div>

                </div>

                <NavbarPublic />

                <div className="flex flex-col items-center justify-center flex-grow mt-10 relative z-10">
                    <div className="grid grid-cols-3 gap-6">
                        {tiles.map((tile, index) => (
                            <div key={index} className="relative w-60 h-60 cursor-pointer perspective group" onClick={() => toggleFlip(index)}>
                                <div className={`relative w-full h-full transition-transform duration-700 transform-style-preserve-3d ${flipped[index] ? "rotate-y-180" : ""}`}>

                                    <div className="absolute inset-0 bg-white/40 backdrop-blur-xl border border-white/30 rounded-2xl flex items-center justify-center text-center p-4 shadow-md group-hover:shadow-xl group-hover:-translate-y-1 backface-hidden">
                                        <h2 className="text-lg font-semibold text-gray-900">{tile.title}</h2>
                                    </div>

                                    <div className="absolute inset-0 bg-white/50 backdrop-blur-xl border border-white/30 rounded-2xl flex items-center justify-center text-center p-4 shadow-md rotate-y-180 backface-hidden">
                                        <p className="text-gray-800 text-sm">{tile.text}</p>
                                    </div>

                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            <style>{`
                .perspective { perspective: 1000px; }
                .rotate-y-180 { transform: rotateY(180deg); }
                .backface-hidden { backface-visibility: hidden; }
                .transform-style-preserve-3d { transform-style: preserve-3d; }

                @keyframes float {
                    0%,100%{transform:translateY(0)}
                    50%{transform:translateY(-20px)}
                }

                @keyframes floatSlow {
                    0%,100%{transform:translateY(0)}
                    50%{transform:translateY(-10px)}
                }

                @keyframes floatReverse {
                    0%,100%{transform:translateY(0)}
                    50%{transform:translateY(15px)}
                }

                .animate-float { animation: float 6s ease-in-out infinite; }
                .animate-floatSlow { animation: floatSlow 8s ease-in-out infinite; }
                .animate-floatReverse { animation: floatReverse 7s ease-in-out infinite; }
            `}</style>
        </>
    );
};

export default Home;