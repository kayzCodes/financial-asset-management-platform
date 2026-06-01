import { FaLock, FaShieldAlt, FaUserSecret } from "react-icons/fa";

const SecuritySection = () => {
    return (
        <section className="w-full py-20 px-6 bg-[#FFECEC]">
            <div className="max-w-5xl mx-auto text-center">

                {/* Title */}
                <h2 className="text-4xl font-extrabold text-gray-900 mb-4">
                    Security You Can Trust
                </h2>

                {/* Subtitle */}
                <p className="text-gray-700 text-lg max-w-2xl mx-auto mb-12">
                    Your data is protected with industry-leading security, encrypted storage,
                    and a privacy-first design. Keystone Portfolio keeps your information safe—always.
                </p>

                {/* SECURITY GRID */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-10">

                    {/* CARD 1 */}
                    <div className="
                        bg-white/70 backdrop-blur-md rounded-2xl p-6
                        border border-white/40
                        flex flex-col items-center text-center
                        shadow-md shadow-black/5
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-300/30
                        hover:-translate-y-1
                    ">
                        <FaLock className="text-4xl text-red-400 mb-4" />
                        <h3 className="text-xl font-semibold text-gray-900">
                            Encrypted Data
                        </h3>
                        <p className="text-gray-600 mt-2 text-sm">
                            All data is securely encrypted both in transit and at rest.
                        </p>
                    </div>

                    {/* CARD 2 */}
                    <div className="
                        bg-white/70 backdrop-blur-md rounded-2xl p-6
                        border border-white/40
                        flex flex-col items-center text-center
                        shadow-md shadow-black/5
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-300/30
                        hover:-translate-y-1
                    ">
                        <FaShieldAlt className="text-4xl text-red-400 mb-4" />
                        <h3 className="text-xl font-semibold text-gray-900">
                            Secure Authentication
                        </h3>
                        <p className="text-gray-600 mt-2 text-sm">
                            Powered by Firebase Authentication for reliable account protection.
                        </p>
                    </div>

                    {/* CARD 3 */}
                    <div className="
                        bg-white/70 backdrop-blur-md rounded-2xl p-6
                        border border-white/40
                        flex flex-col items-center text-center
                        shadow-md shadow-black/5
                        transition-all duration-300
                        hover:shadow-xl hover:shadow-red-300/30
                        hover:-translate-y-1
                    ">
                        <FaUserSecret className="text-4xl text-red-400 mb-4" />
                        <h3 className="text-xl font-semibold text-gray-900">
                            Privacy First
                        </h3>
                        <p className="text-gray-600 mt-2 text-sm">
                            Your data is never shared or sold—your privacy is our priority.
                        </p>
                    </div>

                </div>
            </div>
        </section>
    );
};

export default SecuritySection;