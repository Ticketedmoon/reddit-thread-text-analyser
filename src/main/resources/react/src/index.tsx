import React from "react";
import ReactDOM from "react-dom/client";
import {StyledEngineProvider} from "@mui/material";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {JobListingPage} from "./components/pages/JobListingPage";
import {JobResultViewPage} from "./components/pages/JobResultViewPage";
import axios, {AxiosResponse} from "axios";
import {ErrorPage} from "./components/pages/ErrorPage";

// TODO More work needed here, add fallback routes
const router = createBrowserRouter([
    {
        path: "/",
        element: (<JobListingPage/>),
        errorElement: <ErrorPage/>,
        loader: async () => {
            let res: AxiosResponse = await axios.get("/api/job-reports/results", {})
            return res.data;
        }
    },
    {
        path: "/results/:jobId",
        element: (<JobResultViewPage/>),
        errorElement: <ErrorPage/>,
        loader: async ({params}) => {
            let res: AxiosResponse = await axios.get(`/api/job-reports/results/${params.jobId}`, {})
            return res.data;
        }
    }
]);

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <RouterProvider router={router}/>
        </StyledEngineProvider>
    </React.StrictMode>
);