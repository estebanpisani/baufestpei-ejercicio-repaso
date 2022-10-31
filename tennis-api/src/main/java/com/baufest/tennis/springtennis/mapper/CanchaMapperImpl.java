package com.baufest.tennis.springtennis.mapper;

import com.baufest.tennis.springtennis.dto.CanchaDTO;
import com.baufest.tennis.springtennis.model.Cancha;
import org.springframework.stereotype.Component;

@Component
public class CanchaMapperImpl implements CanchaMapper {

    /* Utilizamos el Mapper para no acceder directamente a la base.
    Lo implementamos en el CanchaServiceImpl a traves del @Autowired
     */

    /* Esta funcion recibe como parametro un Cancha
     * y en base a el parametro recibido genera una nueva instancia
     * de cancha DTO.*/

    @Override
    public CanchaDTO toDTO(Cancha entity) {
        if ( entity == null ) {
            return null;
        }

        CanchaDTO canchaDTO = new CanchaDTO();

        canchaDTO.setId( entity.getId() );
        canchaDTO.setNombre( entity.getNombre() );
        canchaDTO.setDireccion( entity.getDireccion() );

        return canchaDTO;
    }

    /* Esta funcion recibe como parametro un CanchaDTO
     * y hacemos un generamos una nueva instancia de Cancha */
    @Override
    public Cancha fromDTO(CanchaDTO entity) {
        if ( entity == null ) {
            return null;
        }

        Cancha cancha = new Cancha();

        cancha.setId( entity.getId() );
        cancha.setNombre( entity.getNombre() );
        cancha.setDireccion( entity.getDireccion() );

        return cancha;
    }
}

