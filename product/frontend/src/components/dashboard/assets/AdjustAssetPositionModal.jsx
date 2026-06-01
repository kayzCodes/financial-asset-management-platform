import { useEffect, useState } from "react";
import DashboardButton from "../DashboardButton";

const AdjustAssetPositionModal = ({
    isOpen,
    asset,
    type = "stocks",
    adjustMode, // "INCREASE" | "REDUCE"
    onClose,
    onSave,
}) => {
    const [quantity, setQuantity] = useState("");
    const [averagePurchasePrice, setAveragePurchasePrice] = useState("");

    useEffect(() => {
        if (asset && isOpen) {
            setQuantity("");
            setAveragePurchasePrice(
                adjustMode === "INCREASE"
                    ? asset.averagePurchasePrice ?? ""
                    : ""
            );
        }
    }, [asset, isOpen, adjustMode]);

    if (!isOpen || !asset) return null;

    const unit = type === "stocks" ? "shares" : "coins";

    const handleSubmit = () => {
        const qty = Number(quantity);

        if (!qty || qty <= 0) return;

        if (adjustMode === "INCREASE") {
            const avg = Number(averagePurchasePrice);
            if (!avg || avg <= 0) return;

            onSave({
                asset,
                adjustMode,
                quantity: qty,
                averagePurchasePrice: avg,
            });
        }

        if (adjustMode === "REDUCE") {
            onSave({
                asset,
                adjustMode,
                quantity: qty,
            });
        }
    };


    return (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-sm">

                <h2 className="text-xl font-semibold text-gray-900 mb-2">
                    {adjustMode === "INCREASE"
                        ? "Increase Position"
                        : "Reduce Position"}
                </h2>

                <p className="text-sm text-gray-600 mb-4">
                    {asset.companyName || asset.name || asset.symbol}
                </p>

                <div className="space-y-4">

                    {/* Quantity */}
                    <div>
                        <label className="block text-gray-700 font-medium mb-1">
                            {adjustMode === "INCREASE"
                                ? `Quantity (${unit})`
                                : `Quantity to reduce (${unit})`}
                        </label>
                        <input
                            type="number"
                            value={quantity}
                            onChange={(e) => setQuantity(e.target.value)}
                            className="w-full p-3 border rounded-lg bg-gray-50"
                            min="0"
                            step={type === "stocks" ? "1" : "0.0001"}
                        />
                    </div>

                    {/* Average Purchase Price — ONLY for INCREASE */}
                    {adjustMode === "INCREASE" && (
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                New Average Purchase Price
                            </label>
                            <input
                                type="number"
                                value={averagePurchasePrice}
                                onChange={(e) => setAveragePurchasePrice(e.target.value)}
                                className="w-full p-3 border rounded-lg bg-gray-50"
                                min="0"
                                step="0.01"
                            />
                        </div>
                    )}
                </div>

                {/* Buttons */}
                <div className="flex justify-end space-x-3 mt-6">
                    <DashboardButton
                        variant="secondary"
                        onClick={() => {
                            setQuantity("");
                            setAveragePurchasePrice("");
                            onClose();
                        }}
                    >
                        Cancel
                    </DashboardButton>


                    <DashboardButton onClick={handleSubmit}>
                        Save
                    </DashboardButton>
                </div>
            </div>
        </div>
    );
};

export default AdjustAssetPositionModal;
