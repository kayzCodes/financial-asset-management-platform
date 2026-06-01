import { useState } from "react";

const ContactForm = () => {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        alert("Message sent!");
        setName("");
        setEmail("");
        setMessage("");
    };

    return (
        <form
            onSubmit={handleSubmit}
            className="space-y-4 bg-white/70 backdrop-blur-md p-6 rounded-xl border border-rose-200 shadow-md"
        >
            {/* Name */}
            <div>
                <label className="block text-sm font-medium text-gray-800 mb-1">
                    Name
                </label>
                <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                    className="w-full px-4 py-2 border border-rose-200 rounded-lg focus:ring-2 focus:ring-rose-300 focus:outline-none bg-white/80"
                />
            </div>

            {/* Email */}
            <div>
                <label className="block text-sm font-medium text-gray-800 mb-1">
                    Email
                </label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    className="w-full px-4 py-2 border border-rose-200 rounded-lg focus:ring-2 focus:ring-rose-300 focus:outline-none bg-white/80"
                />
            </div>

            {/* Message */}
            <div>
                <label className="block text-sm font-medium text-gray-800 mb-1">
                    Message
                </label>
                <textarea
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    required
                    rows={4}
                    className="w-full px-4 py-2 border border-rose-200 rounded-lg focus:ring-2 focus:ring-rose-300 focus:outline-none bg-white/80"
                ></textarea>
            </div>

            {/* Submit */}
            {/* Submit */}
            <button
                type="submit"
                className="w-full bg-red-400 hover:bg-red-500 text-white py-2 rounded-lg transition shadow"
            >
                Send Message
            </button>

        </form>
    );
};

export default ContactForm;
