package com.baufest.tennis.springtennis.controller;

import com.baufest.tennis.springtennis.dto.JugadorDTO;
import com.baufest.tennis.springtennis.service.JugadorServiceImpl;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(JugadorController.class)
class JugadorControllerTest {
    String basePath = "/springtennis/api/v1/jugadores/";

    List<JugadorDTO> jugadoresDePrueba = new ArrayList<>();
    JSONArray jugadoresDePruebaEnJSON = new JSONArray();
    JugadorDTO jugadorParaAgregar = new JugadorDTO();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JugadorController jugadorController;

    @MockBean
    JugadorServiceImpl jugadorService;

    @BeforeEach
    public void setUp() {
    	//Agregamos datos nuevos
    	JugadorDTO jugador1 = new JugadorDTO(1L,"facu",20);
        jugadoresDePrueba.add(jugador1);
        JugadorDTO jugador2 = new JugadorDTO(2L,"fer",15);
        jugadoresDePrueba.add(jugador2);
        JugadorDTO jugador3 = new JugadorDTO(3L,"juli",10);
        jugadoresDePrueba.add(jugador3);
        JugadorDTO jugador4 = new JugadorDTO(4L,"axel",5);
        jugadoresDePrueba.add(jugador4);
    	
        jugadorParaAgregar.setId(5L);
        jugadorParaAgregar.setNombre("lucas");
        jugadorParaAgregar.setPuntos(25);

        jugadoresDePrueba.forEach((x) -> jugadoresDePruebaEnJSON.put(x.toJSONObject()));

    }

    @Test
    void testListAll() throws Exception {

        when(jugadorService.listAll()).thenReturn(jugadoresDePrueba);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jugadoresDePruebaEnJSON.toString()));

        verify(jugadorService).listAll();

    }

    @Test
    void testGetByID() throws Exception {
        long idJugadorGet = 1L;
        when(jugadorService.getById(1L)).thenReturn(jugadoresDePrueba.get(0));

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + idJugadorGet).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jugadoresDePrueba.get(0)
                        .toJSONObject().toString()));
        verify(jugadorService).getById(1L);
    }

    @Test
    void testSaveJugador() throws Exception {
        ArgumentCaptor<JugadorDTO> argumentCaptor = ArgumentCaptor.forClass(JugadorDTO.class);

        when(jugadorService.save(argumentCaptor.capture())).thenReturn(new JugadorDTO());

        mockMvc.perform(MockMvcRequestBuilders.post(basePath).contentType(MediaType.APPLICATION_JSON)
                .content(jugadorParaAgregar.toJSONObject().toString()))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        assertEquals(jugadorParaAgregar.getNombre(),argumentCaptor.getValue().getNombre());
        assertEquals(jugadorParaAgregar.getId(),argumentCaptor.getValue().getId());
        assertEquals(jugadorParaAgregar.getPuntos(),argumentCaptor.getValue().getPuntos());
        verify(jugadorService).save(any(JugadorDTO.class));
    }

    @Test
    void testUpdateJugador() throws Exception {
        ArgumentCaptor<JugadorDTO> argumentCaptor = ArgumentCaptor.forClass(JugadorDTO.class);
        when(jugadorService.update(argumentCaptor.capture())).thenReturn(new JugadorDTO());

        mockMvc.perform(MockMvcRequestBuilders.put(basePath + jugadoresDePrueba.get(0).getId()).contentType(MediaType.APPLICATION_JSON)
                .content(jugadoresDePrueba.get(0).toJSONObject().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(jugadoresDePrueba.get(0).getId(),argumentCaptor.getValue().getId());
        assertEquals(jugadoresDePrueba.get(0).getNombre(),argumentCaptor.getValue().getNombre());
        assertEquals(jugadoresDePrueba.get(0).getPuntos(),argumentCaptor.getValue().getPuntos());
        verify(jugadorService).update(any(JugadorDTO.class));
    }

    @Test
    void testDeleteJugador() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(basePath + jugadoresDePrueba.get(0).getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(jugadorService).delete(eq(jugadoresDePrueba.get(0).getId()));
    }

}
