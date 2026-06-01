import React, { useEffect, useRef } from "react";
import { createChart } from "lightweight-charts";

const PortfolioChart = ({ data }) => {
    const chartContainerRef = useRef(null);
    const chartRef = useRef(null);
    const seriesRef = useRef(null);

    // 1) Initialise chart ONCE
    useEffect(() => {
        if (!chartContainerRef.current) return;

        // Create chart only once
        chartRef.current = createChart(chartContainerRef.current, {
            width: chartContainerRef.current.clientWidth,
            height: 300,
            layout: {
                background: { color: "transparent" },
                textColor: "#333",
            },
            grid: {
                vertLines: { visible: false },
                horzLines: { visible: false },
            },
        });

        // Create line series ONCE
        seriesRef.current = chartRef.current.addLineSeries({
            color: "#e25555",
            lineWidth: 3,
        });

        // Resize handler
        const resize = () => {
            chartRef.current.applyOptions({
                width: chartContainerRef.current.clientWidth,
            });
        };

        window.addEventListener("resize", resize);

        return () => {
            window.removeEventListener("resize", resize);
            chartRef.current.remove();
        };
    }, []); // <-- RUNS ONCE


    // 2) Update chart data AFTER series exists
    useEffect(() => {
        if (!seriesRef.current) return;
        if (!data || data.length === 0) return;

        seriesRef.current.setData(data);
    }, [data]); // <-- RUNS WHEN DATA CHANGES


    return (
        <div
            ref={chartContainerRef}
            className="w-full h-80 bg-white/80 rounded-xl shadow border border-white/40"
        />
    );
};

export default PortfolioChart;
