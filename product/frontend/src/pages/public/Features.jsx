import NavbarPublic from "../../components/public/NavbarPublic";
import HeroFeatures from "../../sections/features/HeroFeatures";
import FeaturesGrid from "../../components/public/features/FeaturesGrid";

const Features = () => {
    return (
        <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B] flex flex-col">

            {/* Navbar */}
            <NavbarPublic />

            {/* Page Content */}
            <div className="flex flex-col items-center px-8 w-full">
                <HeroFeatures />
                <FeaturesGrid />
            </div>

        </div>
    );
};

export default Features;
