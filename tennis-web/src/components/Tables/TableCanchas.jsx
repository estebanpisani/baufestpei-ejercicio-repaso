import React from 'react';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';

const TableBody = (data) => {
    return data.dataForTable.map(itemRow => {
        return (
            <tr key={`tr-${itemRow.id}`}>
                <td>{itemRow.id}</td>
                <td>{itemRow.nombre}</td>
                <td>{itemRow.direccion}</td>
                <td>
                    <Button onClick={event => data.edit(itemRow, event)} variant="primary">Editar</Button>{' '}
                    <Button onClick={event => data.delete(itemRow.id, event)} variant="danger">Eliminar</Button>
                </td>
            </tr>
        )
    })
}

const TableCanchas = (props) => {
    return (
        <Table striped bordered hover responsive="sm">
            <thead>
            <tr>
                <th>Id</th>
                <th>Nombre</th>
                <th>Dirección</th>
            </tr>
            </thead>
            <tbody>
            {TableBody(props)}
            </tbody>
        </Table>
    )
}

export default TableCanchas;
