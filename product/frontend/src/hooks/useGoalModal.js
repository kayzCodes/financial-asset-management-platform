import { useState } from "react";

const useGoalModal = () => {
    // Modal visibility
    const [isOpen, setIsOpen] = useState(false);

    // Mode: "add" or "edit"
    const [mode, setMode] = useState("add");

    // Form state
    const [formData, setFormData] = useState({
        title: "",
        currentAmount: "",
        targetAmount: "",
        deadline: "",
        description: "",
    });

    // Update form fields
    const updateField = (field, value) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
    };

    // Reset form
    const resetForm = () => {
        setFormData({
            title: "",
            currentAmount: "",
            targetAmount: "",
            deadline: "",
            description: "",
        });
        setMode("add");
    };

    // Open modal for ADD
    const openAddModal = () => {
        resetForm();
        setMode("add");
        setIsOpen(true);
    };

    // Open modal for EDIT (prefill ALL required fields)
    const openEditModal = (goal) => {
        setFormData({
            title: goal.title || goal.goalTitle || "",
            currentAmount: goal.currentAmount ?? "",
            targetAmount: goal.targetAmount ?? "",
            deadline: goal.deadline ? goal.deadline.split("T")[0] : "",
            description: goal.description || "",
        });
        setMode("edit");
        setIsOpen(true);
    };

    // Close modal
    const closeModal = () => {
        setIsOpen(false);
        resetForm();
    };

    // Submit form
    const submitGoal = () => {
        const data = { ...formData, mode };
        resetForm();
        setIsOpen(false);
        return data; // parent page handles saving
    };

    return {
        isOpen,
        mode,
        formData,
        updateField,
        openAddModal,
        openEditModal,
        closeModal,
        submitGoal,
    };
};

export default useGoalModal;
