const AssetAbout = ({ asset, text, stats }) => {
    if (!asset) {
        return (
            <div className="bg-white/60 p-4 rounded-lg shadow border border-white/40 opacity-60">
                <p>Select an asset to view details.</p>
            </div>
        );
    }

    // Determine asset type
    const isStock = !!asset.companyName;
    const isCrypto = !!asset.cryptoName;

    const industry =
        stats?.Industry ??
        stats?.industry ??
        stats?.INDUSTRY ??
        "—";

    return (
        <div className="bg-white/60 p-4 rounded-lg shadow border border-white/40">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">
                About
            </h2>

            <div className="space-y-4 text-gray-700">

                {/* Asset Name */}
                <div>
                    <p className="font-medium">
                        {isStock ? "Company" : "Asset Name"}
                    </p>
                    <p className="text-sm text-gray-600">
                        {isStock
                            ? asset.companyName
                            : asset.cryptoName || asset.name || "—"}
                    </p>
                </div>


                {/* Industry (stocks only, optional but useful) */}
                {isStock && (
                    <div>
                        <p className="font-medium">Industry</p>
                        <p className="text-sm text-gray-600">
                            {industry}
                        </p>
                    </div>
                )}

                {/* Description (stocks only, when provided) */}
                {text && (
                    <div>
                        <p className="font-medium">Description</p>
                        <p className="text-sm text-gray-600 leading-relaxed">
                            {text}
                        </p>
                    </div>
                )}

            </div>
        </div>
    );
};

export default AssetAbout;
