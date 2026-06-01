const StepCard = ({ step, icon, title, text }) => {
    return (
        <div
            className="
                bg-white/70 backdrop-blur-md 
                rounded-xl shadow-md 
                p-6 
                border border-white/40
                hover:shadow-lg hover:bg-white/80
                transition flex flex-col
            "
        >
            {/* Step Number */}
            <div className="text-sm font-semibold text-rose-500 mb-2">
                Step {step}
            </div>

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

export default StepCard;
