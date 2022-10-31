package com.baufest.tennis.springtennis.controller;

import com.baufest.tennis.springtennis.dto.CanchaDTO;
import com.baufest.tennis.springtennis.service.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>Controller de Cancha</p>
 * Aca se exponen los metodos que seran accesibles via HttpRequest por medio de los path declarados,
 * estos seran accesibles por dichas llamadas por medio de fetch (front), RestTemplate(Otras apis)
 * o postman
 */
@RestController //Se indica por medio de Springboot Annotation que es un RestController
@CrossOrigin //Esta anotacion permite llamadas desde cualquier origen, si no estuviera este deberia ser indicado en property
@RequestMapping("springtennis/api/v1/canchas") //El path por el cual se accede a este controller
public class CanchaController {

    //Esta sera la property final en la cual se aloje la memoria instanciada del cancha service
    //se pone final para que no pueda ser modificada una vez instanciada
    private final CanchaService canchaService;

    /*
    Aca se utiliza la anotacion Autowired, esta anotacion de springboot se encarga de enlazar todos los componentes,
    va ligada a la inyeccion de dependencias, en este caso como se usa por constructor se declara el Spring Prototype
    Autowired arriba del constructor, de esta forma se asegura que al momento de instanciacion del componente, los modulos
    a los cuales se declara y se necesitan usar esten disponibles para su instanciacion, de este modo al momento de ser llamados
    se instancian momentaneamente, se llama a la funcion requerida y se desinstancia, es el concepto de hollywood, IoC inversion
    of control, No nos llames; nosotros te llamaremos
     */
    @Autowired
    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    /*
     * Aca se esta exponiendo por medio del controller un metodo del service, en este caso particular llamado
     * listAll que devuelve todos los canchaes, se le indica por medio del annotation de springboot GetMapping
     * que se va a llamar por medio de un request del tipo Get al path definido en el componente RequestMapping,
     * en este caso no tiene parametros de entrada ni por path (link) ni por body, de retorno indica que deberia dar
     * un ResponseEntity que contiene una lista de canchaes, en cual se construye al momento del retorno del service
     * por medio del ResponseEntity.ok() con un HttpStatus.Ok (200)*/
    @GetMapping
    public ResponseEntity<List<CanchaDTO>> listAll() {
        return ResponseEntity.ok(canchaService.listAll());
    }

    /*
     * En este caso se devuelve un cancha particular, como se vera en el GetMapping se le indica
     * que se le agrega al path el /id y por medio de @PathVariable se indica que se obtiene dicho parametro
     * por el path de la llamada request, de la misma forma se llama al service y se devuelve el ResponseEntity
     * con el cancha adentro (body) del response*/
    @GetMapping("/{id}")
    public ResponseEntity<CanchaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(canchaService.getById(id));
    }

    /*
     * En este caso se expone un metodo Post request, y por medio de la anotacion de @RequestBody
     * se solicita como parametro un objeto de tipo CanchaDto que le va a entrar como JSON por la llamada
     * (Spring lo transforma automaticamente) y lo utiliza como parametro de entrada en el service*/
    @PostMapping
    public ResponseEntity<CanchaDTO> saveCancha(@RequestBody CanchaDTO cancha) {
        CanchaDTO savedCancha = canchaService.save(cancha);
        return new ResponseEntity<>(savedCancha, HttpStatus.CREATED);
    }

    /*
     * Aca se ven ambos casos, se solicita un ID que se obtiene por PathVariable como parametro de entrada,
     * y un DTO de CanchaDto que se obtiene por medio de RequestBody como parametro, se utiliza para identificar
     * el id del cancha que se quiere modificar y reemplazar sus valores con el body del request, que entra como Json
     * Object y el sistema lo parsea automaticamente a objeto al momento de la llamada al controller, tambien se declara
     * que esto es un metodo PUT*/
    @PutMapping("/{id}")
    public ResponseEntity<CanchaDTO> updateCancha(@PathVariable Long id, @RequestBody CanchaDTO cancha) {
        cancha.setId(id);
        CanchaDTO updatedCancha = canchaService.update(cancha);
        return ResponseEntity.ok(updatedCancha);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCancha(@PathVariable Long id) {
        canchaService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
	


