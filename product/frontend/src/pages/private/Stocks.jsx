import { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";

import AssetList from "../../components/dashboard/assets/AssetList";
import AssetDetails from "../../components/dashboard/assets/AssetDetails";
import AddAssetModal from "../../components/dashboard/assets/AddAssetModal";
import AdjustAssetPositionModal from "../../components/dashboard/assets/AdjustAssetPositionModal";
import useAssetModal from "../../hooks/useAssetModal";

import { EXCHANGE_CURRENCY } from "../../constants/exchanges";
import axios from "axios";

const Stocks = () => {
    const { userLoggedIn, currentUser } = useAuth();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const [stockList, setStockList] = useState([]);
    const [selectedAsset, setSelectedAsset] = useState(null);

    // 🔑 STEP 5 STATE
    const [liveData, setLiveData] = useState(null);
    const [isRefreshing, setIsRefreshing] = useState(false);

    const [assetToDelete, setAssetToDelete] = useState(null);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
    const [showAdjustModal, setShowAdjustModal] = useState(false);

    const { isOpen, openModal, closeModal } = useAssetModal();
    const [adjustMode, setAdjustMode] = useState(null); // "INCREASE" | "REDUCE"

    const [searchQuery, setSearchQuery] = useState("");
    const [timeframe, setTimeframe] = useState("1Y");


    if (!userLoggedIn) return <Navigate to="/login" replace />;

    // -------------------------------
    // GET USER STOCKS
    // -------------------------------

    const fetchStocks = async () => {
        if (!currentUser) return;

        try {
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                "http://localhost:8080/api/userStock/getStocks",
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setStockList(res.data);
        } catch (err) {
            console.error("Failed to fetch stocks:", err);
        }
    };

    useEffect(() => {
        fetchStocks();
    }, [currentUser]);


    const handleRefreshLiveData = async (asset) => {
        if (!asset?.holdingId) return;

        try {
            setIsRefreshing(true);
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                `http://localhost:8080/api/userStock/getLiveStockDetails/${asset.holdingId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setLiveData(res.data);
        } catch (err) {
            console.error("Failed to refresh live data:", err);
        } finally {
            setIsRefreshing(false);
        }
    };


    // -------------------------------
    // STEP 5: HANDLE ASSET CLICK
    // -------------------------------
    const handleSelectAsset = async (asset, range) => {
        const effectiveRange = range ?? "1Y";

        setSelectedAsset(asset);
        setTimeframe(effectiveRange);   // reset on new asset
        setLiveData(null);
        setIsRefreshing(true);


        try {
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                `http://localhost:8080/api/userStock/getLiveStockDetails/${asset.holdingId}`,
                {
                    params: { range: effectiveRange },
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            setLiveData(res.data);
        } catch (err) {
            console.error("Failed to load live stock data:", err);
        } finally {
            setIsRefreshing
                (false);
        }
    };

    const handleTimeframeChange = (newTimeframe) => {
        if (newTimeframe === timeframe) return;

        setTimeframe(newTimeframe);

        if (selectedAsset) {
            handleSelectAsset(selectedAsset, newTimeframe);
        }
    };


    // -------------------------------
    // ADD STOCK
    // -------------------------------
    const handleAddStock = async (assetData) => {
        try {
            const token = await currentUser.getIdToken(true);

            const currency = EXCHANGE_CURRENCY[assetData.exchange];
            if (!currency) return;

            const res = await axios.post(
                "http://localhost:8080/api/userStock/addStock",
                {
                    holding: {
                        tickerSymbol: assetData.tickerSymbol?.trim().toUpperCase(),
                        companyName: assetData.companyName?.trim(),
                        currency: currency
                    },
                    initialBuy: {
                        pricePerUnit: Number(assetData.averagePurchasePrice),
                        quantity: Number(assetData.quantity)
                    }
                },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setStockList(prev => [...prev, res.data]);
            closeModal();
        } catch (err) {
            console.error("Failed to add stock:", err);
        }
    };

    // -------------------------------
    // UPDATE STOCK (POSITION)
    // -------------------------------
    const handleAdjustSave = async ({
        asset,
        adjustMode,
        quantity,
        averagePurchasePrice,
    }) => {
        try {
            const token = await currentUser.getIdToken(true);

            if (adjustMode === "INCREASE") {
                await axios.put(
                    `http://localhost:8080/api/userStock/updateStockBuy/${asset.holdingId}`,
                    {
                        pricePerUnit: Number(averagePurchasePrice),
                        quantity: Number(quantity),
                    },
                    {
                        headers: { Authorization: `Bearer ${token}` },
                    }
                );
            }

            if (adjustMode === "REDUCE") {
                await axios.put(
                    `http://localhost:8080/api/userStock/updateStockSell/${asset.holdingId}`,
                    {
                        quantity: Number(quantity),
                    },
                    {
                        headers: { Authorization: `Bearer ${token}` },
                    }
                );
            }

            // REFRESH LIST (authoritative)
            await fetchStocks();

            // Re-select asset so right panel stays in sync
            handleSelectAsset(asset);

        } catch (err) {
            console.error("Failed to adjust position:", err);
        } finally {
            setShowAdjustModal(false);
            setAdjustMode(null);
        }
    };

    // -------------------------------
    // DELETE STOCK
    // -------------------------------
    const handleDeleteAsset = async () => {
        if (!assetToDelete) return;

        try {
            const token = await currentUser.getIdToken(true);

            await axios.delete(
                `http://localhost:8080/api/userStock/deleteStock/${assetToDelete.holdingId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setStockList(prev =>
                prev.filter(s => s.holdingId !== assetToDelete.holdingId)
            );

            setSelectedAsset(null);
            setLiveData(null);
            setAssetToDelete(null);
            setShowDeleteConfirm(false);
        } catch (err) {
            console.error("Failed to delete stock:", err);
        }
    };

    const filteredStockList = stockList.filter((asset) => {
        if (!searchQuery.trim()) return true;

        const query = searchQuery.toLowerCase();

        return (
            asset.companyName?.toLowerCase().includes(query) ||
            asset.tickerSymbol?.toLowerCase().includes(query) ||
            asset.symbol?.toLowerCase().includes(query)
        );
    });


    return (
        <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

            <SidebarDashboard
                isOpen={sidebarOpen}
                onClose={() => setSidebarOpen(false)}
            />

            <NavbarDashboard onToggleSidebar={() => setSidebarOpen(true)} />

            <div className="flex flex-1 px-8 mt-10 pb-10 gap-6 overflow-hidden">

                {/* LEFT LIST */}
                <div className="w-80 flex-shrink-0 overflow-y-auto">
                    <AssetList
                        type="stocks"
                        assets={filteredStockList}
                        selectedHoldingId={selectedAsset?.holdingId}
                        liveData={liveData}
                        searchValue={searchQuery}
                        onSearchChange={setSearchQuery}
                        onSelectAsset={handleSelectAsset}
                        onAddClick={openModal}
                        onAdjustShares={(asset, mode) => {
                            setSelectedAsset(asset);
                            setAdjustMode(mode);
                            setShowAdjustModal(true);
                        }}
                        onDeleteAsset={(asset) => {
                            setAssetToDelete(asset);
                            setShowDeleteConfirm(true);
                        }}
                    />



                </div>

                {/* RIGHT DETAILS */}
                <div className="flex-1 overflow-y-auto pr-4">
                    <AssetDetails
                        asset={selectedAsset}
                        liveData={liveData}
                        timeframe={timeframe}
                        onTimeframeChange={handleTimeframeChange}
                        isRefreshing={isRefreshing}
                        onRefresh={() => handleRefreshLiveData(selectedAsset)}
                    />
                </div>
            </div>

            {/* ADD MODAL */}
            <AddAssetModal
                isOpen={isOpen}
                onClose={closeModal}
                type="Stock"
                onSubmit={handleAddStock}
            />

            {/* ADJUST POSITION */}
            <AdjustAssetPositionModal
                isOpen={showAdjustModal}
                asset={selectedAsset}
                type="stocks"
                adjustMode={adjustMode}
                onClose={() => {
                    setShowAdjustModal(false);
                    setAdjustMode(null);
                }}
                onSave={handleAdjustSave}
            />

            {/* DELETE CONFIRM */}
            {showDeleteConfirm && assetToDelete && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 shadow-xl w-full max-w-sm">
                        <h3 className="text-lg font-semibold">Delete Stock</h3>

                        <p className="text-gray-600 mt-2">
                            Are you sure you want to delete{" "}
                            <strong>{assetToDelete.companyName}</strong>?
                        </p>

                        <div className="flex justify-end space-x-3 mt-6">
                            <button
                                className="px-4 py-2 rounded bg-gray-200"
                                onClick={() => setShowDeleteConfirm(false)}
                            >
                                Cancel
                            </button>

                            <button
                                className="px-4 py-2 rounded bg-red-600 text-white"
                                onClick={handleDeleteAsset}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Stocks;
