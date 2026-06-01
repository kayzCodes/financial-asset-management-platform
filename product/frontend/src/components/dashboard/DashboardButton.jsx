const DashboardButton = ({
    children,
    onClick,
    className = "",
    variant = "primary",
    disabled = false
}) => {
    const base = "px-6 py-3 rounded-lg font-semibold shadow transition";

    const styles = {
        primary: "bg-red-400 hover:bg-red-500 text-white",
        secondary: "bg-gray-200 hover:bg-gray-300 text-gray-800",
    };

    const disabledStyles = "opacity-50 cursor-not-allowed hover:bg-red-400";

    return (
        <button
            onClick={onClick}
            disabled={disabled}
            className={`${base} ${styles[variant]} ${disabled ? disabledStyles : ""} ${className}`}
        >
            {children}
        </button>
    );
};

export default DashboardButton;