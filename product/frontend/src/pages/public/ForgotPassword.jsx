import { useState } from "react";
import { sendPasswordResetEmail } from "firebase/auth";
import { auth } from "../../firebase/firebase";
import NavbarPublic from "../../components/public/NavbarPublic";

const ForgotPassword = () => {
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {

        e.preventDefault();

        setMessage("");
        setError("");

        if (!email) {
            setError("Please enter your email.");
            return;
        }

        try {
            setLoading(true);

            console.log("CALLING FIREBASE");
            await sendPasswordResetEmail(auth, email);
            console.log("AFTER FIREBASE");

            setMessage("Check your inbox for a secure reset link.");

        } catch (err) {
            console.error(err);
            console.log("ERROR CODE:", err.code);

            if (err.code === "auth/user-not-found") {
                setError("No account found with this email.");
            } else if (err.code === "auth/invalid-email") {
                setError("Invalid email address.");
            } else {
                setError("Something went wrong. Try again.");
            }
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

            <NavbarPublic />

            <div className="flex flex-grow items-center justify-center">
                <div className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md">

                    <h2 className="text-2xl font-bold text-center mb-6">
                        Reset Password
                    </h2>

                    <form onSubmit={handleSubmit} className="space-y-4">

                        <input
                            type="email"
                            placeholder="Enter your email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="
                                w-full px-4 py-2
                                border border-gray-300
                                rounded-lg
                                focus:outline-none
                                focus:ring-2 focus:ring-red-400
                            "
                        />

                        {error && (
                            <p className="text-red-500 text-sm -mt-2">
                                {error}
                            </p>
                        )}

                        {message && (
                            <p className="text-green-600 text-sm -mt-2">
                                {message}
                            </p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="
                                w-full
                                bg-red-500 text-white
                                py-2 rounded-lg
                                transition
                                hover:bg-red-600
                                hover:-translate-y-[1px]
                                shadow-md hover:shadow-red-300/30
                                disabled:opacity-50
                            "
                        >
                            {loading ? "Sending..." : "Send Reset Link"}
                        </button>

                    </form>

                    <p className="text-center text-sm text-gray-600 mt-4">
                        Back to{" "}
                        <a href="/login" className="text-red-500 hover:underline">
                            Login
                        </a>
                    </p>

                </div>
            </div>
        </div>
    );
};

export default ForgotPassword;