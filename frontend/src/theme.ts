import { createTheme, PaletteMode } from "@mui/material/styles";

// Shared settings that stay the same for both light and dark themes
const sharedSettings = {
  typography: {
    fontFamily: [
      "-apple-system",
      "BlinkMacSystemFont",
      '"Segoe UI"',
      "Roboto",
      '"Helvetica Neue"',
      "Arial",
      "sans-serif",
    ].join(","),
    h1: {
      fontSize: "2.5rem",
      fontWeight: 700,
      lineHeight: 1.2,
    },
    h2: {
      fontSize: "2rem",
      fontWeight: 600,
      lineHeight: 1.3,
    },
    h3: {
      fontSize: "1.75rem",
      fontWeight: 600,
      lineHeight: 1.4,
    },
    h4: {
      fontSize: "1.5rem",
      fontWeight: 500,
      lineHeight: 1.4,
    },
    h5: {
      fontSize: "1.25rem",
      fontWeight: 500,
      lineHeight: 1.5,
    },
    h6: {
      fontSize: "1rem",
      fontWeight: 500,
      lineHeight: 1.6,
    },
    button: {
      textTransform: "none" as const,
      fontWeight: 500,
    },
  },
  shape: {
    borderRadius: 8,
  },
  spacing: 8,
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: "8px 16px",
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
        },
      },
    },
  },
};

// Light theme palette
const lightPalette = {
  mode: "light" as PaletteMode,
  primary: {
    main: "#2196f3",
    light: "#64b5f6",
    dark: "#1976d2",
    contrastText: "#ffffff",
  },
  secondary: {
    main: "#f50057",
    light: "#ff4081",
    dark: "#c51162",
    contrastText: "#ffffff",
  },
  background: {
    default: "#f5f5f5",
    paper: "#ffffff",
  },
  text: {
    primary: "rgba(0, 0, 0, 0.87)",
    secondary: "rgba(0, 0, 0, 0.6)",
    disabled: "rgba(0, 0, 0, 0.38)",
  },
  divider: "rgba(0, 0, 0, 0.12)",
  error: {
    main: "#f44336",
  },
  warning: {
    main: "#ff9800",
  },
  info: {
    main: "#2196f3",
  },
  success: {
    main: "#4caf50",
  },
};

// Dark theme palette
const darkPalette = {
  mode: "dark" as PaletteMode,
  primary: {
    main: "#2196f3",
    light: "#64b5f6",
    dark: "#1976d2",
    contrastText: "#ffffff",
  },
  secondary: {
    main: "#f50057",
    light: "#ff4081",
    dark: "#c51162",
    contrastText: "#ffffff",
  },
  background: {
    default: "#121212",
    paper: "#1e1e1e",
  },
  text: {
    primary: "#ffffff",
    secondary: "rgba(255, 255, 255, 0.7)",
    disabled: "rgba(255, 255, 255, 0.5)",
  },
  divider: "rgba(255, 255, 255, 0.12)",
  error: {
    main: "#f44336",
  },
  warning: {
    main: "#ff9800",
  },
  info: {
    main: "#2196f3",
  },
  success: {
    main: "#4caf50",
  },
};

// Function to create theme based on mode
export const createAppTheme = (mode: PaletteMode) => {
  return createTheme({
    palette: mode === "light" ? lightPalette : darkPalette,
    ...sharedSettings,
  });
};

// Default export (dark theme as default)
const theme = createAppTheme("dark");
export default theme;
