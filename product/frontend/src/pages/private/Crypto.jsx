// src/pages/Crypto.jsx
import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";

import AssetList from "../../components/dashboard/assets/AssetList";
import AssetDetails from "../../components/dashboard/assets/AssetDetails";
import AddAssetModal from "../../components/dashboard/assets/AddAssetModal";
import AdjustAssetPositionModal from "../../components/dashboard/assets/AdjustAssetPositionModal";
import useAssetModal from "../../hooks/useAssetModal";

import axios from "axios";

const Crypto = () => {
    const { userLoggedIn, currentUser } = useAuth();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const [cryptoList, setCryptoList] = useState([]);
    const [selectedAsset, setSelectedAsset] = useState(null);

    const [liveCryptoData, setLiveCryptoData] = useState(null);
    const [isRefreshing, setIsRefreshing] = useState(false);

    const [assetToDelete, setAssetToDelete] = useState(null);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
    const [showAdjustModal, setShowAdjustModal] = useState(false);

    const { isOpen, openModal, closeModal } = useAssetModal();
    const [adjustMode, setAdjustMode] = useState(null);

    const [searchQuery, setSearchQuery] = useState("");
    const [timeframe, setTimeframe] = useState("1Y");




    if (!userLoggedIn) return <Navigate to="/login" replace />;

    // -----------------------------
    // GET CRYPTOS
    // -----------------------------
    const fetchCryptos = async () => {
        if (!currentUser) return;

        try {
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                "http://localhost:8080/api/userCrypto/getCryptos",
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setCryptoList(res.data);
        } catch (err) {
            console.error("Failed to fetch cryptos:", err);
        }
    };

    useEffect(() => {
        fetchCryptos();
    }, [currentUser]);

    // -----------------------------
    // ADD CRYPTO
    // -----------------------------
    const handleAddCrypto = async (assetData) => {
        try {
            const token = await currentUser.getIdToken(true);

            const res = await axios.post(
                "http://localhost:8080/api/userCrypto/addCrypto",
                {
                    holding: {
                        symbol: assetData.tickerSymbol?.trim().toUpperCase(),
                        name: assetData.companyName?.trim(),
                    },
                    initialBuy: {
                        pricePerUnit: Number(assetData.averagePurchasePrice),
                        quantity: Number(assetData.quantity),
                    }
                },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setCryptoList((prev) => [...prev, res.data]);
            closeModal();
        } catch (err) {
            console.error("Failed to add crypto:", err);
        }
    };

    const handleAdjustSave = async ({
        asset,
        adjustMode,
        quantity,
        averagePurchasePrice,
    }) => {
        try {

            const token = await currentUser.getIdToken(true);

            await axios.put(
                `http://localhost:8080/api/userCrypto/updateCryptoBuy/${asset.holdingId}`,
                {
                    pricePerUnit: Number(averagePurchasePrice),
                    quantity: Number(quantity),
                    occurredAt: new Date().toISOString(),
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (adjustMode === "REDUCE") {
                await axios.put(
                    `http://localhost:8080/api/userCrypto/updateCryptoSell/${asset.holdingId}`,
                    {
                        quantity: Number(quantity),
                        occurredAt: new Date().toISOString(),
                    },
                    {
                        headers: { Authorization: `Bearer ${token}` },
                    }
                );
            }

            // REFRESH CRYPTO LIST (authoritative)
            await fetchCryptos();

            // Re-select asset so right panel stays in sync
            handleSelectCrypto(asset);

        } catch (err) {
            console.error("Failed to adjust crypto position:", err);
        } finally {
            setShowAdjustModal(false);
            setAdjustMode(null);
        }
    };


    // -----------------------------
    // DELETE CRYPTO
    // -----------------------------
    const handleDeleteAsset = async () => {
        if (!assetToDelete) return;

        try {
            const token = await currentUser.getIdToken(true);

            await axios.delete(
                `http://localhost:8080/api/userCrypto/deleteCrypto/${assetToDelete.holdingId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setCryptoList((prev) =>
                prev.filter((c) => c.holdingId !== assetToDelete.holdingId)
            );

            setSelectedAsset(null);
            setAssetToDelete(null);
            setShowDeleteConfirm(false);
        } catch (err) {
            console.error("Failed to delete crypto:", err.response || err);
        }
    };

    const handleRefreshLiveCryptoData = async (asset) => {
        if (!asset?.holdingId) return;

        try {
            setIsRefreshing(true);
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                `http://localhost:8080/api/userCrypto/getLiveCryptoDetails/${asset.holdingId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setLiveCryptoData(res.data);
        } catch (err) {
            console.error("Failed to refresh crypto live data:", err);
        } finally {
            setIsRefreshing(false);
        }
    };

    const handleSelectCrypto = async (asset, range) => {
        const effectiveRange = range ?? "1Y";

        setSelectedAsset(asset);
        setTimeframe(effectiveRange);
        setLiveCryptoData(null);
        setIsRefreshing(true);

        try {
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                `http://localhost:8080/api/userCrypto/getLiveCryptoDetails/${asset.holdingId}`,
                {
                    params: { range: effectiveRange },
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            setLiveCryptoData(res.data);
        } catch (err) {
            console.error("Failed to load live crypto data:", err);
        } finally {
            setIsRefreshing(false);
        }
    };

    const handleTimeframeChange = (newTimeframe) => {
        if (newTimeframe === timeframe) return;

        setTimeframe(newTimeframe);

        if (selectedAsset) {
            handleSelectCrypto(selectedAsset, newTimeframe);
        }
    };

    const filteredCryptoList = cryptoList.filter((asset) => {
        if (!searchQuery.trim()) return true;

        const query = searchQuery.toLowerCase();

        return (
            asset.name?.toLowerCase().includes(query) ||
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

            {/* MAIN CONTENT */}
            <div className="flex flex-1 px-8 mt-10 pb-10 gap-6 overflow-hidden">

                {/* LEFT LIST */}
                <div className="w-80 flex-shrink-0 overflow-y-auto">
                    <AssetList
                        type="crypto"
                        assets={filteredCryptoList}
                        selectedHoldingId={selectedAsset?.holdingId}
                        liveData={liveCryptoData}
                        searchValue={searchQuery}
                        onSearchChange={setSearchQuery}
                        onSelectAsset={handleSelectCrypto}
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
                        liveData={liveCryptoData}
                        timeframe={timeframe}
                        onTimeframeChange={handleTimeframeChange}
                        isRefreshing={isRefreshing}
                        onRefresh={() => handleRefreshLiveCryptoData(selectedAsset)}
                    />
                </div>
            </div>

            {/* ADD MODAL */}
            <AddAssetModal
                isOpen={isOpen}
                onClose={closeModal}
                type="Crypto"
                onSubmit={handleAddCrypto}
            />

            {/* ADJUST POSITION MODAL */}
            <AdjustAssetPositionModal
                isOpen={showAdjustModal}
                asset={selectedAsset}
                type="crypto"
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
                        <h3 className="text-lg font-semibold text-gray-900">
                            Delete Crypto
                        </h3>

                        <p className="text-gray-600 mt-2">
                            Are you sure you want to delete{" "}
                            <strong>{assetToDelete.name || assetToDelete.symbol}</strong>?
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

export default Crypto;
