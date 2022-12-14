package com.baufest.tennis.springtennis.service;

import com.baufest.tennis.springtennis.dto.CanchaDTO;
import com.baufest.tennis.springtennis.dto.JugadorDTO;
import com.baufest.tennis.springtennis.dto.PartidoDTO;
import com.baufest.tennis.springtennis.enums.Estado;
import com.baufest.tennis.springtennis.enums.ModoJugador;
import com.baufest.tennis.springtennis.mapper.PartidoMapper;
import com.baufest.tennis.springtennis.model.Partido;
import com.baufest.tennis.springtennis.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

//Aca va la anotacion @Service para declarar que es un servicio y pueda ser detectado por el autowired
@Service
public class PartidoServiceImpl implements PartidoService {
    //Int que se utilizan para validaciones
    public static final int SCORE_ADV = 4;
    public static final int SCORE_40 = 3;
    public static final int SCORE_30 = 2;
    public static final int SCORE_15 = 1;
    public static final int SCORE_0 = 0;

    //String que se utilizan para las respuestas de los exception handler
    public static final String DOES_NOT_EXIST = " does not exist.";
    public static final String PARTIDO_WITH_ID = "Partido with id = ";
    public static final String ALREADY_EXISTS = " already exists.";
    public static final String NOT_IN_PROGRESS = " is not in progress. ";
    private static final String ALREADY_IN_PROGRESS = " is already in progress or is finished. ";
    public static final String SCORE_IMPOSIBLE = "Score imposible";
    private static final String PLAYER_MISSING = "Se deben agregar ambos jugadores.";
    private static final String PLAYER_DUPLICATED = "Los jugadores agregados deben ser distintos.";

    private static final String STADIUM_MISSING = "Se debe asignar una cancha.";
    private static final String INVALID_DATE = "La fecha/hora de inicio debe ser mayor o igual a la fecha/hora actual.";
    private static final String INVALID_RAGE = "Debe haber una diferencia de al menos 4 horas entre partidos en una misma cancha.";

    //Estas son las variables donde se alojaran las instanciaciones del repository y el mapper al momento
    //de utilizarlas por medio de Autowired, son final para que no se puedan modificar una vez instanciadas
    private final PartidoRepository partidoRepository;
    private final PartidoMapper partidoMapper;

    //Map utilizado para guardar las descripciones y los distintos puntajes
    //Este es un hashmap, eso significa que tiene asociado un key y un valor
    //Por ejemplo el key SCORE_ADV tiene asociado el String Adv
    //El key SCORE_40 tiene asociado el string 40... etc
    private static final Map<Integer, String> descriptions = new HashMap<>();

    //Static utilizado para guardar en el map descriptions los valores de los distintos puntajes
    static {
        descriptions.put(SCORE_ADV, "Adv");
        descriptions.put(SCORE_40, "40");
        descriptions.put(SCORE_30, "30");
        descriptions.put(SCORE_15, "15");
        descriptions.put(SCORE_0, "0");
    }

    /*
    Aca se utiliza la anotacion Autowired, esta anotacion de springboot se encarga de enlazar todos los componentes,
    va ligada a la inyeccion de dependencias, en este caso como se usa por constructor se declara el Spring Prototype
    Autowired arriba del constructor, de esta forma se asegura que al momento de instanciacion del componente, los modulos
    a los cuales se declara y se necesitan usar esten disponibles para su instanciacion, de este modo al momento de ser llamados
    se instancian momentaneamente, se llama a la funcion requerida y se desinstancia, es el concepto de hollywood, IoC inversion
    of control, No nos llames; nosotros te llamaremos
     */
    @Autowired
    public PartidoServiceImpl(PartidoRepository partidoRepository, PartidoMapper partidoMapper) {
        this.partidoRepository = partidoRepository;
        this.partidoMapper = partidoMapper;
    }

