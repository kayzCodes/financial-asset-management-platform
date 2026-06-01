import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../context/AuthContext";

const SignupDetails = () => {
    const navigate = useNavigate();
    const { currentUser, email, refreshProfile } = useAuth();

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [username, setUsername] = useState("");

    if (!currentUser) {
        return (
            <div className="min-h-screen flex justify-center items-center text-lg">
                Loading your account...
            </div>
        );
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        const userData = {
            firebaseUid: currentUser.uid,
            email,
            firstName,
            lastName,
            username
        };

        try {
            await axios.post(
                "http://localhost:8080/api/user/registerUser",
                userData
            );

            await currentUser.getIdToken(true);
            await refreshProfile();

            navigate("/dashboard", { replace: true });

        } catch (error) {
            console.error("Signup failed:", error);
            alert("Signup failed — see console for details.");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]">
            <div className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md">
                <h2 className="text-2xl font-bold text-center mb-6">Your Details</h2>

                <form className="space-y-4" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="First Name"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        className="
                            w-full px-4 py-2
                            border border-gray-300
                            rounded-lg
                            focus:outline-none
                            focus:ring-2 focus:ring-red-400
                        "
                    />

                    <input
                        type="text"
                        placeholder="Last Name"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        className="
                            w-full px-4 py-2
                            border border-gray-300
                            rounded-lg
                            focus:outline-none
                            focus:ring-2 focus:ring-red-400
                        "
                    />

                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="
                            w-full px-4 py-2
                            border border-gray-300
                            rounded-lg
                            focus:outline-none
                            focus:ring-2 focus:ring-red-400
                        "
                    />

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
            </div>
        </div>
    );
};

export default SignupDetails;