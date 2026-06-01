import NavbarPublic from "../../components/public/NavbarPublic";

import HeroSection from "../../sections/about/HeroSection";
import FeatureSection from "../../sections/about/FeatureSection";
import StepSection from "../../sections/about/StepSection";
import PreviewSection from "../../sections/about/PreviewSection";
import SecuritySection from "../../sections/about/SecuritySection";
import CTASection from "../../sections/about/CTASection";

const About = () => {
    return (
        <div className="min-h-screen bg-gradient-to-b from-[#FFDADA] via-[#FFD4D4] to-[#FF7B7B]">

            {/* FULL-WIDTH NAVBAR */}
            <NavbarPublic />

            {/* MAIN WRAPPER */}
            <div className="w-full flex flex-col items-center mt-16">

                {/* WIDER CONTENT CONTAINER */}
                <div className="w-full max-w-[1500px] px-12 space-y-24">

                    <HeroSection />
                    <FeatureSection />
                    <StepSection />
                    <PreviewSection />
                    <SecuritySection />
                    <CTASection />

                </div>

            </div>

        </div>
    );
};

export default About;
