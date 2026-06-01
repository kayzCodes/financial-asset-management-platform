const Navbar = () => {
    return (
        <nav className="bg-[#FFDADA] shadow-md py-4 px-8 flex justify-between items-center">

            {/* App / Brand Name */}
            <div className="text-2xl font-bold text-gray-800">
                Keystone Portfolio
            </div>

            {/* Center Links */}
            <div className="flex-1 flex justify-center space-x-8">
                <a href="/" className="text-gray-700 font-medium hover:text-rose-500">Home</a>
                <a href="/about" className="text-gray-700 font-medium hover:text-rose-500">About</a>
                <a href="/features" className="text-gray-700 font-medium hover:text-rose-500">Features</a>
                <a href="/contact" className="text-gray-700 font-medium hover:text-rose-500">Contact</a>
            </div>

            {/* Right Links */}
            <div className="flex space-x-8">
                <a href="/login" className="text-gray-700 font-medium hover:text-rose-500">Log In</a>
                <a href="/signup" className="text-gray-700 font-medium hover:text-rose-500">Sign Up</a>
            </div>
        </nav>
    );
};

export default Navbar;
