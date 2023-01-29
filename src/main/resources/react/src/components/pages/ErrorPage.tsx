import {Box, Typography} from "@mui/material";
import React from "react";
import {isRouteErrorResponse, useRouteError} from "react-router-dom";

export const ErrorPage = (): JSX.Element => {

    const error = useRouteError();

    return (
        <Box display="flex"
             justifyContent="center">
            {isRouteErrorResponse(error) ? (
                <>
                    <Typography variant="h2">Oops!</Typography>
                    <Typography variant="subtitle1">Sorry, an unexpected error has occurred</Typography>
                    <Typography variant="caption">{error.statusText}</Typography>
                    {error.data?.message && <Typography variant="caption">{error.data.message}</Typography>}
                </>
            ) : (
                <>
                    <Typography variant="h2">Oops!</Typography>
                    <Typography variant="subtitle1">Sorry, an unexpected error has occurred</Typography>
                </>
            )}
        </Box>
    )
}