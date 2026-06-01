import { useState, useEffect } from "react";
import axios from "axios";
import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";
import DashboardButton from "../../components/dashboard/DashboardButton";
import { useAuth } from "../../context/AuthContext";

const Account = () => {
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const { currentUser } = useAuth();

    const [user, setUser] = useState(null);

    // Fetch backend user
    useEffect(() => {
        if (!currentUser) return;

        const fetchUser = async () => {
            try {
                const token = await currentUser.getIdToken(true);

                const res = await axios.get(
                    "http://localhost:8080/api/user/getUser",
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );
                setUser(res.data);
            } catch (err) {
                console.error("Failed to load user:", err);
            }
        };

        fetchUser();
    }, [currentUser]);

    const formatDateTime = (date) => {
        if (!date) return "Unknown";
        return new Date(date).toLocaleString("en-GB", {
            dateStyle: "medium",
            timeStyle: "short",
        });
    };

    const formatDate = (date) => {
        if (!date) return "Unknown";
        return new Date(date).toLocaleDateString("en-GB", {
            dateStyle: "medium",
        });
    };

    const fullName = `${user?.firstName || ""} ${user?.lastName || ""}`.trim();
    const username = user?.username || "Not set";
    const email = user?.email || "Unknown";
    const role = user?.role || "User";
    const bio =
        user?.bio ||
        "Passionate about personal finance, long-term investing, and building wealth through smart decisions.";

    return (
        <>
            <SidebarDashboard
                isOpen={sidebarOpen}
                onClose={() => setSidebarOpen(false)}
            />

            <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

                <NavbarDashboard onToggleSidebar={() => setSidebarOpen(true)} />

                <div className="p-10 flex justify-center">
                    <div className="bg-white/70 backdrop-blur-md border border-white/40 shadow-xl rounded-2xl p-10 w-full max-w-3xl">

                        <h1 className="text-3xl font-bold text-gray-900 mb-8 text-center">
                            My Account
                        </h1>

                        {/* Profile Picture */}
                        <div className="flex flex-col items-center mb-12">
                            <img
                                src={
                                    user?.profilePictureUrl
                                        ? user.profilePictureUrl.startsWith("http")
                                            ? user.profilePictureUrl
                                            : `http://localhost:8080${user.profilePictureUrl}`
                                        : "https://placehold.co/150"
                                }
                                onError={(e) => {
                                    e.currentTarget.src = "https://placehold.co/150";
                                }}
                                className="
                                        w-40 h-40
                                        rounded-full
                                        object-cover
                                        border-4 border-white
                                        shadow-lg
                                        ring-2 ring-red-200
                                    "
                                alt="Profile"
                            />

                            <h2 className="mt-6 text-2xl font-semibold text-gray-800 text-center break-words max-w-xs">
                                {fullName || "Unnamed User"}
                            </h2>

                            <p className="text-gray-600 text-md">@{username}</p>
                        </div>

                        {/* BIO */}
                        {bio && (
                            <div className="mb-10 text-center">
                                <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                    Bio
                                </h3>
                                <p className="text-gray-700 max-w-xl mx-auto whitespace-pre-wrap">
                                    {bio}
                                </p>
                            </div>
                        )}

                        {/* INFO GRID */}
                        <div className="grid grid-cols-2 gap-6 text-gray-800">

                            <div>
                                <p className="font-semibold">Email</p>
                                <p>{email}</p>
                            </div>

                            <div>
                                <p className="font-semibold">Role</p>
                                <p>{role}</p>
                            </div>

                            <div>
                                <p className="font-semibold">Last Login</p>
                                <p>{formatDateTime(user?.lastLoginAt)}</p>
                            </div>

                            <div>
                                <p className="font-semibold">Member Since</p>
                                <p>{formatDate(user?.createdAt)}</p>
                            </div>

                        </div>

                        {/* BUTTONS */}
                        <div className="mt-10 flex justify-center space-x-6">
                            <DashboardButton onClick={() => (window.location.href = "/settings")}>
                                Edit Settings
                            </DashboardButton>

                            <DashboardButton
                                variant="secondary"
                                onClick={() => (window.location.href = "/logout")}
                            >
                                Log Out
                            </DashboardButton>
                        </div>

                    </div>
                </div>
            </div>
        </>
    );
};

export default Account;