    @Override
    public List<PartidoDTO> listAll() {
        /*
         * Se obtiene una collection de partidos (entidad) del repository,
         * se transforma a una collection de partidos (DTO) y luego
         * se lo transforma en un ArrayList para devolverlo al controller*/
        return partidoRepository.findAll()
                .stream()
                .map(this.partidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartidoDTO getById(Long id) {
        /*Llama al repository en el metodo findById, para obtener una entidad partido, la cual la transforma
         * a DTO con el .map() en caso de que el .map arroje conjunto vacio arroja una excepcion de tipo NoSuchElementException*/
        return partidoRepository.findById(id).map(this.partidoMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException(PARTIDO_WITH_ID + id + DOES_NOT_EXIST));
    }

    private void validarJugadores(PartidoDTO partido) {
        /*Este metodo sirve para validar que los jugadores del partido sean correctos,
        * primero carga en dos variables el jugador local y el jugador visitante,
        * luego verifica que sean distinto de null, es decir, existan,
        * luego verifica en dicho if que el id del jugador local sea diferente al del visitante,
        * si es el caso arroja una excepcion del tipo IllegalArgumentException, si sale del if padre
        * significa que el jugador local o el jugador visitante no existen por que son null,
        * en ese caso arroja un IllegalArgumentException*/
        JugadorDTO jugadorLocal = partido.getJugadorLocal();
        JugadorDTO jugadorVisitante = partido.getJugadorVisitante();

        if (jugadorLocal != null && jugadorVisitante != null) {
            if (jugadorLocal.getId().equals(jugadorVisitante.getId())) {
                throw new IllegalArgumentException(PLAYER_DUPLICATED);
            }
        } else {
            throw new IllegalArgumentException(PLAYER_MISSING);
        }
    }
    private void validarCancha(PartidoDTO partido) {
        CanchaDTO canchaDTO = partido.getCancha();
        if (canchaDTO == null) {
            throw new IllegalArgumentException(STADIUM_MISSING);
        }
    }

    private void validarHorario(PartidoDTO dto) {
        Calendar fechaComienzo = Calendar.getInstance();
        fechaComienzo.setTime(dto.getFechaComienzo());
        Long idCancha = dto.getCancha().getId();

        for (Partido partido: partidoRepository.findAll()) {
            Calendar fechaIterador = Calendar.getInstance();
            fechaIterador.setTime(partido.getFechaComienzo());
            if(partido.getCancha().getId().equals(idCancha)){
                if(ChronoUnit.HOURS.between(fechaIterador.toInstant(), fechaComienzo.toInstant())<4){
                    throw  new IllegalArgumentException(INVALID_RAGE);
                }
            }
        }
    }

    private void validarFechaYHora(PartidoDTO partido) {
        /*
        * Este metodo sirve para validar la fecha y la hora del partido que recibe como parametro,
        * en este caso carga en una variable el horario actual, y en un if compara la fecha de comienzo con
        * el horario actual, se fija que no sea el mismo o que no sea inferior a este, caso positivo arroja un
        * IllegalArgumentException*/
        Date now = new Date();
        if (partido.getFechaComienzo().compareTo(now) < 0) {
            throw new IllegalArgumentException(INVALID_DATE);
        }
    }

    private void validarNuevoPartido(PartidoDTO partido) {
        /*
        * En este metodo se valida el partido entrante por parametro,
        * primero se fija que el partido no exista, alojando la respuesta en una variable booleana,
        * se fija que en el partido entrante por parametro haya una Id distinta de null y verifica esa id
        * en el repository por medio del metodo ExistById, si existe arroja una excepcion del tipo IllegalArgumentException
        * luego llamada a los metodos validarJugadores y validarFechaYHora */
        boolean exists = partido.getId() != null && partidoRepository.existsById(partido.getId());
        if (exists) {
            throw new IllegalArgumentException(PARTIDO_WITH_ID + partido.getId() + ALREADY_EXISTS);
        }
        this.validarJugadores(partido);
        this.validarCancha(partido);
        this.validarFechaYHora(partido);
        this.validarHorario(partido);
    }

    @Override
    public PartidoDTO save(PartidoDTO partido) {
        /*
        * En este metodo se guarda el partido recibido por parametro, primero se llama a la funcion
        * validarNuevoPartido, luego se convierte el partidoDTO a entidad partido y se la envia al repository
        * al metodo save*/
        this.validarNuevoPartido(partido);
        return this.partidoMapper.toDTO(partidoRepository.save(this.partidoMapper.fromDTO(partido)));
    }

    private void validarPartidoNoIniciado(Long id) {
        /*
        * En este metodo se valida el partido no iniciado, se obtiene el partido por el id recibido por parametro
        * y se lo aloja en una variable local, luego, en un if verifica que el estado del partido sea distinto a NO_INICIADO,
        * en caso positivo arroja una excepcion del tipo IllegalArgumentException*/
        PartidoDTO partido = this.getById(id);
        if (!Estado.NO_INICIADO.equals(partido.getEstado())) {
            throw new IllegalArgumentException(PARTIDO_WITH_ID + partido.getId() + ALREADY_IN_PROGRESS);
        }
    }

    private void validarPartidoEditado(PartidoDTO partido) {
        /*
        * En este metodo se valida un partido recibido por parametro para poder editarlo,
        * primero se llama al repository buscando un partido que exista con la id de dicho partido,
        * si no existe, arroja una excepcion del tipo NoSuchElementException, caso contrario,
        * llama al metodo validarPartidoNoIniciado, validarJugadores,validarFechaYHora*/
        boolean exists = partidoRepository.existsById(partido.getId());
        if (!exists) {
            throw new NoSuchElementException(PARTIDO_WITH_ID + partido.getId() + DOES_NOT_EXIST);
        }
        this.validarPartidoNoIniciado(partido.getId());
        this.validarJugadores(partido);
        this.validarFechaYHora(partido);
    }

    @Override
    public PartidoDTO update(PartidoDTO partido) {
        /*En este metodo se trata de editar el partido recibido por parametro, primero se llama al metodo
        * validarPartidoEditado, luego, se transforma el DTO del partido recibido por parametro a entidad
        * partido y se lo envia al metodo del repository save*/
        this.validarPartidoEditado(partido);
        return this.partidoMapper.toDTO(partidoRepository.save(this.partidoMapper.fromDTO(partido)));
    }

    private void validarPartidoEliminado(Long id) {
        /*En este metodo se valida el partido a eliminar, se recibe una Id por parametro y luego
        * se verifica que existe llamando al metodo existById del repository mandando como parametro dicha id,
        * en caso de no existir entraria al if que arrojaria una excepcion del tipo NoSuchElementException, luego
        * se llama al metodo validarPartidoNoIniciado*/
        boolean exists = partidoRepository.existsById(id);
        if (!exists) {
            throw new NoSuchElementException(PARTIDO_WITH_ID + id + DOES_NOT_EXIST);
        }
        this.validarPartidoNoIniciado(id);
    }

    @Override
    public void delete(Long id) {
        /*En este metodo se trata de eliminar un partido por el id recibido por parametro,
        * primero se llama a validarPartidoEliminado y luego, se llama al repository metodo
        * deleteById para que lo elimine*/
        this.validarPartidoEliminado(id);
        partidoRepository.deleteById(id);
    }

    private String translateScore(int puntos) {
        /*Este metodo traduce el puntaje recibido por int a un key del hashmap descriptions*/
        return descriptions.get(puntos);
    }

    private void gameLocal(Partido partido) {
        partido.setScoreLocal(0); //Se setean los puntos del score local a 0
        partido.setScoreVisitante(0); //se setean los puntos del score visitante a 0
        partido.setPuntosGameActualLocal(this.translateScore(partido.getScoreLocal())); //Se le cargan puntos al game local actual traducidos
        partido.setPuntosGameActualVisitante(this.translateScore(partido.getScoreVisitante()));//Se le cargan puntos al game visitante actual traducidos

        partido.setCantidadGamesLocal(partido.getCantidadGamesLocal() + 1); //Se le aumenta un game al local (ya que anoto el maximo puntaje)
        if (partido.getCantidadGamesLocal() == 6) { //Si llega a 6 rondas ganadas se finaliza el partido
            partido.setEstado(Estado.FINALIZADO);
        }
    }

    private void gameVisitante(Partido partido) {
        partido.setScoreLocal(0); //Se setean los puntos del score local a 0
        partido.setScoreVisitante(0); //se setean los puntos del score visitante a 0
        partido.setPuntosGameActualLocal(this.translateScore(partido.getScoreLocal())); //Se le cargan puntos al game local actual traducidos
        partido.setPuntosGameActualVisitante(this.translateScore(partido.getScoreVisitante())); //Se le cargan puntos al game visitante actual traducidos

        partido.setCantidadGamesVisitante(partido.getCantidadGamesVisitante() + 1); //Se le aumenta un game al visitante (ya que anoto el maximo puntaje)
        if (partido.getCantidadGamesVisitante() == 6) { //Si llega a 6 rondas ganadas se finaliza el partido
            partido.setEstado(Estado.FINALIZADO);
        }
    }

    @Override
    public void initGame(Long id) {
        /*En este metodo se inicia un partido
        * se obtiene un opcional de partido llamando a findById con el id obtenido por parametro*/
        Optional<Partido> optPartido = partidoRepository.findById(id);
        if (optPartido.isPresent()) { //Se fija que en el opcional de partido haya un partido
            validarPartidoNoIniciado(id); //Se valida que el partido no este iniciado
            Partido partido = optPartido.get(); //Se obtiene el partido dentro de opcional y se lo guarda en una variable
            partido.setEstado(Estado.EN_CURSO); //Se le asigna el estado EN CURSO al partido
            partidoRepository.save(partido); //Se guarda el partido
        } else {
            throw new NoSuchElementException(PARTIDO_WITH_ID + id + DOES_NOT_EXIST); //Si no habia partido presente se arroja excepcion
        }
    }

    @Override
    public PartidoDTO sumarPuntos(Long id, ModoJugador modo) {
        //En esta funcion se le suman puntos al jugador en el id de partido
        Optional<Partido> optPartido = partidoRepository.findById(id); //Se busca un partido en el repository
        if (optPartido.isPresent()) { //Verifica que el opcional el partido este presente
            Partido partido = optPartido.get(); //Se obtiene el partido del opcional y lo guarda en una variable temporal de tipo Partido

            if (!Estado.EN_CURSO.equals(partido.getEstado())) {  //Verifica que el partido este en curso
                //Si el partido no esta en curso arroja una excepcion
                throw new IllegalArgumentException(PARTIDO_WITH_ID + partido.getId() + NOT_IN_PROGRESS);
            }

            if (modo == ModoJugador.LOCAL) { //Si el que sumo puntos es el local
                if (partido.getScoreVisitante() == SCORE_ADV) { //Si el visitante tiene el score maximo
                    if (partido.getScoreLocal() != SCORE_40) { //Si el score es distinto de 40 arroja excepcion
                        throw new IllegalArgumentException(SCORE_IMPOSIBLE);
                    }
                    partido.setScoreVisitante(partido.getScoreVisitante() - 1); //Setea el score del visitante al actual - 1
                } else { //Mismas logicas de arriba pero para el local
                    if (partido.getScoreLocal() == SCORE_ADV && partido.getScoreVisitante() != SCORE_40) {
                        throw new IllegalArgumentException(SCORE_IMPOSIBLE);
                    }
                    partido.setScoreLocal(partido.getScoreLocal() + 1);
                }
            } else { //Mismas logicas de arriba pero para la suma de puntos del visitante
                if (partido.getScoreLocal() == SCORE_ADV) {
                    if (partido.getScoreVisitante() != SCORE_40) {
                        throw new IllegalArgumentException(SCORE_IMPOSIBLE);
                    }
                    partido.setScoreLocal(partido.getScoreLocal() - 1);
                } else {
                    if (partido.getScoreVisitante() == SCORE_ADV && partido.getScoreLocal() != SCORE_40) {
                        throw new IllegalArgumentException(SCORE_IMPOSIBLE);
                    }
                    partido.setScoreVisitante(partido.getScoreVisitante() + 1);
                }
            }
            this.actualizarScore(partido); //se llama al metodo actualizar score
            return this.partidoMapper.toDTO(partidoRepository.save(partido)); //se guarda la entidad en el repository y se transforma el return a dto para response
        } else {
            throw new NoSuchElementException(PARTIDO_WITH_ID + id + DOES_NOT_EXIST);
        }
    }

    private void actualizarScore(Partido partido) {
        partido.setPuntosGameActualLocal(this.translateScore(partido.getScoreLocal())); //Se traduce el puntaje del score local a puntos de game
        partido.setPuntosGameActualVisitante(this.translateScore(partido.getScoreVisitante())); //lo mismo de arriba para visitante

        //Math abs devuelve el valor absoluto, en el if verifica que sea mayor o igual a 2
        if (Math.abs(partido.getScoreLocal() - partido.getScoreVisitante()) >= 2) {
            if (partido.getScoreLocal() > partido.getScoreVisitante() && partido.getScoreLocal() >= SCORE_ADV) { //Si el score del local es mayor al visitante y tiene SCORE_ADV
                this.gameLocal(partido); //se llama al a funcion gameLocal
            } else if (partido.getScoreVisitante() > partido.getScoreLocal() && partido.getScoreVisitante() >= SCORE_ADV) { //Si el score del visitante es mayor al local y tiene SCORE_ADV
                this.gameVisitante(partido); //se llama a la funcion gameVisitante
            }
        }

    }

}
