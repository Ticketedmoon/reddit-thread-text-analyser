import React from "react";
import {JobMetadata} from "../types/JobMetadata";
import {Box, Button, Typography} from "@mui/material";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import Paper from "@mui/material/Paper";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import {Outlet, useLoaderData} from "react-router-dom";

export const JobListingPage: React.FC = () => {

    const jobs: JobMetadata[] = useLoaderData() as JobMetadata[];

    return (
        <Box>
            <Box display="flex"
                 justifyContent="center"
                 pt={5}>
                {/* TODO Change this font */}
                <Typography fontFamily={"cursive"}
                            color={"#2bfb07"}
                            variant={"h5"}
                            fontWeight={"bold"}> Reddit Thread Text Analyser </Typography>
            </Box>
            <Box pt={5}
                 pl={30}
                 pr={30}
                 display="flex"
                 flexDirection="column">
                <TableContainer component={Paper}>
                    <Table sx={{minWidth: 650}} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell>Job ID</TableCell>
                                <TableCell align="right">Subreddit</TableCell>
                                <TableCell align="right">Start Time</TableCell>
                                <TableCell align="right">Completion Time</TableCell>
                                <TableCell align="right">Total Scanned Posts</TableCell>
                                <TableCell align="center">✅</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {jobs.map((jobMetadata, index) => (
                                <TableRow key={jobMetadata.id + "-" + index}
                                          sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                                    <TableCell component="th" scope="row">{jobMetadata.id}</TableCell>
                                    <TableCell align="right">{jobMetadata.subreddit}</TableCell>
                                    <TableCell align="right">{jobMetadata.jobStartTime}</TableCell>
                                    <TableCell align="right">{jobMetadata.jobFinishTime}</TableCell>
                                    <TableCell align="right">{jobMetadata.totalPostsToScan}</TableCell>

                                    {/* TODO can't be inside <TableRow> + make button smaller + nicer design */}
                                    <Button variant="contained"
                                            color="success">
                                        View Results
                                    </Button>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
            <Outlet/>
        </Box>
    )
}