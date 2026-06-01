import { useRef, useEffect, useState } from "react";
import DashboardButton from "../../dashboard/DashboardButton";

const AddGoalModal = ({ isOpen, mode, onClose, formData, updateField, submitGoal }) => {
    if (!isOpen) return null;

    // Track initial form snapshot
    const initialFormRef = useRef(null);

    // Confirmation modal state
    const [showConfirm, setShowConfirm] = useState(false);

    useEffect(() => {
        if (!isOpen) return;

        // Only set snapshot once, after formData is populated
        if (initialFormRef.current === null) {
            initialFormRef.current = JSON.stringify(formData);
            setShowConfirm(false);
        }
    }, [isOpen, formData]);


    const isDirty =
        initialFormRef.current !== null &&
        JSON.stringify(formData) !== initialFormRef.current;

    // Keyboard shortcuts
    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === "Escape") {
                if (isDirty) {
                    setShowConfirm(true);
                } else {
                    handleClose();
                }
            }

            if (e.key === "Enter") {
                if (mode === "edit" && !isDirty) return;
                handleSubmit();
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [isDirty, mode]);

    const handleClose = () => {
        initialFormRef.current = null;
        setShowConfirm(false);
        onClose();
    };

    const handleSubmit = () => {
        // HARD GUARD: never allow edit submit with no changes
        if (mode === "edit" && !isDirty) {
            return;
        }

        initialFormRef.current = null;
        submitGoal();
    };


    return (
        <>
            {/* Main Modal */}
            <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
                <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md">

                    {/* Title */}
                    <div className="flex items-center justify-between">
                        <h2 className="text-2xl font-bold text-gray-900">
                            {mode === "edit"
                                ? `Editing: ${formData.title || "Goal"}`
                                : "Create New Goal"}
                        </h2>

                        {isDirty && (
                            <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full">
                                Unsaved changes
                            </span>
                        )}
                    </div>

                    <div className="space-y-4 mt-4">

                        {/* Title */}
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                Goal Title
                            </label>
                            <input
                                type="text"
                                value={formData.title}
                                onChange={(e) => updateField("title", e.target.value)}
                                placeholder="New Car, Emergency Fund..."
                                className="w-full p-3 bg-gray-50 border rounded-lg"
                            />

                        </div>

                        {/* Current Amount */}
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                Current Amount (£)
                            </label>
                            <input
                                type="number"
                                value={formData.currentAmount}
                                onChange={(e) => updateField("currentAmount", e.target.value)}
                                placeholder="1200"
                                className="w-full p-3 bg-gray-50 border rounded-lg"
                            />

                        </div>

                        {/* Target Amount */}
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                Target Amount (£)
                            </label>
                            <input
                                type="number"
                                value={formData.targetAmount}
                                onChange={(e) => updateField("targetAmount", e.target.value)}
                                placeholder="5000"
                                className="w-full p-3 bg-gray-50 border rounded-lg"
                            />

                        </div>

                        {/* Deadline */}
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                Deadline
                            </label>
                            <input
                                type="date"
                                value={formData.deadline}
                                onChange={(e) => updateField("deadline", e.target.value)}
                                className="w-full p-3 bg-gray-50 border rounded-lg"
                            />
                        </div>

                        {/* Description */}
                        <div>
                            <label className="block text-gray-700 font-medium mb-1">
                                Description (Optional)
                            </label>
                            <textarea
                                value={formData.description}
                                onChange={(e) => updateField("description", e.target.value)}
                                placeholder="Why are you saving for this?"
                                className="w-full p-3 bg-gray-50 border rounded-lg"
                                rows="3"
                            />

                        </div>
                    </div>

                    {/* Buttons */}
                    <div className="flex justify-end space-x-3 mt-6">
                        <DashboardButton
                            variant="secondary"
                            onClick={() => {
                                if (isDirty) {
                                    setShowConfirm(true);
                                } else {
                                    handleClose();
                                }
                            }}
                        >
                            Cancel
                        </DashboardButton>

                        <DashboardButton
                            onClick={handleSubmit}
                            disabled={mode === "edit" && !isDirty}
                        >
                            {mode === "edit" ? "Save Changes" : "Create Goal"}
                        </DashboardButton>
                    </div>
                </div>
            </div>

            {/* Custom Confirmation Modal */}
            {showConfirm && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-60">
                    <div className="bg-white rounded-xl p-6 shadow-xl w-full max-w-sm">
                        <h3 className="text-lg font-semibold text-gray-900">
                            Discard changes?
                        </h3>
                        <p className="text-gray-600 mt-2">
                            You have unsaved changes. Are you sure you want to close?
                        </p>

                        <div className="flex justify-end space-x-3 mt-6">
                            <DashboardButton
                                variant="secondary"
                                onClick={() => setShowConfirm(false)}
                            >
                                Keep Editing
                            </DashboardButton>

                            <DashboardButton onClick={handleClose}>
                                Discard
                            </DashboardButton>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default AddGoalModal;
