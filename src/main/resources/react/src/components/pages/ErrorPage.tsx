import {Box, Typography} from "@mui/material";
import React from "react";
import {isRouteErrorResponse, useRouteError} from "react-router-dom";

export const ErrorPage = (): JSX.Element => {

    const error = useRouteError();

    return (
        <Box display="flex"
             justifyContent="center">
            {isRouteErrorResponse(error) ? (
                <Box display="flex" flexDirection="column" alignItems="center" gap={3}>
                    <Typography variant="h2">Oops!</Typography>
                    <Typography variant="subtitle1">Sorry, an unexpected error has occurred</Typography>
                    <Typography fontWeight={"bold"} color="red" variant="subtitle1">{error.statusText}</Typography>
                    {error.data?.message && <Typography color="red" variant="caption">{error.data.message}</Typography>}
                </Box>
            ) : (
                <Box display="flex" flexDirection="column" alignItems="center" gap={3}>
                    <Typography variant="h2">Oops!</Typography>
                    <Typography variant="subtitle1">Sorry, an unexpected error has occurred</Typography>
                </Box>
            )}
        </Box>
    )
}