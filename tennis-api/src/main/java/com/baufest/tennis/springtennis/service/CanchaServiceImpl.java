package com.baufest.tennis.springtennis.service;

import com.baufest.tennis.springtennis.dto.CanchaDTO;
import com.baufest.tennis.springtennis.mapper.CanchaMapper;
import com.baufest.tennis.springtennis.repository.CanchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

//Aca va la anotacion @Service para declarar que es un servicio y pueda ser detectado por el autowired
@Service
public class CanchaServiceImpl implements CanchaService {

    //String que se utilizan para las respuestas de los exception handler
    public static final String CANCHA_WITH_ID = "Player with id = ";
    public static final String DOES_NOT_EXIST = " does not exist.";
    public static final String ALREADY_EXISTS = " already exists.";

    //Estas son las variables donde se alojaran las instanciaciones del repository y el mapper al momento
    //de utilizarlas por medio de Autowired, son final para que no se puedan modificar una vez instanciadas
    private final CanchaRepository canchaRepository;
    private final CanchaMapper canchaMapper;

    /*
    Aca se utiliza la anotacion Autowired, esta anotacion de springboot se encarga de enlazar todos los componentes,
    va ligada a la inyeccion de dependencias, en este caso como se usa por constructor se declara el Spring Prototype
    Autowired arriba del constructor, de esta forma se asegura que al momento de instanciacion del componente, los modulos
    a los cuales se declara y se necesitan usar esten disponibles para su instanciacion, de este modo al momento de ser llamados
    se instancian momentaneamente, se llama a la funcion requerida y se desinstancia, es el concepto de hollywood, IoC inversion
    of control, No nos llames; nosotros te llamaremos
     */
    @Autowired
    public CanchaServiceImpl(CanchaRepository canchaRepository,
                             CanchaMapper canchaMapper) {
        this.canchaRepository = canchaRepository;
        this.canchaMapper = canchaMapper;
    }


    @Override
    public List<CanchaDTO> listAll() {
        return canchaRepository.findAll().stream()
                .map(this.canchaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CanchaDTO getById(Long id) {
        return canchaRepository.findById(id).map(this.canchaMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException(CANCHA_WITH_ID + id + DOES_NOT_EXIST));
    }


    @Override
    public CanchaDTO save(CanchaDTO cancha) {
        /*
         * instancia un booleano llamado exist, el cual lo iguala a la respuesta de getById del DTO entrante por parametro
         * get id, si es distinto de null y ademas el repository en metodo existById devuelve un ID se carga con True caso contrario false*/
        boolean exists = cancha.getId() != null && canchaRepository.existsById(cancha.getId());
        if (exists) { //Si existe arroja una nueva excepcion del tipo IllegalArgumentException
            throw new IllegalArgumentException(CANCHA_WITH_ID + cancha.getId() + ALREADY_EXISTS);
        }
        //En caso de que la verificacion anterior no suceda, continua el flujo y guarda la entidad con el save
        //Tambien devuelve el cancha guardado ya que el repository lo devuelve....
        return this.canchaMapper.toDTO(canchaRepository.save(this.canchaMapper.fromDTO(cancha)));
    }

    @Override
    public CanchaDTO update(CanchaDTO cancha) {
        boolean exists = canchaRepository.existsById(cancha.getId());
        if (!exists) {
            throw new NoSuchElementException(CANCHA_WITH_ID + cancha.getId() + DOES_NOT_EXIST);
        }
        return this.canchaMapper.toDTO(canchaRepository.save(this.canchaMapper.fromDTO(cancha)));
    }


    @Override
    public void delete(Long id) {
        boolean exists = canchaRepository.existsById(id);
        if (!exists) {
            throw new NoSuchElementException(CANCHA_WITH_ID + id + DOES_NOT_EXIST);
        }
        canchaRepository.deleteById(id);
    }

}