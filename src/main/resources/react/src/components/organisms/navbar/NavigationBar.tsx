import React from "react";

import {Box, Typography} from "@mui/material";
import {Outlet, useNavigate} from "react-router-dom";

export const NavigationBar = (): JSX.Element => {

    const navigate = useNavigate();

    const handleTabChange = (routeForDirect: string) => {
        navigate(routeForDirect);
    }

    return (
        <Box id="navigation-wrapper">
            <Box width={1} display="flex" height="3em">
                {/* TODO Make this Box it's own component */}
                <Box display="flex"
                     flex={50}
                     justifyContent="center"
                     alignItems="center"
                     sx={{
                         backgroundColor: '#1976d2',
                         ':hover': {
                             backgroundColor: '#00000091',
                             cursor: 'pointer'
                         }
                     }}
                     onClick={() => handleTabChange("/")}>
                    <Typography variant="subtitle1" sx={{
                        fontFamily: "MyItimFont, arial, sans-serif",
                        fontSize: "1.2em",
                        color: "whitesmoke",
                        letterSpacing: "5px"
                    }}>Job Creation</Typography>
                </Box>

                <Box display="flex"
                     flex={50}
                     justifyContent="center"
                     alignItems="center"
                     sx={{
                         backgroundColor: '#1976d2',
                         ':hover': {
                             backgroundColor: '#00000091',
                             cursor: 'pointer'
                         }
                     }}
                    onClick={() => handleTabChange("/results")}>
                    <Typography variant="subtitle1" sx={{
                        color: "whitesmoke",
                        fontSize: "1.2em",
                        fontFamily: "MyItimFont, arial, sans-serif",
                        letterSpacing: "5px"
                    }}>Completed Jobs</Typography>
                </Box>
            </Box>
            <Outlet/>
        </Box>
    )
}