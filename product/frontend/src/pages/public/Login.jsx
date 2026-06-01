import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from "../../firebase/firebase";

import NavbarPublic from "../../components/public/NavbarPublic";

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const navigate = useNavigate();

    // Auto-hide error message
    useEffect(() => {
        if (errorMessage) {
            const timer = setTimeout(() => setErrorMessage(""), 3000);
            return () => clearTimeout(timer);
        }
    }, [errorMessage]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const userCred = await signInWithEmailAndPassword(auth, email, password);

            console.log("Firebase Login Success:", userCred.user);

            navigate("/dashboard");

        } catch (error) {
            console.error("Login failed:", error);
            setErrorMessage("Incorrect email or password.");
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

            {/* Navbar */}
            <NavbarPublic />

            {/* Page content */}
            <div className="flex flex-grow items-center justify-center">
                <div className="relative">

                    {/* Error Message (now consistent position) */}
                    {errorMessage && (
                        <div className="
                            absolute left-1/2 -translate-x-1/2 top-full mt-4
                            w-72 bg-red-100 border border-red-400 text-red-700
                            p-4 rounded-xl shadow-md text-center
                        ">
                            <p className="font-medium">Login Failed</p>
                            <p className="text-sm">{errorMessage}</p>
                        </div>
                    )}

                    {/* Form */}
                    <div className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md">
                        <h2 className="text-2xl font-bold text-center mb-6">Log In</h2>

                        <form className="space-y-4" onSubmit={handleSubmit}>

                            <input
                                type="email"
                                placeholder="Email"
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

                            <input
                                type="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="
                                    w-full px-4 py-2
                                    border border-gray-300
                                    rounded-lg
                                    focus:outline-none
                                    focus:ring-2 focus:ring-red-400
                                "
                            />
                            <p className="text-sm text-right text-gray-600">
                                <a href="/forgot-password" className="text-red-500 hover:underline">
                                    Forgot password?
                                </a>
                            </p>

                            <button
                                type="submit"
                                className="
                                    w-full
                                    bg-red-500 text-white
                                    py-2 rounded-lg
                                    transition
                                    hover:bg-red-600
                                    hover:-translate-y-[1px]
                                    shadow-md hover:shadow-red-300/30
                                "
                            >
                                Log In
                            </button>
                        </form>

                        <p className="text-center text-sm text-gray-600 mt-4">
                            Don’t have an account?{" "}
                            <a href="/signup" className="text-red-500 hover:underline">
                                Sign Up
                            </a>
                        </p>
                    </div>

                </div>
            </div>

        </div>
    );
};

export default Login;