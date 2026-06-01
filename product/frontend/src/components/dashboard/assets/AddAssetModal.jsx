import React, { useState, useEffect } from "react";
import DashboardButton from "../DashboardButton";

const AddAssetModal = ({ isOpen, onClose, type = "Stock", onSubmit }) => {
    if (!isOpen) return null;

    const [formData, setFormData] = useState({
        tickerSymbol: "",
        companyName: "",
        quantity: "",
        averagePurchasePrice: "",
        exchange: "", // only used for Stock
        notes: ""
    });

    useEffect(() => {
        if (isOpen) {
            setFormData({
                tickerSymbol: "",
                companyName: "",
                quantity: "",
                averagePurchasePrice: "",
                exchange: "",
                notes: ""
            });
        }
    }, [isOpen]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleAddAsset = () => {
        const cleanedTicker = formData.tickerSymbol.trim().toUpperCase();
        const cleanedCompanyName = formData.companyName.trim();

        const cleanedQuantity = Number(formData.quantity);
        const cleanedAvgPrice = Number(formData.averagePurchasePrice);

        // Basic validation
        if (
            !cleanedTicker ||
            !cleanedCompanyName ||
            isNaN(cleanedQuantity) ||
            isNaN(cleanedAvgPrice)
        ) {
            console.error("Invalid input");
            return;
        }

        onSubmit({
            ...formData,
            tickerSymbol: cleanedTicker,
            companyName: cleanedCompanyName,
            quantity: cleanedQuantity,
            averagePurchasePrice: cleanedAvgPrice,
        });
    };


    return (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-md border border-gray-200">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                    Add {type}
                </h2>

                <div className="space-y-4">

                    {/* Stock Exchange — ONLY FOR STOCKS */}
                    {type === "Stock" && (
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                Stock Exchange
                            </label>
                            <select
                                name="exchange"
                                value={formData.exchange}
                                onChange={handleChange}
                                className="w-full p-3 border rounded-lg bg-gray-50"
                            >
                                <option value="">Select exchange</option>
                                <option value="LSE">London Stock Exchange (LSE)</option>
                                <option value="NASDAQ">NASDAQ</option>
                                <option value="NYSE">New York Stock Exchange (NYSE)</option>
                                <option value="XETRA">XETRA</option>
                            </select>
                        </div>
                    )}

                    {/* Ticker Symbol */}
                    <div>
                        <label className="block text-gray-700 font-medium mb-1">
                            Ticker Symbol
                        </label>
                        <input
                            type="text"
                            name="tickerSymbol"
                            value={formData.tickerSymbol}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg bg-gray-50"
                            placeholder={type === "Crypto" ? "BTC" : "AAPL"}
                        />
                    </div>

                    {/* Company / Asset Name */}
                    <div>
                        <label className="block text-gray-700 font-medium mb-1">
                            {type === "Crypto" ? "Asset Name" : "Company Name"}
                        </label>
                        <input
                            type="text"
                            name="companyName"
                            value={formData.companyName}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg bg-gray-50"
                            placeholder={type === "Crypto" ? "Bitcoin" : "Apple Inc."}
                        />
                    </div>

                    {/* Quantity */}
                    <div>
                        <label className="block text-gray-700 font-medium mb-1">
                            Quantity
                        </label>
                        <input
                            type="number"
                            name="quantity"
                            value={formData.quantity}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg bg-gray-50"
                            placeholder="0"
                            min="0"
                            step={type === "Crypto" ? "0.0001" : "1"}
                        />
                    </div>

                    {/* Avg Purchase Price */}
                    <div>
                        <label className="block text-gray-700 font-medium mb-1">
                            Average Purchase Price
                        </label>
                        <input
                            type="number"
                            name="averagePurchasePrice"
                            value={formData.averagePurchasePrice}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg bg-gray-50"
                            placeholder="150.00"
                            min="0"
                            step="0.01"
                        />
                    </div>

                </div>

                <div className="flex justify-end space-x-3 mt-6">
                    <DashboardButton variant="secondary" onClick={onClose}>
                        Cancel
                    </DashboardButton>

                    <DashboardButton onClick={handleAddAsset}>
                        Add {type}
                    </DashboardButton>
                </div>
            </div>
        </div>
    );
};

export default AddAssetModal;
