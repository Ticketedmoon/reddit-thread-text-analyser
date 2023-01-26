import React from "react";
import ReactDOM from "react-dom/client";
import {App} from "./components/App";
import {StyledEngineProvider} from "@mui/material";

ReactDOM.createRoot(document.querySelector("#root")).render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <App />
        </StyledEngineProvider>
    </React.StrictMode>
);