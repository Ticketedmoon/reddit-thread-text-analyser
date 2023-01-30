import React from "react";
import {Box, Divider, Typography} from "@mui/material";

export const JobCreationPage = (): JSX.Element => {
    return (
        <Box>
            <Box display="flex" justifyContent="center" pt={10}>
                <Box>
                    <Typography variant="h5">
                        Start a Subreddit Analysis
                    </Typography>
                </Box>
            </Box>

            <Box>
                <Divider orientation={"vertical"} flexItem/>
            </Box>
        </Box>
    )
}