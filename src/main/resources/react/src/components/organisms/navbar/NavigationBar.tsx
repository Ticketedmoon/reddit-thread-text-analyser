import React, {useEffect, useState} from "react";

import {Box, Typography} from "@mui/material";
import {Outlet, useLocation, useNavigate} from "react-router-dom";

enum PageState {
    JOB_CREATION_PAGE,
    COMPLETED_JOBS_PAGE
}

export const NavigationBar = (): JSX.Element => {

    const navigate = useNavigate();
    const location = useLocation();

    const [page, setPage] = useState<PageState>(PageState.JOB_CREATION_PAGE);

    useEffect(() => {
        const path = location.pathname;
        if (path === "/") {
            setPage(PageState.COMPLETED_JOBS_PAGE);
        } else if (path == "/results") {
            setPage(PageState.COMPLETED_JOBS_PAGE);
        }
    }, []);

    const handleTabChange = (pageState: PageState, routeForDirect: string) => {
        setPage(pageState);
        navigate(routeForDirect);
    }

    return (
        <Box id="navigation-wrapper">
            <Box width={1} display="flex" border="0.5px solid black" height="3em">
                {/* TODO Make this Box it's own component */}
                <Box display="flex"
                     flex={50}
                     borderRight="0.5px solid black"
                     justifyContent="center"
                     alignItems="center"
                     sx={page == PageState.JOB_CREATION_PAGE ? {
                         backgroundColor: '#00e73240'
                     } : {
                         ':hover': {
                             backgroundColor: '#00e73240',
                             cursor: 'pointer'
                         },
                     }}
                     onClick={() => handleTabChange(PageState.JOB_CREATION_PAGE, "/")}>
                    <Typography variant="subtitle1" sx={{
                        color: "black",
                        letterSpacing: "5px"
                    }}>Job Creation</Typography>
                </Box>

                <Box display="flex"
                     flex={50}
                     justifyContent="center"
                     alignItems="center"
                     sx={page == PageState.COMPLETED_JOBS_PAGE ? {
                         backgroundColor: '#00e73240'
                     } : {
                         ':hover': {
                             backgroundColor: '#00e73240',
                             cursor: 'pointer'
                         },
                     }}
                    onClick={() => handleTabChange(PageState.COMPLETED_JOBS_PAGE, "/results")}>
                    <Typography variant="subtitle1" sx={{
                        color: "black",
                        letterSpacing: "5px"
                    }}>Completed Jobs</Typography>
                </Box>
            </Box>
            <Outlet/>
        </Box>
    )
}