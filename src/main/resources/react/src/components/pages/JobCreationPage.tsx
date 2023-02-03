import React, {ChangeEvent, useState} from "react";
import {Box, Button, Divider, Slider, TextField, Typography} from "@mui/material";
import UploadIcon from '@mui/icons-material/Upload';
import axios from "axios";

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

    const [subRedditName, setRedditName] = useState<string>("");
    const [totalPostsToScanForSubReddit, setTotalPostsToScanForSubReddit] = useState<number>(25);
    const [isFormError, setFormError] = useState<boolean>(false);

    const startJob = () => {
        console.log(subRedditName)
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
                // Loading icon for jobs in-progress
                console.log("success");
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

    function handleSubRedditNameChange(e: ChangeEvent<HTMLInputElement>) {
        if (isFormError) {
            setFormError(false);
        }
        setRedditName(e.target.value);
    }

    return (
        <Box>
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
                        <Typography variant="caption">
                            Morbi porttitor mi sit amet risus lobortis dignissim mollis at nisl.
                            Quisque vel volutpat felis. Fusce finibus malesuada mi, id aliquam diam
                            malesuada sodales. Donec ac velit et libero rhoncus fringilla eget aliquet tortor.
                            Donec vehicula nisl nunc, in bibendum arcu fermentum in. Vivamus porttitor cursus quam,
                            ac fringilla nisi ultricies quis. Aenean et felis mollis, commodo lorem vel, efficitur enim.
                            Donec posuere nibh nisl, sit amet consequat mauris iaculis id. Morbi non ultricies lacus.
                            In ultrices pharetra libero, eu lacinia ex. Morbi sodales lorem eu enim volutpat dapibus.
                            Suspendisse rhoncus, ipsum suscipit accumsan ultricies, orci metus sagittis velit, eu
                            interdum metus urna in nunc. Nunc eu accumsan justo. Phasellus pellentesque, erat vitae
                            ornare pulvinar, ante ipsum pellentesque orci, et lobortis enim nulla pretium orci.
                            Mauris rutrum mauris tellus, et varius augue finibus ac. Aliquam ipsum tortor, viverra
                            quis posuere iaculis, fermentum quis quam.
                        </Typography>
                    </Box>
                    <Box display="flex" flexDirection="column">
                        <Typography sx={{
                            fontFamily: "MyItimFont, arial, sans-serif",
                        }}>
                            Developer: Shane Creedon <br/>
                            Email: shane.creedon3@mail.dcu.ie <br/>
                            Github: XXX <br/>
                            Project Name: process.env.REACT_APP_NAME <br/>
                            Version: process.env.REACT_APP_VERSION <br/>
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
                                Start Analysis Job
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Box>
        </Box>
    )
}