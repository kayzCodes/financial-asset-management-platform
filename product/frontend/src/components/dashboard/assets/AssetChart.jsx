import React, { useEffect, useRef, useState } from "react";
import { createChart } from "lightweight-charts";

const AssetChart = ({ title = "Price Chart", data = [] }) => {
    const containerRef = useRef(null);
    const chartRef = useRef(null);
    const seriesRef = useRef(null);
    const [isReady, setIsReady] = useState(false);

    // Create chart once container is measurable
    useEffect(() => {
        const el = containerRef.current;
        if (!el) return;

        const checkReady = setInterval(() => {
            if (el.clientWidth > 0 && !chartRef.current) {
                chartRef.current = createChart(el, {
                    width: el.clientWidth,
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

                seriesRef.current = chartRef.current.addLineSeries({
                    color: "#2563eb",
                    lineWidth: 2,
                });

                setIsReady(true);
                clearInterval(checkReady);
            }
        }, 50);

        return () => clearInterval(checkReady);
    }, []);

    // Update chart data (LOCKED CONTRACT: { date, close })
    useEffect(() => {
        if (!isReady || !seriesRef.current) return;
        if (!Array.isArray(data) || data.length === 0) return;

        const formattedData = data
            .filter(p => p?.date && typeof p.close === "number")
            .sort(
                (a, b) =>
                    new Date(a.date).getTime() -
                    new Date(b.date).getTime()
            )
            .map(p => ({
                time: p.date,      // ISO date
                value: p.close,    // closing price
            }));

        if (formattedData.length === 0) return;

        seriesRef.current.setData(formattedData);
        chartRef.current.timeScale().fitContent();
    }, [isReady, data]);

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
                    No price data available.
                </p>
            )}
        </div>
    );
};

export default AssetChart;
