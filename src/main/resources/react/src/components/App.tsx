import React, {useEffect, useState} from "react";
import axios from "axios";
import {ResultTable, ResultTableRow} from "./organisms/ResultTable";
import {RedditTextType} from "./types/RedditTextType";
import {Box, Divider} from "@mui/material";

interface ResultMetadata {
    id: string,
    subreddit: string,
    jobStartTime: string,
    jobFinishTime: string,
    totalPostsToScan: number
}

interface ResultSummary {
    job_execution_metadata: ResultMetadata,
    results: ResultTableRow[]
}

export const App = () => {

    const [state, setState] = useState<ResultSummary | null>(null);

    useEffect(() => {
        const testJobId: string = "4f68b1e0-7240-463d-89f6-8023c80fdd43";
        axios.get(`/api/job-reports/${testJobId}/results`, {})
            .then(res => {
                setState(res.data)
            })
            .catch(err => {
                console.error(err);
            })
    }, [])

    return (
        <>
            {state == null ? null : (
                <div>
                    <Box display="flex"
                         alignItems="center"
                         flexDirection="column">

                        {/* TODO: Replace with <typography> and new fonts */}
                        <h1>Job Data</h1>
                        <div>
                            <h3> Summary Info </h3>
                            <p> Job ID: {state.job_execution_metadata.id} </p>
                            <p> Subreddit: {state.job_execution_metadata.subreddit} </p>
                            <p> Total Reddit Posts Scanned: {state.job_execution_metadata.totalPostsToScan} </p>
                            <p> Job Start Time: {state.job_execution_metadata.jobStartTime} </p>
                            <p> Job Finish Time: {state.job_execution_metadata.jobFinishTime} </p>
                        </div>
                    </Box>

                    <Divider variant="middle" />

                    <Box display="flex"
                         pt={5}
                         alignItems="center"
                         width={1}
                         flexDirection="column">
                        <Box width={0.6}>
                            <h3> Overall Results </h3>
                            <ResultTable rows={state.results}/>
                        </Box>

                        {/* TODO have this separation returned from the backend */}

                        <Box width={0.6}>
                            <h3> Results (Posts) </h3>
                            <ResultTable rows={state.results.filter(res => res.type === RedditTextType.POST)}/>
                        </Box>


                        <Box width={0.6}>
                            <h3> Results (Comments) </h3>
                            <ResultTable rows={state.results.filter(res => res.type === RedditTextType.COMMENT)}/>
                        </Box>

                        {/* TODO Investigate why there are no replies */}
                        <Box width={0.6}>
                            <h3> Results (Replies) </h3>
                            <ResultTable rows={state.results.filter(res => res.type === RedditTextType.REPLY)}/>
                        </Box>
                    </Box>
                </div>
            )}
        </>
    );
}