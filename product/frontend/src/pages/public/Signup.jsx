import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { createUserWithEmailAndPassword } from "firebase/auth";
import { auth } from "../../firebase/firebase.js";

// Public Navbar
import NavbarPublic from "../../components/public/NavbarPublic.jsx";

const Signup = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [emailError, setEmailError] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [showRequirements, setShowRequirements] = useState(false);

    const navigate = useNavigate();

    const validatePassword = (pw) => {
        const minLength = pw.length >= 10;
        const hasUpper = /[A-Z]/.test(pw);
        const hasLower = /[a-z]/.test(pw);
        const hasNumber = /[0-9]/.test(pw);
        const hasSpecial = /[!@#_\-]/.test(pw);
        return minLength && hasUpper && hasLower && hasNumber && hasSpecial;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            setEmailError("Please enter a valid email address");
            return;
        }
        setEmailError("");

        if (!validatePassword(password)) {
            setPasswordError("Password does not meet the required criteria.");
            return;
        }
        setPasswordError("");

        try {
            const userCred = await createUserWithEmailAndPassword(auth, email, password);
            const uid = userCred.user.uid;

            navigate("/signup/details", {
                state: { email, uid }
            });

        } catch (err) {
            console.error("Signup error:", err.message);

            if (err.code === "auth/email-already-in-use") {
                setEmailError("This email is already registered.");
            } else {
                setEmailError("Signup failed. Try again.");
            }
        }
    };

    return (
        <div className="min-h-screen flex flex-col bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]">

            {/* Navbar */}
            <NavbarPublic />

            {/* Content */}
            <div className="flex flex-grow items-center justify-center">
                <div className="relative w-full max-w-md">

                    {/* FORM */}
                    <div className="bg-white p-8 rounded-2xl shadow-md w-full">
                        <h2 className="text-2xl font-bold text-center mb-6">Sign Up</h2>

                        <form onSubmit={handleSubmit} noValidate className="space-y-4">

                            {/* EMAIL */}
                            <input
                                type="email"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className={`
                                    w-full px-4 py-2
                                    border rounded-lg
                                    focus:outline-none
                                    focus:ring-2 focus:ring-red-400
                                    ${emailError ? "border-red-500" : "border-gray-300"}
                                `}
                            />

                            {emailError && (
                                <p className="text-red-500 text-sm -mt-2">
                                    {emailError}
                                </p>
                            )}

                            {/* PASSWORD */}
                            <input
                                type="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onFocus={() => setShowRequirements(true)}
                                className={`
                                    w-full px-4 py-2
                                    border rounded-lg
                                    focus:outline-none
                                    focus:ring-2 focus:ring-red-400
                                    ${passwordError ? "border-red-500" : "border-gray-300"}
                                `}
                            />

                            {passwordError && (
                                <p className="text-red-500 text-sm -mt-2">
                                    {passwordError}
                                </p>
                            )}

                            {/* BUTTON */}
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
                                Continue
                            </button>

                        </form>

                        {/* LOGIN LINK */}
                        <p className="text-center text-sm text-gray-600 mt-4">
                            Already have an account?{" "}
                            <Link to="/login" className="text-red-500 hover:underline">
                                Log in
                            </Link>
                        </p>
                    </div>

                    {/* PASSWORD REQUIREMENTS */}
                    {showRequirements && (
                        <div className="absolute left-1/2 -translate-x-1/2 top-full mt-4 bg-white/70 backdrop-blur-sm shadow-md p-4 rounded-xl w-64">
                            <button
                                onClick={() => setShowRequirements(false)}
                                className="absolute top-2 right-2 text-gray-500 hover:text-gray-800"
                            >
                                ✕
                            </button>

                            <h3 className="font-semibold text-lg mb-2">
                                Password Requirements
                            </h3>

                            <ul className="text-sm text-gray-700 space-y-1">
                                <li>• Minimum 10 characters</li>
                                <li>• At least one uppercase letter</li>
                                <li>• At least one lowercase letter</li>
                                <li>• At least one number</li>
                                <li>• At least one special character (! @ # _ -)</li>
                            </ul>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Signup;