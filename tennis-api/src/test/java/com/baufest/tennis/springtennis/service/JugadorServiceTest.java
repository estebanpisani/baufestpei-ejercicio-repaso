package com.baufest.tennis.springtennis.service;

import com.baufest.tennis.springtennis.dto.JugadorDTO;
import com.baufest.tennis.springtennis.mapper.JugadorMapperImpl;
import com.baufest.tennis.springtennis.model.Jugador;
import com.baufest.tennis.springtennis.repository.JugadorRepository;
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
class JugadorServiceTest {
    private final List<Jugador> jugadoresDePrueba = new ArrayList<>();
    private final List<JugadorDTO> jugadoresDTODePrueba = new ArrayList<>();
    private final JugadorDTO jugadorDTOParaAgregar = new JugadorDTO();
    private final Jugador jugadorParaAgregar = new Jugador();

    JugadorServiceImpl jugadorService;

    @Mock
    JugadorRepository jugadorRepository;

    @BeforeEach
    public void setUp() {
        
        jugadoresDTODePrueba.clear();
    	
    	//Agregamos datos nuevos
        JugadorDTO jugadorDTO1 = new JugadorDTO(1L,"facu",20);
        jugadoresDTODePrueba.add(jugadorDTO1);
        JugadorDTO jugadorDTO2 = new JugadorDTO(2L,"fer",15);
        jugadoresDTODePrueba.add(jugadorDTO2);
        JugadorDTO jugadorDTO3 = new JugadorDTO(3L,"juli",10);
        jugadoresDTODePrueba.add(jugadorDTO3);
        JugadorDTO jugadorDTO4 = new JugadorDTO(4L,"axel",5);
        jugadoresDTODePrueba.add(jugadorDTO4);

        Jugador jugador1 = new Jugador(1L,"facu",20);
        jugadoresDePrueba.add(jugador1);
        Jugador jugador2 = new Jugador(2L,"fer",15);
        jugadoresDePrueba.add(jugador2);
        Jugador jugador3 = new Jugador(3L,"juli",10);
        jugadoresDePrueba.add(jugador3);
        Jugador jugador4 = new Jugador(4L,"axel",5);
        jugadoresDePrueba.add(jugador4);

        //seteamos datos de jugador a agregar
		jugadorDTOParaAgregar.setId(5L);
        jugadorDTOParaAgregar.setNombre("lucas");
        jugadorDTOParaAgregar.setPuntos(25);

        jugadorParaAgregar.setId(5L);
        jugadorParaAgregar.setNombre("lucas");
        jugadorParaAgregar.setPuntos(25);

        jugadorService = new JugadorServiceImpl(jugadorRepository,new JugadorMapperImpl());
    }

    @Test
    void testListJugadores() {
        when(jugadorRepository.findAllByOrderByNombreAsc()).thenReturn(jugadoresDePrueba);
        List<JugadorDTO> jugadoresConseguidos = jugadorService.listAll();
        assertEquals(jugadoresDTODePrueba.size(),jugadoresConseguidos.size());
        verify(jugadorRepository,times(1)).findAllByOrderByNombreAsc();
    }

    @Test
    void testGetJugadorByID() {
        when(jugadorRepository.findById(jugadoresDTODePrueba.get(0).getId()))
                .thenReturn(Optional.ofNullable(jugadoresDePrueba.get(0)));
        JugadorDTO jugadorEncontrado = jugadorService.getById(jugadoresDTODePrueba.get(0).getId());
        assertEquals(jugadoresDTODePrueba.get(0).getId(),jugadorEncontrado.getId());
        verify(jugadorRepository).findById(eq(jugadoresDTODePrueba.get(0).getId()));
    }

    @Test
    void testSaveOrUpdate() {
        ArgumentCaptor<Jugador> argumentCaptor = ArgumentCaptor.forClass(Jugador.class);
        when(jugadorRepository.save(argumentCaptor.capture())).thenReturn(jugadorParaAgregar);
        JugadorDTO jugadorDTO = jugadorService.save(jugadorDTOParaAgregar);
        assertEquals(jugadorDTOParaAgregar.getId(),argumentCaptor.getValue().getId());
        assertEquals(jugadorParaAgregar.getId(), jugadorDTO.getId());
        assertEquals(jugadorParaAgregar.getNombre(), jugadorDTO.getNombre());
        verify(jugadorRepository).save(any(Jugador.class));
    }

    @Test
    void testDelete() {
        Long idParaBorrar = 1L;
        when(jugadorRepository.existsById(idParaBorrar)).thenReturn(true);

        jugadorService.delete(idParaBorrar);

        verify(jugadorRepository).existsById(eq(idParaBorrar));
        verify(jugadorRepository).deleteById(eq(idParaBorrar));
    }

    @Test
    void testDeleteNotFound() {
        Long idParaBorrar = 1L;
        when(jugadorRepository.existsById(idParaBorrar)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> jugadorService.delete(idParaBorrar));
        verify(jugadorRepository).existsById(eq(idParaBorrar));
        verify(jugadorRepository,times(0)).deleteById(any());
    }

    @Test
    void testInsertExistent() {
        when(jugadorRepository.existsById(jugadorDTOParaAgregar.getId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () ->  jugadorService.save(jugadorDTOParaAgregar));
        verify(jugadorRepository).existsById(eq(jugadorDTOParaAgregar.getId()));
    }

    @Test
    void testUpdateExisting() {
        ArgumentCaptor<Jugador> argumentCaptor = ArgumentCaptor.forClass(Jugador.class);
        when(jugadorRepository.existsById(jugadorDTOParaAgregar.getId())).thenReturn(true);
        when(jugadorRepository.save(argumentCaptor.capture())).thenReturn(jugadorParaAgregar);
        JugadorDTO jugadorDTO = jugadorService.update(jugadorDTOParaAgregar);
        assertEquals(jugadorDTOParaAgregar.getId(),argumentCaptor.getValue().getId());
        assertEquals(jugadorParaAgregar.getId(), jugadorDTO.getId());
        assertEquals(jugadorParaAgregar.getNombre(), jugadorDTO.getNombre());
        verify(jugadorRepository,times(1)).save(any(Jugador.class));
        verify(jugadorRepository).existsById(eq(jugadorDTOParaAgregar.getId())); // times por defecto va 1
    }

    @Test
    void testUpdateNotFound() {
        when(jugadorRepository.existsById(jugadorDTOParaAgregar.getId())).thenReturn(false);
        assertThrows(NoSuchElementException.class, () ->  jugadorService.update(jugadorDTOParaAgregar));
        verify(jugadorRepository,times(1)).existsById(eq((jugadorDTOParaAgregar.getId())));
        verify(jugadorRepository,times(0)).save(any());
    }


}