const FeatureCard = ({ icon, title, description }) => {
    return (
        <div
            className="
                bg-white/70 backdrop-blur-md
                rounded-2xl
                p-6 text-center
                border border-white/40

                shadow-md shadow-black/5
                transition-all duration-300

                hover:shadow-xl hover:shadow-red-300/30
                hover:-translate-y-1
            "
        >
            <div
                className="
                    text-4xl mb-4 text-rose-400
                    transition-transform duration-300
                    hover:scale-110
                "
            >
                {icon}
            </div>

            <h3 className="text-xl font-semibold mb-2 text-gray-900">
                {title}
            </h3>

            <p className="text-gray-600 text-sm leading-relaxed">
                {description}
            </p>
        </div>
    );
};

export default FeatureCard;