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

    const [subRedditName, setRedditName] = useState<string | null>(null);
    const [totalPostsToScanForSubReddit, setTotalPostsToScanForSubReddit] = useState<number>(25);

    const startJob = () => {
        if (subRedditName === null) {
            console.error("Failed to provide subreddit name, please enter a value");
            // TODO Snackbar instead of ^
        }
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

    const handleTotalPostsToScanSliderChange = (_: Event, value: number) => {
        const totalPostsToScanForSubReddit = Array.isArray(value) ? value[0] : value;
        setTotalPostsToScanForSubReddit(totalPostsToScanForSubReddit);
    }

    function handleSubRedditNameChange(e: ChangeEvent<HTMLInputElement>) {
        setRedditName(e.target.value);
    }

    return (
        <Box>
            <Box display="flex" justifyContent="center" pt={10}>
                <Box>
                    <Typography variant="h5">
                        Start a Subreddit Analysis
                    </Typography>
                </Box>
            </Box>

            <Box display="grid" gridTemplateColumns="3fr 1fr 3fr" gridAutoRows="auto" mt={3}>
                <Box display="flex"
                     justifyContent="center"
                     alignSelf="center">
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
                <Box display="flex"
                     justifyContent="center">
                    <Divider orientation={"vertical"}
                             flexItem
                             sx={{
                                 height: '500px',
                                 borderWidth: '1px'
                             }}/>
                </Box>
                <Box display="flex"
                     justifyContent="center"
                     flexDirection="column"
                     gap={3}
                     alignSelf="center">
                    <Box>
                        <TextField label="Subreddit Name"
                                   color="primary"
                                   focused
                                   onChange={handleSubRedditNameChange}
                                   sx={{
                                       width: 300
                                   }}/>
                    </Box>
                    <Box sx={{width: 400}}>
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
                                endIcon={<UploadIcon/>}
                                onClick={() => startJob()}>
                            Start Analysis Job
                        </Button>
                    </Box>
                </Box>
            </Box>
        </Box>
    )
}