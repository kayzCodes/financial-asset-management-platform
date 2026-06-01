const FeatureCard = ({ icon, title, text }) => {
    return (
        <div
            className="
                bg-white/70 backdrop-blur-md 
                rounded-xl shadow-md 
                p-6 
                border border-white/40
                flex flex-col items-start
                hover:shadow-lg hover:bg-white/80
                transition
            "
        >
            {/* Icon */}
            <div className="text-rose-500 text-3xl mb-3">
                {icon}
            </div>

            {/* Title */}
            <h3 className="text-lg font-semibold text-gray-900">
                {title}
            </h3>

            {/* Description */}
            <p className="text-gray-700 mt-2 text-sm leading-relaxed">
                {text}
            </p>
        </div>
    );
};

export default FeatureCard;
