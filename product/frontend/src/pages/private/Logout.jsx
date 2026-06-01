import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import { signOut } from "firebase/auth";
import { auth } from "../../firebase/firebase";
import { useAuth } from "../../context/AuthContext";

const Logout = () => {
    const { logout } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        signOut(auth)
            .then(() => {
                logout();
                navigate("/login");
            })
            .catch((err) => {
                console.error("Logout failed:", err);
                navigate("/login");
            });
    }, []);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]">
            <h1 className="text-xl font-semibold text-gray-800">
                Logging out...
            </h1>
        </div>
    );
};

export default Logout;
