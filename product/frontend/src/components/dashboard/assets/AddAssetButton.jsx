const AddAssetButton = ({ label = "Add Asset", onClick }) => {
    return (
        <button
            onClick={onClick}
            className="
                w-full mb-3
                px-4 py-2
                bg-red-400 hover:bg-red-500
                text-white font-semibold
                rounded-lg shadow
                transition
            "
        >
            {label}
        </button>
    );
};

export default AddAssetButton;
