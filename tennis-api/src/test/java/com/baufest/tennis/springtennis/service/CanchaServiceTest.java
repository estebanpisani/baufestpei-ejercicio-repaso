package com.baufest.tennis.springtennis.service;

import com.baufest.tennis.springtennis.dto.CanchaDTO;
import com.baufest.tennis.springtennis.mapper.CanchaMapperImpl;
import com.baufest.tennis.springtennis.model.Cancha;
import com.baufest.tennis.springtennis.repository.CanchaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CanchaServiceTest {
    private final List<Cancha> canchasDePrueba = new ArrayList<>();
    private final List<CanchaDTO> canchasDTODePrueba = new ArrayList<>();
    private final CanchaDTO canchaDTOParaAgregar = new CanchaDTO();
    private final Cancha canchaParaAgregar = new Cancha();

    CanchaServiceImpl canchaService;

    @Mock
    CanchaRepository canchaRepository;

    @BeforeEach
    public void setUp() {
        
        canchasDTODePrueba.clear();
    	
    	//Agregamos datos nuevos
        CanchaDTO canchaDTO1 = new CanchaDTO(1L,"Estadio Velez Sarfield","Av. Velez Sarfield 123");
        canchasDTODePrueba.add(canchaDTO1);
        CanchaDTO canchaDTO2 = new CanchaDTO(2L,"Estadio Monumental","Av. Rio de la Plata 521");
        canchasDTODePrueba.add(canchaDTO2);
        CanchaDTO canchaDTO3 = new CanchaDTO(3L,"Estadio Malvinas Argentinas","Av. Malvinas Argentinas 302");
        canchasDTODePrueba.add(canchaDTO3);
        CanchaDTO canchaDTO4 = new CanchaDTO(4L,"Estadio Argentino de Tennis","Av. Nadal 462");
        canchasDTODePrueba.add(canchaDTO4);

        Cancha cancha1 = new Cancha(1L,"Estadio Velez Sarfield","Av. Velez Sarfield 123");
        canchasDePrueba.add(cancha1);
        Cancha cancha2 = new Cancha(2L,"Estadio Monumental","Av. Rio de la Plata 521");
        canchasDePrueba.add(cancha2);
        Cancha cancha3 = new Cancha(3L,"Estadio Malvinas Argentinas","Av. Malvinas Argentinas 302");
        canchasDePrueba.add(cancha3);
        Cancha cancha4 = new Cancha(4L,"Estadio Argentino de Tennis","Av. Nadal 462");
        canchasDePrueba.add(cancha4);

        //seteamos datos de cancha a agregar
		canchaDTOParaAgregar.setId(5L);
        canchaDTOParaAgregar.setNombre("Movistar Arena");
        canchaDTOParaAgregar.setDireccion("Av Manuel Belgrano 564");

        canchaParaAgregar.setId(5L);
        canchaParaAgregar.setNombre("Movistar Arena");
        canchaParaAgregar.setDireccion("Av Manuel Belgrano 564");

        canchaService = new CanchaServiceImpl(canchaRepository, new CanchaMapperImpl());
    }

    @Test
    void testListCanchas() {
        when(canchaRepository.findAll()).thenReturn(canchasDePrueba);
        List<CanchaDTO> canchasConseguidos = canchaService.listAll();
        assertEquals(canchasDTODePrueba.size(),canchasConseguidos.size());
        verify(canchaRepository,times(1)).findAll();
    }

    @Test
    void testGetCanchaByID() {
        when(canchaRepository.findById(canchasDTODePrueba.get(0).getId()))
                .thenReturn(Optional.ofNullable(canchasDePrueba.get(0)));
        CanchaDTO canchaEncontrada = canchaService.getById(canchasDTODePrueba.get(0).getId());
        assertEquals(canchasDTODePrueba.get(0).getId(),canchaEncontrada.getId());
        verify(canchaRepository).findById(eq(canchasDTODePrueba.get(0).getId()));
    }

    @Test
    void testSaveOrUpdate() {
        ArgumentCaptor<Cancha> argumentCaptor = ArgumentCaptor.forClass(Cancha.class);
        when(canchaRepository.save(argumentCaptor.capture())).thenReturn(canchaParaAgregar);
        CanchaDTO canchaDTO = canchaService.save(canchaDTOParaAgregar);
        assertEquals(canchaDTOParaAgregar.getId(),argumentCaptor.getValue().getId());
        assertEquals(canchaParaAgregar.getId(), canchaDTO.getId());
        assertEquals(canchaParaAgregar.getNombre(), canchaDTO.getNombre());
        verify(canchaRepository).save(any(Cancha.class));
    }

    @Test
    void testDelete() {
        Long idParaBorrar = 1L;
        when(canchaRepository.existsById(idParaBorrar)).thenReturn(true);

        canchaService.delete(idParaBorrar);

        verify(canchaRepository).existsById(eq(idParaBorrar));
        verify(canchaRepository).deleteById(eq(idParaBorrar));
    }

    @Test
    void testDeleteNotFound() {
        Long idParaBorrar = 1L;
        when(canchaRepository.existsById(idParaBorrar)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> canchaService.delete(idParaBorrar));
        verify(canchaRepository).existsById(eq(idParaBorrar));
        verify(canchaRepository,times(0)).deleteById(any());
    }

    @Test
    void testInsertExistent() {
        when(canchaRepository.existsById(canchaDTOParaAgregar.getId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () ->  canchaService.save(canchaDTOParaAgregar));
        verify(canchaRepository).existsById(eq(canchaDTOParaAgregar.getId()));
    }

    @Test
    void testUpdateExisting() {
        ArgumentCaptor<Cancha> argumentCaptor = ArgumentCaptor.forClass(Cancha.class);
        when(canchaRepository.existsById(canchaDTOParaAgregar.getId())).thenReturn(true);
        when(canchaRepository.save(argumentCaptor.capture())).thenReturn(canchaParaAgregar);
        CanchaDTO canchaDTO = canchaService.update(canchaDTOParaAgregar);
        assertEquals(canchaDTOParaAgregar.getId(),argumentCaptor.getValue().getId());
        assertEquals(canchaParaAgregar.getId(), canchaDTO.getId());
        assertEquals(canchaParaAgregar.getNombre(), canchaDTO.getNombre());
        verify(canchaRepository,times(1)).save(any(Cancha.class));
        verify(canchaRepository).existsById(eq(canchaDTOParaAgregar.getId())); // times por defecto va 1
    }

    @Test
    void testUpdateNotFound() {
        when(canchaRepository.existsById(canchaDTOParaAgregar.getId())).thenReturn(false);
        assertThrows(NoSuchElementException.class, () ->  canchaService.update(canchaDTOParaAgregar));
        verify(canchaRepository,times(1)).existsById(eq((canchaDTOParaAgregar.getId())));
        verify(canchaRepository,times(0)).save(any());
    }


}