package com.baufest.tennis.springtennis.repository;

import com.baufest.tennis.springtennis.model.Cancha;
import com.baufest.tennis.springtennis.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

//Aca va la anotacion Repository para indicar que es un repositorio y pueda ser detectado por el autowired
@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    List<Partido> findAllByCanchaAndFechaComienzoBetween(Cancha cancha, Date fechaInicio, Date fechaFin);
}
