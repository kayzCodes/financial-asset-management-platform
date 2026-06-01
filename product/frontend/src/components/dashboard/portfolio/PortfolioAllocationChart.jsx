import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";

const COLORS = ["#ef4444", "#6366f1"];

const PortfolioAllocationChart = ({ stocksPercent, cryptoPercent }) => {
    const data = [
        {
            name: "Stocks",
            value: Number(stocksPercent),
        },
        {
            name: "Crypto",
            value: Number(cryptoPercent),
        },
    ];

    return (
        <div className="bg-white/80 backdrop-blur-md rounded-xl border border-white/40 shadow-md hover:shadow-lg transition-all duration-200 p-6">

            <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Asset Allocation
            </h2>

            <div className="w-full h-72">

                <ResponsiveContainer>
                    <PieChart>

                        <Pie
                            data={data}
                            dataKey="value"
                            nameKey="name"
                            cx="50%"
                            cy="50%"
                            outerRadius={90}
                            label={({ name, value }) =>
                                `${name} ${value.toFixed(1)}%`
                            }
                        >

                            {data.map((entry, index) => (
                                <Cell
                                    key={`cell-${index}`}
                                    fill={COLORS[index]}
                                />
                            ))}

                        </Pie>

                        <Tooltip
                            formatter={(value) => `${value.toFixed(2)}%`}
                        />

                    </PieChart>
                </ResponsiveContainer>

            </div>

        </div>
    );
};

export default PortfolioAllocationChart;