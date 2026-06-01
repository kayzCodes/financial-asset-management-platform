import { useState, useEffect } from "react";
import axios from "axios";
import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";
import DashboardButton from "../../components/dashboard/DashboardButton";
import { useAuth } from "../../context/AuthContext";
import { useTranslation } from "react-i18next";

const Settings = () => {
    const { t, i18n } = useTranslation();
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const [activeSection, setActiveSection] = useState("Account Details");

    const [showToast, setShowToast] = useState(false);

    const [isProfileSaving, setIsProfileSaving] = useState(false);
    const [isPreferencesSaving, setIsPreferencesSaving] = useState(false);

    const [initialProfile, setInitialProfile] = useState(null);

    const { currentUser } = useAuth();

    const [userMeta, setUserMeta] = useState(null);

    console.log("thus is the user info: ", userMeta);

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        username: "",
        email: "",
        profilePictureUrl: "",
        bio: "",
        phoneNumber: ""
    });

    const [preferences, setPreferences] = useState({
        currency: "USD",
        language: "English",
        timezone: "GMT",
        theme: "Light"
    });

    const isProfileChanged =
        initialProfile !== null && (
            formData.firstName !== initialProfile.firstName ||
            formData.lastName !== initialProfile.lastName ||
            formData.username !== initialProfile.username ||
            formData.profilePictureUrl !== initialProfile.profilePictureUrl ||
            formData.bio !== initialProfile.bio ||
            formData.phoneNumber !== initialProfile.phoneNumber
        );

    const isPreferencesChanged =
        userMeta !== null && (
            preferences.currency !== (userMeta.currency || "USD") ||
            preferences.language !== (userMeta.preferredLanguage || "English") ||
            preferences.timezone !== (userMeta.timezone || "GMT") ||
            preferences.theme !== (userMeta.theme || "Light")
        );

    const formatDate = (dateString) => {
        if (!dateString) return "Never";
        return new Date(dateString).toLocaleString("en-GB", {
            dateStyle: "medium",
            timeStyle: "short",
        });
    };

    const applyTheme = (theme) => {
        const root = document.documentElement;

        if (theme === "Dark") {
            root.classList.add("dark");
        } else {
            root.classList.remove("dark");
        }
    };

    const fetchUser = async () => {
        if (!currentUser) return;

        try {
            const token = await currentUser.getIdToken(true);

            const res = await axios.get(
                "http://localhost:8080/api/user/getUser",
                { headers: { Authorization: `Bearer ${token}` } }
            );

            const user = res.data;
            setUserMeta(user);

            const nextFormData = {
                firstName: user.firstName || "",
                lastName: user.lastName || "",
                username: user.username || "",
                email: user.email || "",
                profilePictureUrl: user.profilePictureUrl || "",
                bio: user.bio || "",
                phoneNumber: user.phoneNumber || ""
            };

            setFormData(nextFormData);

            setInitialProfile({
                firstName: nextFormData.firstName,
                lastName: nextFormData.lastName,
                username: nextFormData.username,
                profilePictureUrl: nextFormData.profilePictureUrl,
                bio: nextFormData.bio,
                phoneNumber: nextFormData.phoneNumber
            });

            const nextPreferences = {
                currency: user.currency || "USD",
                language: user.preferredLanguage || "English",
                timezone: user.timezone || "GMT",
                theme: user.theme || "Light"
            };

            setPreferences(nextPreferences);
            applyTheme(nextPreferences.theme);
        } catch (err) {
            console.error("Failed to fetch user", err);
        }
    };

    useEffect(() => {
        fetchUser();
    }, [currentUser]);

    const handleChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handlePrefChange = (e) => {
        setPreferences((prev) => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleProfileSave = async () => {
        try {
            setIsProfileSaving(true);

            const token = await currentUser.getIdToken(true);

            await axios.put(
                "http://localhost:8080/api/user/updateUserProfile",
                {
                    firstName: formData.firstName,
                    lastName: formData.lastName,
                    username: formData.username,
                    profilePictureUrl: formData.profilePictureUrl,
                    bio: formData.bio,
                    phoneNumber: formData.phoneNumber
                },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            const updatedProfile = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                username: formData.username,
                profilePictureUrl: formData.profilePictureUrl,
                bio: formData.bio,
                phoneNumber: formData.phoneNumber
            };

            setInitialProfile(updatedProfile);
            setUserMeta((prev) => ({
                ...prev,
                firstName: formData.firstName,
                lastName: formData.lastName,
                username: formData.username,
                profilePictureUrl: formData.profilePictureUrl,
                bio: formData.bio,
                phoneNumber: formData.phoneNumber
            }));

            setShowToast(true);
            setTimeout(() => setShowToast(false), 2000);
        } catch (err) {
            console.error("Failed to update profile", err);
        } finally {
            setIsProfileSaving(false);
        }
    };

    const handlePreferencesSave = async () => {
        try {
            setIsPreferencesSaving(true);

            const token = await currentUser.getIdToken(true);

            await axios.put(
                "http://localhost:8080/api/user/updatePreferences",
                preferences,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            i18n.changeLanguage(
                preferences.language === "Spanish" ? "es" : "en"
            );

            applyTheme(preferences.theme);
            localStorage.setItem("theme", preferences.theme);

            setUserMeta((prev) => ({
                ...prev,
                currency: preferences.currency,
                preferredLanguage: preferences.language,
                timezone: preferences.timezone,
                theme: preferences.theme
            }));

            setShowToast(true);
            setTimeout(() => setShowToast(false), 2000);
        } catch (err) {
            console.error("Failed to update preferences", err);
        } finally {
            setIsPreferencesSaving(false);
        }
    };

    return (
        <>
            <SidebarDashboard
                isOpen={sidebarOpen}
                onClose={() => setSidebarOpen(false)}
            />

            <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">
                <NavbarDashboard onToggleSidebar={() => setSidebarOpen(true)} />

                <div className="p-10">
                    <h1 className="text-3xl font-bold text-gray-900 mb-8">
                        {t("settings")}
                    </h1>

                    <div className="flex">

                        <div className="w-64 bg-white/70 backdrop-blur-md shadow-lg rounded-xl p-6 border border-white/40">
                            {[
                                { key: "Account Details", label: t("accountDetails") },
                                { key: "Preferences", label: t("preferences") },
                                { key: "Read Only", label: t("readOnly") }
                            ].map((section) => (
                                <button
                                    key={section.key}
                                    onClick={() => setActiveSection(section.key)}
                                    className={`w-full text-left px-3 py-2 rounded-lg mb-2 font-medium
                ${activeSection === section.key
                                            ? "bg-red-200 text-gray-900"
                                            : "hover:bg-gray-200 text-gray-700"}
            `}
                                >
                                    {section.label}
                                </button>
                            ))}
                        </div>

                        <div className="flex-1 ml-10 bg-white/60 backdrop-blur-md rounded-xl p-10 shadow-md border border-white/40">

                            {activeSection === "Account Details" && (
                                <>
                                    <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                                        {t("editAccountDetails")}
                                    </h2>

                                    {[
                                        "firstName",
                                        "lastName",
                                        "username",
                                        "profilePictureUrl",
                                        "bio",
                                        "phoneNumber"
                                    ].map((field) => (
                                        <div key={field} className="mb-4">
                                            <label className="block text-gray-700 font-medium mb-1">
                                                {field.replace(/([A-Z])/g, " $1")}
                                            </label>
                                            <input
                                                type="text"
                                                name={field}
                                                value={formData[field]}
                                                onChange={handleChange}
                                                className="w-full p-3 rounded-lg bg-white shadow border border-gray-300"
                                            />
                                        </div>
                                    ))}

                                    <div className="mb-4">
                                        <label className="block text-gray-700 font-medium mb-1">
                                            {t("emailReadOnly")}
                                        </label>
                                        <input
                                            type="email"
                                            value={formData.email}
                                            readOnly
                                            className="w-full p-3 rounded-lg bg-gray-200 border border-gray-300 cursor-not-allowed"
                                        />
                                    </div>

                                    <DashboardButton
                                        onClick={handleProfileSave}
                                        disabled={
                                            isProfileSaving ||
                                            !initialProfile ||
                                            !isProfileChanged
                                        }
                                    >
                                        {isProfileSaving ? t("saving") : t("saveChanges")}
                                    </DashboardButton>
                                </>
                            )}

                            {activeSection === "Preferences" && (
                                <>
                                    <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                                        {t("preferences")}
                                    </h2>

                                    <div className="space-y-5">

                                        <div>
                                            <label className="block text-gray-700 font-medium mb-1">
                                                {t("currency")}
                                            </label>
                                            <select
                                                name="currency"
                                                value={preferences.currency}
                                                onChange={handlePrefChange}
                                                className="w-full p-3 rounded-lg bg-white shadow border border-gray-300"
                                            >
                                                <option value="USD">USD</option>
                                                <option value="GBP">GBP</option>
                                                <option value="EUR">EUR</option>
                                            </select>
                                        </div>

                                        <div>
                                            <label className="block text-gray-700 font-medium mb-1">
                                                {t("preferredLanguage")}
                                            </label>
                                            <select
                                                name="language"
                                                value={preferences.language}
                                                onChange={handlePrefChange}
                                                className="w-full p-3 rounded-lg bg-white shadow border border-gray-300"
                                            >
                                                <option value="English">{t("english")}</option>
                                                <option value="Spanish">{t("spanish")}</option>
                                                {/* <option value="French">{t("french")}</option>
                                                <option value="German">{t("german")}</option> */}
                                            </select>
                                        </div>

                                        <div>
                                            <label className="block text-gray-700 font-medium mb-1">
                                                {t("timezone")}
                                            </label>
                                            <select
                                                name="timezone"
                                                value={preferences.timezone}
                                                onChange={handlePrefChange}
                                                className="w-full p-3 rounded-lg bg-white shadow border border-gray-300"
                                            >
                                                <option>GMT</option>
                                                <option>UTC</option>
                                                <option>CET</option>
                                                <option>EST</option>
                                                <option>PST</option>
                                            </select>
                                        </div>

                                        <div>
                                            <label className="block text-gray-700 font-medium mb-1">
                                                {t("theme")}
                                            </label>
                                            <select
                                                name="theme"
                                                value={preferences.theme}
                                                onChange={handlePrefChange}
                                                className="w-full p-3 rounded-lg bg-white shadow border border-gray-300"
                                            >
                                                <option>{t("light")}</option>
                                                <option>{t("dark")}</option>
                                            </select>
                                        </div>
                                    </div>

                                    <DashboardButton
                                        className="mt-6"
                                        onClick={handlePreferencesSave}
                                        disabled={
                                            isPreferencesSaving ||
                                            !userMeta ||
                                            !isPreferencesChanged
                                        }
                                    >
                                        {isPreferencesSaving ? t("saving") : t("savePreferences")}
                                    </DashboardButton>
                                </>
                            )}

                            {activeSection === "Read Only" && userMeta && (
                                <>
                                    <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                                        {t("readOnly")}
                                    </h2>

                                    <div className="space-y-4 text-gray-800">
                                        <div>
                                            <strong>{t("Email Verified")}:</strong>{" "}
                                            {userMeta.active ? "Yes" : "No"}
                                        </div>

                                        <div>
                                            <strong>{t("Last Login")}:</strong><br />
                                            {formatDate(userMeta.lastLoginAt)}
                                        </div>

                                        <div>
                                            <strong>{t("Account Created")}:</strong><br />
                                            {formatDate(userMeta.createdAt)}
                                        </div>
                                    </div>
                                </>
                            )}

                        </div>
                    </div>
                </div>

                {showToast && (
                    <div className="
                        fixed bottom-6 right-6
                        bg-red-500 text-white
                        px-4 py-2 rounded-lg shadow-lg
                        text-sm font-medium
                    ">
                        {t("changesSaved")}
                    </div>
                )}
            </div>
        </>
    );
};

export default Settings;