import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import {RedditTextType} from "../types/RedditTextType";
import {styled, tableCellClasses} from "@mui/material";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: theme.palette.common.black,
        color: theme.palette.common.white,
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 14
    },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
    '&:nth-of-type(odd)': {
        backgroundColor: theme.palette.action.hover,
    },
    // hide last border
    '&:last-child td, &:last-child th': {
        border: 0,
    },
}));

export interface ResultTableRow {
    id: string,
    jobId: string,
    type: RedditTextType,
    textItem: RedditTextType,
    count: number
}

export const ResultTable = (props: {rows: ResultTableRow[]}): JSX.Element => {

    return (
        <TableContainer component={Paper}>
            <Table sx={{ width: 0.5 }}
                   size="small"
                   aria-label="result table">
                <TableHead>
                    <TableRow>
                        <StyledTableCell>ID</StyledTableCell>
                        <StyledTableCell>Type</StyledTableCell>
                        <StyledTableCell>Text</StyledTableCell>
                        <StyledTableCell>Count</StyledTableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    {props.rows.map((row: ResultTableRow) => (
                        <StyledTableRow key={row.id}>
                            <StyledTableCell component="th" scope="row">
                                {row.id}
                            </StyledTableCell>

                            <StyledTableCell>{row.type}</StyledTableCell>
                            <StyledTableCell>{row.textItem}</StyledTableCell>
                            <StyledTableCell>{row.count}</StyledTableCell>
                        </StyledTableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}