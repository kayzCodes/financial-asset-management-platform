import { useState, useEffect, useRef } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import NavbarDashboard from "../../components/dashboard/NavbarDashboard";
import SidebarDashboard from "../../components/dashboard/SidebarDashboard";

import DashboardButton from "../../components/dashboard/DashboardButton";
import GoalCard from "../../components/dashboard/goals/GoalCard";
import AddGoalModal from "../../components/dashboard/goals/AddGoalModal";

import useGoalModal from "../../hooks/useGoalModal";
import axios from "axios";

const Goals = () => {
    const { userLoggedIn, currentUser } = useAuth();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const {
        isOpen,
        mode,
        formData,
        updateField,
        openAddModal,
        openEditModal,
        closeModal,
        submitGoal
    } = useGoalModal();

    const [goals, setGoals] = useState([]);

    // Context menu
    const [contextMenuOpen, setContextMenuOpen] = useState(false);
    const [contextMenuPos, setContextMenuPos] = useState({ x: 0, y: 0 });
    const [selectedGoal, setSelectedGoal] = useState(null);

    // Delete modal + toast
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
    const [showToast, setShowToast] = useState(false);

    // Savings modal
    const [showSavingsModal, setShowSavingsModal] = useState(false);
    const [savingsAmount, setSavingsAmount] = useState("");

    const [showSavingsToast, setShowSavingsToast] = useState(false);
    const [lastSavedAmount, setLastSavedAmount] = useState(0);

    const deleteModalRef = useRef(null);

    if (!userLoggedIn) return <Navigate to="/login" replace />;

    // Disable background scroll when delete modal open
    useEffect(() => {
        document.body.style.overflow = showDeleteConfirm ? "hidden" : "auto";
        return () => (document.body.style.overflow = "auto");
    }, [showDeleteConfirm]);

    // Extracted fetchGoals (clean reuse)
    const fetchGoals = async (token) => {
        const res = await axios.get(
            "http://localhost:8080/api/userGoals/getGoals",
            { headers: { Authorization: `Bearer ${token}` } }
        );

        setGoals(Array.isArray(res.data) ? res.data : res.data.goals || []);
    };

    // GET GOALS
    useEffect(() => {
        if (!currentUser) return;

        const init = async () => {
            try {
                const token = await currentUser.getIdToken(true);
                await fetchGoals(token);
            } catch (err) {
                console.error("Failed to fetch goals:", err);
            }
        };

        init();
    }, [currentUser]);

    // ADD SAVINGS
    const handleAddSavings = async (goalId, amount) => {
        try {
            const token = await currentUser.getIdToken(true);

            await axios.post(
                "http://localhost:8080/api/savings/addSaving",
                { goalId, amount },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            await fetchGoals(token);

            setLastSavedAmount(amount);
            setShowSavingsToast(true);

            setShowSavingsModal(false);
            setSavingsAmount("");

            setTimeout(() => setShowSavingsToast(false), 2000);

        } catch (err) {
            console.error("Savings failed:", err);
        }
    };

    // Prefill edit modal
    const prefillEditForm = (goal) => {
        updateField("title", goal.title ?? goal.goalTitle ?? "");
        updateField("currentAmount", String(goal.currentAmount ?? 0));
        updateField("targetAmount", String(goal.targetAmount ?? 0));
        updateField("description", goal.description ?? "");
        updateField("deadline", goal.deadline ? goal.deadline.split("T")[0] : "");
    };

    // ADD / EDIT
    const handleSubmit = async () => {
        const g = submitGoal();

        const payload = {
            goalTitle: g.title,
            currentAmount: Number(g.currentAmount),
            targetAmount: Number(g.targetAmount),
            description: g.description || "",
            deadline: `${g.deadline}T00:00:00`,
        };

        try {
            const token = await currentUser.getIdToken(true);

            if (mode === "edit" && selectedGoal) {
                const res = await axios.put(
                    `http://localhost:8080/api/userGoals/updateGoal/${selectedGoal.id}`,
                    payload,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                setGoals(prev => prev.map(g => g.id === selectedGoal.id ? res.data : g));
            } else {
                const res = await axios.post(
                    "http://localhost:8080/api/userGoals/createGoal",
                    payload,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                setGoals(prev => [...prev, res.data]);
            }

            closeModal();
        } catch (err) {
            console.error("Failed to submit goal:", err);
        }
    };

    // DELETE
    const handleDeleteGoal = async (goalId) => {
        try {
            const token = await currentUser.getIdToken(true);
            await axios.delete(
                `http://localhost:8080/api/userGoals/deleteGoal/${goalId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setGoals(prev => prev.filter(g => g.id !== goalId));
            setShowDeleteConfirm(false);
            setShowToast(true);

            setTimeout(() => setShowToast(false), 2500);
        } catch (err) {
            console.error("Failed to delete goal:", err);
        }
    };

    // Right-click
    const handleRightClick = (e, goal) => {
        e.preventDefault();
        setSelectedGoal(goal);
        setContextMenuPos({ x: e.clientX, y: e.clientY });
        setContextMenuOpen(true);
    };

    // Close context menu
    useEffect(() => {
        const closeMenu = () => setContextMenuOpen(false);
        window.addEventListener("click", closeMenu);
        return () => window.removeEventListener("click", closeMenu);
    }, []);

    // Keyboard support for delete modal
    useEffect(() => {
        if (!showDeleteConfirm) return;

        deleteModalRef.current?.focus();

        const handleKey = (e) => {
            if (e.key === "Escape") setShowDeleteConfirm(false);
            if (e.key === "Enter") handleDeleteGoal(selectedGoal.id);
        };

        window.addEventListener("keydown", handleKey);
        return () => window.removeEventListener("keydown", handleKey);
    }, [showDeleteConfirm, selectedGoal]);

    return (
        <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col relative">

            <SidebarDashboard isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
            <NavbarDashboard onToggleSidebar={() => setSidebarOpen(true)} />

            <div className="flex justify-between items-center px-10 mt-10">
                <h1 className="text-4xl font-bold text-gray-900">Your Goals</h1>
                <DashboardButton onClick={openAddModal}>Add Goal</DashboardButton>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 px-10 my-10">
                {goals.map(goal => (
                    <GoalCard
                        key={goal.id}
                        goal={goal}
                        onContextMenu={(e) => handleRightClick(e, goal)}
                    />
                ))}
            </div>

            {/* Context Menu */}
            {contextMenuOpen && selectedGoal && (
                <div
                    className="fixed bg-white border rounded-lg shadow-lg z-40"
                    style={{ top: contextMenuPos.y, left: contextMenuPos.x }}
                >
                    <ul className="py-1">
                        <li
                            className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                            onClick={() => {
                                prefillEditForm(selectedGoal);
                                openEditModal(selectedGoal);
                                setContextMenuOpen(false);
                            }}
                        >
                            Edit
                        </li>
                        <li
                            className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                            onClick={() => {
                                setShowSavingsModal(true);
                                setContextMenuOpen(false);
                            }}
                        >
                            Add Savings
                        </li>
                        <li
                            className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-red-600"
                            onClick={() => {
                                setShowDeleteConfirm(true);
                                setContextMenuOpen(false);
                            }}
                        >
                            Delete
                        </li>
                    </ul>
                </div>
            )}

            {/* Savings Modal */}
            {showSavingsModal && selectedGoal && (
                <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
                    <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-sm">

                        <h2 className="text-lg font-bold text-gray-900">
                            Add Savings
                        </h2>

                        <p className="text-sm text-gray-600 mt-1">
                            {selectedGoal.goalTitle || selectedGoal.title}
                        </p>

                        <input
                            type="number"
                            value={savingsAmount}
                            onChange={(e) => setSavingsAmount(e.target.value)}
                            placeholder="Enter amount (£)"
                            className="w-full p-3 mt-4 bg-gray-50 border rounded-lg"
                        />

                        <div className="flex justify-end gap-2 mt-4">

                            <DashboardButton
                                variant="secondary"
                                onClick={() => {
                                    setShowSavingsModal(false);
                                    setSavingsAmount("");
                                }}
                            >
                                Cancel
                            </DashboardButton>

                            <DashboardButton
                                onClick={() => {
                                    const value = Number(savingsAmount);
                                    if (!value || value <= 0) return;

                                    handleAddSavings(selectedGoal.id, value);
                                }}
                            >
                                Add
                            </DashboardButton>

                        </div>
                    </div>
                </div>
            )}

            {/* Delete Modal + Toast (unchanged) */}
            {showDeleteConfirm && selectedGoal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div ref={deleteModalRef} tabIndex={-1} className="bg-white rounded-xl p-6 w-full max-w-sm">
                        <h3 className="text-lg font-semibold">Delete Goal</h3>
                        <p className="mt-2">
                            Delete <strong>{selectedGoal.goalTitle}</strong>?
                        </p>

                        <div className="flex justify-end gap-2 mt-4">
                            <DashboardButton variant="secondary" onClick={() => setShowDeleteConfirm(false)}>
                                Cancel
                            </DashboardButton>
                            <DashboardButton onClick={() => handleDeleteGoal(selectedGoal.id)}>
                                Delete
                            </DashboardButton>
                        </div>
                    </div>
                </div>
            )}

            {showSavingsToast && (
                <div className="fixed bottom-20 right-6 bg-rose-500 text-white px-4 py-2 rounded-lg shadow-lg">
                    +£{lastSavedAmount} added
                </div>
            )}

            {showToast && (
                <div className="fixed bottom-6 right-6 bg-green-600 text-white px-4 py-2 rounded-lg">
                    Goal deleted successfully
                </div>
            )}

            <AddGoalModal
                isOpen={isOpen}
                mode={mode}
                onClose={closeModal}
                formData={formData}
                updateField={updateField}
                submitGoal={handleSubmit}
            />
        </div>
    );
};

export default Goals;