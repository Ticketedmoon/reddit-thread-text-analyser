import React, {ChangeEvent, useState} from "react";
import {Alert, Box, Button, Divider, Slider, Snackbar, TextField, Typography} from "@mui/material";
import UploadIcon from '@mui/icons-material/Upload';
import axios from "axios";
import {useNavigate} from "react-router-dom";

const marks = [
    {
        value: 25,
        label: "25",
    },
    {
        value: 50,
        label: "50",
    },
    {
        value: 75,
        label: "75",
    },
    {
        value: 100,
        label: "100",
    },
];

export const JobCreationPage = (): JSX.Element => {

    const navigate = useNavigate();

    const [subRedditName, setRedditName] = useState<string>("");
    const [totalPostsToScanForSubReddit, setTotalPostsToScanForSubReddit] = useState<number>(25);
    const [isFormError, setFormError] = useState<boolean>(false);
    const [isSnackbarOpen, setSnackbarOpen] = useState<boolean>(false);

    const startJob = () => {
        if (subRedditName.length === 0) {
            setFormError(true);
        } else {
            axios.post("/api/job-reports", {}, {
                params: {
                    "subreddit": subRedditName,
                    "totalPosts": totalPostsToScanForSubReddit
                }
            }).then(() => {
                // Snackbar displaying job successfully started
                // Redirect to Job Results page
                openSnackbar();
                setTimeout(() => navigate("/results"), 3000);
            }).catch((e) => {
                // TODO Replace with Snackbar
                console.log("error: ", e)
            })
        }
    }

    const handleTotalPostsToScanSliderChange = (_: Event, value: number) => {
        const totalPostsToScanForSubReddit = Array.isArray(value) ? value[0] : value;
        setTotalPostsToScanForSubReddit(totalPostsToScanForSubReddit);
    }

    const handleSubRedditNameChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (isFormError) {
            setFormError(false);
        }
        setRedditName(e.target.value);
    }

    const openSnackbar = () => {
        setSnackbarOpen(true);
    }

    const handleSnackBarClose = () => {
        setSnackbarOpen(false);
    }

    return (
        <Box>
            <Snackbar
                anchorOrigin={{
                    vertical: "top",
                    horizontal: "right"
                }}
                autoHideDuration={3000}
                open={isSnackbarOpen}
                onClose={handleSnackBarClose}
                key={"top-right-snackbar"}>
                <Alert onClose={handleSnackBarClose}
                       severity="success">
                    Success! Redirecting to analysis list...
                </Alert>
            </Snackbar>

            <Box display="grid"
                 gridTemplateColumns="3fr 0.5fr 3fr"
                 alignItems="center"
                 mt={3}>

                {/* Left Column */}
                <Box display="flex"
                     justifyContent="center"
                     alignSelf="center"
                     flexDirection="column"
                     gap={5}
                     pl={10}>
                    <Box>
                        <Typography variant="h5"
                                    sx={{
                                        color: '#1976d2',
                                        fontFamily: "MyItimFont, arial, sans-serif",
                                    }}>
                            How it works
                        </Typography>
                    </Box>
                    <Box>
                        <Typography
                            sx={{
                                lineHeight: '24px'
                            }}
                            variant="caption">
                            This application enables to ability to scan any subreddit of your choosing by means of
                            counting the words used in Subreddit Posts, Comments and replies.
                            This can be interesting to observe the most common words used by different subreddit
                            communities and how the lingo for these communities changes over time.
                            To start your own analysis, simply input a subreddit name into the input box and
                            select the total amount of posts to analyse - maximum 100. Prepositions, Stop-words and links
                            will automatically be removed. <br/>
                            To view your jobs select the `Completed Jobs` navbar item however you will be redirected
                            here automatically on submission of a new analysis.
                        </Typography>
                    </Box>
                    <Box display="flex" flexDirection="column">
                        <Typography sx={{
                            fontFamily: "MyItimFont, arial, sans-serif",
                            lineHeight: '32px'
                        }}>
                            Developer: <span style={{color: '#1976d2'}}> Shane Creedon </span> <br/>
                            Email: <span style={{color: '#1976d2'}}> shane.creedon3@mail.dcu.ie </span> <br/>
                            Github: <span style={{color: '#1976d2'}}> https://github.com/Ticketedmoon </span> <br/>
                            Project Name: <span style={{color: '#1976d2'}}> Reddit Thread Text Analyser </span> <br/>
                            Version: <span style={{color: '#1976d2'}}> 1.0.0 </span> <br/>
                        </Typography>
                    </Box>
                </Box>

                {/* Middle Column */}
                <Box display="flex"
                     justifyContent="center">
                    <Divider orientation={"vertical"}
                             flexItem
                             sx={{
                                 height: '500px',
                                 borderWidth: '1px'
                             }}/>
                </Box>

                {/* Right Column */}
                <Box component="form">
                    <Box display="flex"
                         justifyContent="center"
                         flexDirection="column"
                         gap={5}
                         alignSelf="center">
                        <Box>
                            <Typography variant="h5"
                                        sx={{
                                            color: '#1976d2',
                                            fontFamily: "MyItimFont, arial, sans-serif",
                                        }}>
                                Start a Subreddit Analysis
                            </Typography>
                        </Box>
                        <Box>
                            <TextField label="Subreddit Name"
                                       color="primary"
                                       focused
                                       error={isFormError}
                                       autoFocus
                                       helperText={isFormError ? "Subreddit name required" : ""}
                                       onInput={handleSubRedditNameChange}
                                       sx={{
                                           width: 300
                                       }}/>
                        </Box>
                        <Box display="flex"
                             flexDirection="column"
                             sx={{width: 400}}>
                            <Typography id="non-linear-slider" gutterBottom>
                                Choose how many top Reddit posts to scan
                            </Typography>
                            <Slider
                                aria-label="Always visible"
                                defaultValue={25}
                                min={25}
                                max={100}
                                onChange={handleTotalPostsToScanSliderChange}
                                step={1}
                                marks={marks}
                                valueLabelDisplay="auto"
                            />
                        </Box>
                        <Box>
                            <Button variant="contained"
                                    onClick={startJob}
                                    endIcon={<UploadIcon/>}>
                                Start Analysis
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Box>
        </Box>
    )
}