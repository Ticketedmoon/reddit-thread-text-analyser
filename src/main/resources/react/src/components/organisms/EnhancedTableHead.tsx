import {HeadCell, ResultTableRow, StyledTableCell} from "./ResultTable";
import React from "react";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import {Box, TableSortLabel} from "@mui/material";
import {visuallyHidden} from "@mui/utils";

export type Order = 'asc' | 'desc';

export interface EnhancedTableProps {
    headCells: readonly HeadCell[]
    numSelected: number;
    onRequestSort: (event: React.MouseEvent<unknown>, property: keyof ResultTableRow) => void;
    onSelectAllClick: (event: React.ChangeEvent<HTMLInputElement>) => void;
    order: Order;
    orderBy: string;
    rowCount: number;
}

export const EnhancedTableHead = (props: EnhancedTableProps): JSX.Element => {
    const {order, orderBy, onRequestSort} = props;
    const createSortHandler = (property: keyof ResultTableRow) => (event: React.MouseEvent<unknown>) => {
        onRequestSort(event, property);
    };

    return (
        <TableHead>
            <TableRow>
                {props.headCells.map((headCell) => (
                    <StyledTableCell key={headCell.id}
                                     align={headCell.numeric ? 'right' : 'left'}
                                     padding={headCell.disablePadding ? 'none' : 'normal'}
                                     sortDirection={orderBy === headCell.id ? order : false}>
                        <TableSortLabel active={orderBy === headCell.id}
                                        direction={orderBy === headCell.id ? order : 'asc'}
                                        onClick={createSortHandler(headCell.id)}>
                            {headCell.label}
                            {orderBy === headCell.id ? (
                                <Box component="span" sx={visuallyHidden}>
                                    {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                                </Box>
                            ) : null}
                        </TableSortLabel>
                    </StyledTableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}
