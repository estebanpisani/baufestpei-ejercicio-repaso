import React, { useEffect, useState } from 'react';
import Typography from '../../components/Typography/Typography';
import { Button } from 'react-bootstrap';
import TableCanchas from '../../components/Tables/TableCanchas';
import CanchaModal from '../../components/Modals/CanchaModal';
import httpClient from '../../lib/httpClient';

let canchaInit = {
    nombre: '',
    puntos: 0,
};

const Cancha = (props) => {
    const [canchasList, setCanchasList] = useState([]);
    const [canchaData, setCanchaData] = useState(canchaInit);
    const [isEdit, setIsEdit] = useState(false);
    const [hasErrorInForm, setHasErrorInForm] = useState(false);
    const [openModal, setOpenModal] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(async () => {
        await getCanchas();
    }, []);

    //Verbos
    const getCanchas = async () => {
        try {
            const data = await httpClient.get('/canchas');
            setCanchasList(data);
        } catch (error) {
            console.log(error);
        }
    };

    const agregarCancha = async () => {
        try {
            const data = await httpClient.post(`/canchas`, { data: canchaData });
            setCanchasList([...canchasList, data]);
        } catch (error) {
            console.log(error);
        }
        handleCloseModal();
    };

    const editarCancha = async (id) => {
        try {
            const data = await httpClient.put(`/canchas/${id}`, { data: canchaData });
            setCanchasList(canchasList.map((item) => (item.id === id ? data : item)));
        } catch (error) {
            console.log(error);
        }
        handleCloseModal();
    };

    const borrarCancha = async (id) => {
        try {
            await httpClient.delete(`/canchas/${id}`);
            setCanchasList(canchasList.filter((cancha) => cancha.id !== id));
        } catch (error) {
            console.log(error);
        }
    };

    // Buttons
    const handleDetail = (data, event) => {
        event.preventDefault();
        props.history.push(`/cancha/detalle/${data.id}`, { data });
    };

    const handleEdit = (editData, event) => {
        event.preventDefault();
        handleOpenModal(true, editData);
    };
    const handleDelete = async (id, event) => {
        event.preventDefault();
        if (window.confirm('Estas seguro?')) {
            await borrarCancha(id);
        }
    };

    // Modal
    const handleOpenModal = (editarCancha = false, canchaToEdit = null) => {
        setIsEdit(editarCancha);

        if (editarCancha) {
            setCanchaData(canchaToEdit);
        }

        setOpenModal(true);
    };

    const handleCloseModal = () => {
        setOpenModal(false);
        setIsEdit(false);
        setHasErrorInForm(false);
        setCanchaData(canchaInit);
        setErrorMsg('');
    };

    // Form
    const handleChangeInputForm = (property, value) => {
        // Si el valor del input es vacío, entonces setea que hay un error
        value === '' ? setHasErrorInForm(true) : setHasErrorInForm(false);

        setCanchaData({ ...canchaData, [property]: value });
    };

    const handleSubmitForm = (e, form, isEdit) => {
        e.preventDefault();
        setHasErrorInForm(true);

        if (form.checkValidity()) isEdit ? editarCancha(canchaData.id) : agregarCancha();
    };

    // API

    return (
        <>
            <Typography id={'title-id'}>Cancha</Typography>
            <div className="mb-2">
                <Button variant="success" onClick={() => handleOpenModal()}>Agregar cancha</Button>
            </div>

            <TableCanchas
                dataForTable={canchasList}
                detail={handleDetail}
                edit={handleEdit}
                delete={(id, event) => handleDelete(id, event)}
            />
            <CanchaModal
                show={openModal}
                onHide={handleCloseModal}
                isEdit={isEdit}
                handleChange={handleChangeInputForm}
                cancha={canchaData}
                validated={hasErrorInForm}
                handleSubmit={handleSubmitForm}
                errorMsg={errorMsg}
            />
        </>
    );
};

export default Cancha;
