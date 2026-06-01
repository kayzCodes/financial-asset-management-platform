import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import AssetListItem from "./AssetListItem";
import AssetSearchBar from "./AssetSearchBar";
import AddAssetButton from "./AddAssetButton";

const MENU_WIDTH = 192;

const AssetList = ({
    type = "stocks",
    assets = [],
    selectedHoldingId,
    liveData,
    searchValue,
    onSearchChange,
    onSelectAsset,
    onAddClick,
    onAdjustShares,
    onDeleteAsset,
}) => {

    const [contextMenuOpen, setContextMenuOpen] = useState(false);
    const [contextMenuPos, setContextMenuPos] = useState({ x: 0, y: 0 });
    const [contextMenuAsset, setContextMenuAsset] = useState(null);

    const handleRightClick = (e, asset) => {
        e.preventDefault();

        let x = e.clientX;
        let y = e.clientY;

        if (x + MENU_WIDTH > window.innerWidth) {
            x = window.innerWidth - MENU_WIDTH - 8;
        }

        setContextMenuAsset(asset);
        setContextMenuPos({ x, y });
        setContextMenuOpen(true);
    };

    useEffect(() => {
        const closeMenu = () => setContextMenuOpen(false);
        const onKey = (e) => e.key === "Escape" && closeMenu();

        window.addEventListener("click", closeMenu);
        window.addEventListener("scroll", closeMenu);
        window.addEventListener("keydown", onKey);

        return () => {
            window.removeEventListener("click", closeMenu);
            window.removeEventListener("scroll", closeMenu);
            window.removeEventListener("keydown", onKey);
        };
    }, []);

    return (
        <>
            <div className="
                w-64 h-full 
                bg-white/60 backdrop-blur-md 
                border border-white/40 shadow-md 
                rounded-xl p-4
                flex flex-col
            ">
                <AddAssetButton
                    label={`Add ${type === "stocks" ? "Stock" : "Crypto"}`}
                    onClick={onAddClick}
                />

                <div className="mt-4">
                    <AssetSearchBar
                        placeholder={`Search ${type}...`}
                        value={searchValue}
                        onChange={onSearchChange}
                    />
                </div>


                <div className="flex-1 overflow-y-auto space-y-2 pr-1 mt-4">
                    {assets.length === 0 ? (
                        <p className="text-gray-600 text-sm text-center">
                            No {type === "stocks" ? "stocks" : "crypto"} added yet.
                        </p>
                    ) : (
                        assets.map((asset) => {
                            const isSelected = asset.holdingId === selectedHoldingId;
                            const activeLiveMetrics = isSelected ? liveData : null;

                            return (
                                <AssetListItem
                                    key={asset.holdingId}
                                    asset={asset}
                                    type={type}
                                    onClick={() => onSelectAsset(asset)}
                                    onContextMenu={(e) => handleRightClick(e, asset)}
                                    isSelected={isSelected}
                                    activeLiveMetrics={activeLiveMetrics}
                                />
                            );
                        })
                    )}
                </div>
            </div>

            {/* CONTEXT MENU */}
            {contextMenuOpen &&
                contextMenuAsset &&
                createPortal(
                    <div
                        className="fixed bg-white border rounded-lg shadow-lg z-[9999] w-48"
                        style={{
                            top: contextMenuPos.y,
                            left: contextMenuPos.x,
                        }}
                    >
                        <ul className="py-1">
                            <li
                                className="px-4 py-2 w-full hover:bg-gray-100 cursor-pointer"
                                onClick={() => {
                                    onAdjustShares(contextMenuAsset, "INCREASE");
                                    setContextMenuOpen(false);
                                }}
                            >
                                Increase position
                            </li>

                            <li
                                className="px-4 py-2 w-full hover:bg-gray-100 cursor-pointer"
                                onClick={() => {
                                    onAdjustShares(contextMenuAsset, "REDUCE");
                                    setContextMenuOpen(false);
                                }}
                            >
                                Reduce position
                            </li>


                            <li
                                className="px-4 py-2 w-full cursor-pointer text-red-600 hover:bg-red-50"
                                onClick={() => {
                                    onDeleteAsset(contextMenuAsset);
                                    setContextMenuOpen(false);
                                }}
                            >
                                Exit Position
                            </li>
                        </ul>
                    </div>,
                    document.body
                )}
        </>
    );
};

export default AssetList;
