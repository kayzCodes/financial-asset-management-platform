import { FiSearch } from "react-icons/fi";

const AssetSearchBar = ({
    placeholder = "Search...",
    value,
    onChange,
}) => {
    return (
        <div className="relative w-full">
            <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-900 text-lg" />

            <input
                type="text"
                placeholder={placeholder}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className="
                    w-full pl-10 pr-4 py-2
                    rounded-lg
                    bg-white/80 backdrop-blur-sm
                    shadow-md
                    text-gray-700
                    focus:outline-none focus:ring-2 focus:ring-red-300
                "
            />
        </div>
    );
};

export default AssetSearchBar;
