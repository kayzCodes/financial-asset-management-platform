import { useEffect, useRef } from "react";
import { createChart } from "lightweight-charts";

const LineChart = ({ title = "Chart", data = [], color = "#2563eb" }) => {
    const containerRef = useRef(null);
    const chartRef = useRef(null);
    const seriesRef = useRef(null);

    // -----------------------------
    // CREATE CHART
    // -----------------------------
    useEffect(() => {
        const container = containerRef.current;
        if (!container) return;

        const chart = createChart(container, {
            width: container.clientWidth,
            height: 320,
            layout: {
                background: { color: "transparent" },
                textColor: "#111",
            },
            grid: {
                vertLines: { visible: false },
                horzLines: { visible: false },
            },
            rightPriceScale: {
                borderVisible: false,
            },
            timeScale: {
                borderVisible: false,
                timeVisible: true,
            },
        });

        const lineSeries = chart.addLineSeries({
            color,
            lineWidth: 2,
        });

        chartRef.current = chart;
        seriesRef.current = lineSeries;

        // -----------------------------
        // HANDLE RESIZE
        // -----------------------------
        const resizeObserver = new ResizeObserver((entries) => {
            const { width } = entries[0].contentRect;

            if (width <= 0) return;

            chart.applyOptions({ width });
        });

        resizeObserver.observe(container);

        return () => {
            resizeObserver.disconnect();
            chart.remove();
        };
    }, [color]);

    // -----------------------------
    // UPDATE DATA
    // -----------------------------
    useEffect(() => {
        if (!seriesRef.current) return;
        if (!Array.isArray(data) || data.length === 0) return;

        const formattedData = data
            .filter((p) => p?.date && typeof p.close === "number")
            .sort(
                (a, b) =>
                    new Date(a.date).getTime() -
                    new Date(b.date).getTime()
            )
            .map((p) => ({
                time: p.date,
                value: p.close,
            }));

        if (formattedData.length === 0) return;

        seriesRef.current.setData(formattedData);
        chartRef.current.timeScale().fitContent();
    }, [data]);

    return (
        <div className="w-full bg-white/60 rounded-xl border shadow p-5">
            <div className="flex justify-between items-center mb-3">
                <h2 className="text-xl font-semibold text-gray-900">
                    {title}
                </h2>
            </div>

            <div
                ref={containerRef}
                className="w-full h-[320px] bg-white/70 rounded-lg border shadow-inner"
            />

            {Array.isArray(data) && data.length === 0 && (
                <p className="text-center text-gray-500 text-sm mt-2">
                    No chart data available.
                </p>
            )}
        </div>
    );
};

export default LineChart;