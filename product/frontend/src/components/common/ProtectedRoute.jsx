// src/components/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const ProtectedRoute = ({ children }) => {
    const { userLoggedIn, profile, loading } = useAuth();

    // Don't decide anything until AuthContext finishes initial checks
    if (loading || profile === undefined) {
        return null; // or <Spinner />
    }

    // User not logged in → go to login
    if (!userLoggedIn) {
        return <Navigate to="/login" replace />;
    }

    // User logged in but backend has NO profile → must complete signup
    if (profile === null) {
        return <Navigate to="/signup/details" replace />;
    }

    // User exists but missing signup-required fields
    const incomplete =
        !profile.firstName ||
        !profile.lastName ||
        !profile.username;

    if (incomplete) {
        return <Navigate to="/signup/details" replace />;
    }

    // Fully authenticated & fully registered → allow access
    return children;
};

export default ProtectedRoute;
