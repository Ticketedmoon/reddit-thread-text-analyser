import React from "react";
import {HeadCell, ResultTable, ResultTableRow} from "../organisms/ResultTable";
import {Box, Divider} from "@mui/material";
import {ThreadTextType} from "../types/ThreadTextType";
import {JobMetadata} from "../types/JobMetadata";
import {useLoaderData} from "react-router-dom";

type ResultMap = {
    [type: string]: ResultTableRow[]
}

interface ResultSummary {
    job_execution_metadata: JobMetadata,
    results: ResultMap
}

const headCells: readonly HeadCell[] = [
    {
        id: 'id',
        numeric: false,
        disablePadding: true,
        label: 'ID',
    },
    {
        id: 'type',
        numeric: false,
        disablePadding: false,
        label: 'Type',
    },
    {
        id: 'textItem',
        numeric: false,
        disablePadding: false,
        label: 'Text',
    },
    {
        id: 'count',
        numeric: true,
        disablePadding: false,
        label: 'Count',
    }
];

export const JobResultViewPage: React.FC = () => {

    const state: ResultSummary | null = useLoaderData() as ResultSummary | null;

    return (
        <>
            { state == null ? null : (
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
                            <ResultTable
                                headCells={headCells.filter(item => item.id != "id")}
                                rows={state.results[ThreadTextType.OVERALL]}/>
                        </Box>

                        <Box width={0.6}>
                            <h3> Results (Posts) </h3>
                            <ResultTable headCells={headCells}
                                         rows={state.results[ThreadTextType.POST]}/>
                        </Box>

                        <Box width={0.6}>
                            <h3> Results (Comments) </h3>
                            <ResultTable headCells={headCells}
                                         rows={state.results[ThreadTextType.COMMENT]}/>
                        </Box>

                        <Box width={0.6}>
                            <h3> Results (Replies) </h3>
                            <ResultTable headCells={headCells}
                                         rows={state.results[ThreadTextType.REPLY]}/>
                        </Box>
                    </Box>
                </div>
            )}
        </>
    )
}