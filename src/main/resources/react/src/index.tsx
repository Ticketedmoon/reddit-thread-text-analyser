import React from "react";
import ReactDOM from "react-dom/client";
import {StyledEngineProvider} from "@mui/material";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {JobListingPage} from "./components/pages/JobListingPage";
import {JobResultViewPage} from "./components/pages/JobResultViewPage";
import axios, {AxiosResponse} from "axios";
import {ErrorPage} from "./components/pages/ErrorPage";
import {NavigationBar} from "./components/organisms/navbar/NavigationBar";
import {JobCreationPage} from "./components/pages/JobCreationPage";

import "./index.css";

const router = createBrowserRouter([
    {
        path: "/",
        errorElement: <ErrorPage/>,
        children: [
            {
                element: <NavigationBar/>,
                children: [
                    {
                        path: "/",
                        element: (<JobCreationPage/>)
                    },
                    {
                        path: "/results",
                        element: (<JobListingPage/>)
                    },
                    {
                        path: "/results/:jobId",
                        element: (<JobResultViewPage/>),
                        loader: async ({params}) => {
                            let res: AxiosResponse = await axios.get(`/api/job-reports/results/${params.jobId}`, {})
                            return res.data;
                        }
                    }
                ]
            }
        ]
    }
]);

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <RouterProvider router={router}/>
        </StyledEngineProvider>
    </React.StrictMode>
);