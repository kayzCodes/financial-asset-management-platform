import NavbarPublic from "../../components/public/NavbarPublic";
import ContactForm from "../../components/public/contact/ContactForm";

const Contact = () => {
    return (
        <div className="min-h-screen flex flex-col bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]">

            {/* Public Navbar */}
            <NavbarPublic />

            <div className="w-full flex justify-center mt-24">
                <div className="w-full max-w-xl">
                    <ContactForm />
                </div>
            </div>



        </div>
    );
};

export default Contact;
