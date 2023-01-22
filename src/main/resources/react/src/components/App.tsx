import React, {useEffect, useState} from "react";
import axios from "axios";

interface ResultMetadata {
    id: string,
    subreddit: string,
    jobStartTime: string,
    jobFinishTime: string,
    totalPostsToScan: number
}

interface ResultItem {
    count: number,
    id: string,
    jobId: string,
    textItem: string,
    type: string
}

interface ResultSummary {
    job_execution_metadata: ResultMetadata,
    results: ResultItem[]
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
            <h1>Job Data</h1>
            { state == null ? null : (
                <div>
                    <div>
                        <h3> Summary Info </h3>
                        <p> Job ID: {state.job_execution_metadata.id} </p>
                        <p> Subreddit: {state.job_execution_metadata.subreddit} </p>
                        <p> Total Reddit Posts Scanned: {state.job_execution_metadata.totalPostsToScan} </p>
                        <p> Job Start Time: {state.job_execution_metadata.jobStartTime} </p>
                        <p> Job Finish Time: {state.job_execution_metadata.jobFinishTime} </p>
                    </div>
                    <div>
                        <h3> Results </h3>
                        { state.results.map(result => {
                            return <div> | ID: {result.id} | Type: {result.type} | Word: {result.textItem} | Count: {result.count} | </div>
                        }) }
                    </div>
                </div>
            )}
        </>
    );
}