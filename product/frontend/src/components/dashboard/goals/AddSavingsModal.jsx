import { useState } from "react";
import DashboardButton from "../../dashboard/DashboardButton";

const AddSavingsModal = ({ isOpen, onClose, goal, onSubmit }) => {
    const [amount, setAmount] = useState("");

    if (!isOpen || !goal) return null;

    const handleSubmit = () => {
        const value = Number(amount);
        if (!value || value <= 0) return;

        onSubmit({
            goalId: goal.id,
            amount: value
        });

        setAmount("");
    };

    return (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md">

                <h2 className="text-xl font-bold text-gray-900">
                    Add Savings
                </h2>

                <p className="text-sm text-gray-600 mt-1">
                    {goal.goalTitle}
                </p>

                <div className="mt-4 space-y-4">

                    <input
                        type="number"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        placeholder="Enter amount (£)"
                        className="w-full p-3 bg-gray-50 border rounded-lg"
                    />

                </div>

                <div className="flex justify-end space-x-3 mt-6">
                    <DashboardButton variant="secondary" onClick={onClose}>
                        Cancel
                    </DashboardButton>

                    <DashboardButton onClick={handleSubmit}>
                        Add
                    </DashboardButton>
                </div>
            </div>
        </div>
    );
};

export default AddSavingsModal;