export function sliceByRange(data, range) {
    if (!Array.isArray(data) || data.length === 0) return data;

    switch (range) {
        case "1D":
            return data.slice(-1);

        case "1W":
            return data.slice(-5);   // ~5 trading days

        case "1M":
            return data.slice(-21);  // ~21 trading days

        case "1Y":
            return data;             // free-tier limitation

        default:
            return data;
    }
}
