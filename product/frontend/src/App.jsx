import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";

import { useEffect } from "react";

import "./i18n";
import ProtectedRoute from "./components/common/ProtectedRoute";

import Home from "./pages/public/Home";
import About from "./pages/public/About";
import Features from "./pages/public/Features";
import Contact from "./pages/public/Contact";
import News from "./pages/private/News";
import Signup from "./pages/public/Signup";
import Login from "./pages/public/Login";
import ForgotPassword from "./pages/public/ForgotPassword";
import SignupDetails from "./pages/public/SignupDetails";
import Dashboard from "./pages/private/Dashboard";
import Goals from "./pages/private/Goals";
import Stocks from "./pages/private/Stocks";
import Crypto from "./pages/private/Crypto";
import PortfolioOverview from "./pages/private/PortfolioOverview";
import Logout from "./pages/private/Logout";
import Settings from "./pages/private/Settings";
import Account from "./pages/private/Account";

const App = () => {

    useEffect(() => {
        const savedTheme = localStorage.getItem("theme");

        const root = document.documentElement;

        if (savedTheme === "Dark") {
            root.classList.add("dark");
        } else if (savedTheme === "Light") {
            root.classList.remove("dark");
        } else {
            const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;

            if (prefersDark) {
                root.classList.add("dark");
            } else {
                root.classList.remove("dark");
            }
        }
    }, []);

    return (
        <Router>
            <AuthProvider>
                <Routes>

                    {/* Public */}
                    <Route path="/" element={<Home />} />
                    <Route path="/about" element={<About />} />
                    <Route path="/features" element={<Features />} />
                    <Route path="/contact" element={<Contact />} />

                    {/* Auth */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/signup/details" element={<SignupDetails />} />
                    <Route path="/forgot-password" element={<ForgotPassword />} />

                    {/* Protected */}
                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                <Dashboard />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/goals"
                        element={
                            <ProtectedRoute>
                                <Goals />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/news"
                        element={
                            <ProtectedRoute>
                                <News />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/portfolio-overview"
                        element={
                            <ProtectedRoute>
                                <PortfolioOverview />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/assets/stocks"
                        element={
                            <ProtectedRoute>
                                <Stocks />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/assets/crypto"
                        element={
                            <ProtectedRoute>
                                <Crypto />
                            </ProtectedRoute>
                        }
                    />

                    {/*  NEW PROTECTED SETTINGS ROUTE */}
                    <Route
                        path="/settings"
                        element={
                            <ProtectedRoute>
                                <Settings />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/account"
                        element={
                            <ProtectedRoute>
                                <Account />
                            </ProtectedRoute>
                        }
                    />


                    {/* Logout */}
                    <Route path="/logout" element={<Logout />} />

                </Routes>
            </AuthProvider>
        </Router>
    );
};

export default App;
