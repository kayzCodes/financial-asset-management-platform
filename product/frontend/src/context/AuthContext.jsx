// src/context/AuthContext.jsx
import React, { useState, useEffect, useContext } from "react";
import axios from "axios";
import { auth } from "../firebase/firebase";
import { onAuthStateChanged } from "firebase/auth";

const AuthContext = React.createContext();

export function useAuth() {
    return useContext(AuthContext);
}

export function AuthProvider({ children }) {
    const [currentUser, setCurrentUser] = useState(null);
    const [userLoggedIn, setUserLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [profile, setProfile] = useState(undefined);

    const CHECK_USER_URL = "http://localhost:8080/api/user/checkUserByFirebaseUid";

    // Fetch backend profile
    const fetchProfile = async (firebaseUid) => {
        try {
            const token = await auth.currentUser.getIdToken(true);

            const res = await axios.get(`${CHECK_USER_URL}/${firebaseUid}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            return res.data;
        } catch (e) {
            console.error("❌ fetchProfile failed:", e);
            return null;
        }
    };

    // Called by SignupDetails AFTER registration
    const refreshProfile = async () => {
        if (!currentUser) return null;
        const data = await fetchProfile(currentUser.uid);
        setProfile(data);
        return data;
    };

    // Logout function (needed for Logout.jsx)
    const logout = () => {
        setCurrentUser(null);
        setUserLoggedIn(false);
        setProfile(undefined);
    };

    // Listen for Firebase login/logout
    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, async (user) => {

            if (!user) {
                setCurrentUser(null);
                setUserLoggedIn(false);
                setProfile(undefined);
                setLoading(false);
                return;
            }

            setCurrentUser(user);
            setUserLoggedIn(true);

            const backendUser = await fetchProfile(user.uid);
            setProfile(backendUser);

            setLoading(false);
        });

        return unsubscribe;
    }, []);

    const value = {
        currentUser,
        userLoggedIn,
        profile,
        loading,
        email: currentUser?.email ?? null,
        uid: currentUser?.uid ?? null,
        refreshProfile,
        logout
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
}
