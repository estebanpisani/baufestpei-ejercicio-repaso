package com.baufest.tennis.springtennis.service;

import com.baufest.tennis.springtennis.dto.CanchaDTO;

import java.util.List;

/**
 * <p>Service de Cancha</p>
 * Este componente sera el encargado de aplicar la logica de negocio a los jugadores antes de persistirlos en la base de datos
 * o de devolver dichas entradas desde la base de datos, es necesario que contenga el spring prototype @Service para su funcionamiento
 * ya que es la forma de declarar al momento de la inyeccion de dependencias que se trata de un service
 */
public interface CanchaService {

	List<CanchaDTO> listAll();

	CanchaDTO getById(Long id);

	CanchaDTO save(CanchaDTO jugador);

	CanchaDTO update(CanchaDTO jugador);

	void delete(Long id);

}
