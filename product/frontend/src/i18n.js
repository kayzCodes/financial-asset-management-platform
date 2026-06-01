import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const resources = {
    en: {
        translation: {
            // Core
            settings: "Settings",
            preferences: "Preferences",
            readOnly: "Read Only",

            // Buttons
            saveChanges: "Save Changes",
            savePreferences: "Save Preferences",
            saving: "Saving...",

            // Labels
            currency: "Currency",
            language: "Language",
            preferredLanguage: "Preferred Language",
            timezone: "Timezone",
            theme: "Theme",

            // Profile
            editAccountDetails: "Edit Account Details",
            emailReadOnly: "Email (read-only)",

            // Options
            english: "English",
            spanish: "Spanish",

            light: "Light",
            dark: "Dark",

            myDashboard: "My Dashboard",
            notifications: "Notifications",
            noUpdates: "No updates",
            account: "Account",
            logout: "Log Out",

            // Misc
            changesSaved: "Changes saved successfully",

            accountDetails: "Account Details",

            myDashboard: "My Dashboard",

            search: "Search...",
        }
    },
    es: {
        translation: {
            // Core
            settings: "Configuración",
            preferences: "Preferencias",
            readOnly: "Solo lectura",

            // Buttons
            saveChanges: "Guardar cambios",
            savePreferences: "Guardar preferencias",
            saving: "Guardando...",

            // Labels
            currency: "Moneda",
            language: "Idioma",
            preferredLanguage: "Idioma preferido",
            timezone: "Zona horaria",
            theme: "Tema",

            // Profile
            editAccountDetails: "Editar detalles de la cuenta",
            emailReadOnly: "Correo (solo lectura)",

            // Options
            english: "Inglés",
            spanish: "Español",
            french: "Francés",
            german: "Alemán",

            light: "Claro",
            dark: "Oscuro",

            // Misc
            changesSaved: "Cambios guardados correctamente",

            accountDetails: "Detalles de la cuenta",

            myDashboard: "Mi panel",

            search: "Buscar...",        }
    }
};

i18n
    .use(initReactI18next)
    .init({
        resources,
        lng: "en",
        fallbackLng: "en",
        interpolation: {
            escapeValue: false
        }
    });

export default i18n;