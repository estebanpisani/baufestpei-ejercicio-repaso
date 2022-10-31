package com.baufest.tennis.springtennis.model;

import org.json.JSONObject;

import javax.persistence.*;
@Entity
@Table(name = "Cancha")
public class Cancha {

        /*Atributos privados de la clase*/

        @Id /* Sera el index de nuestra tabla */
        @GeneratedValue(strategy = GenerationType.IDENTITY) /* Valor Auto-generado con la estategia: GenerationType.IDENTITY */
        private Long id;

        @Column(nullable = false) /* No podemos recibir este valor como nulo "null" */
        private String nombre;

    @Column(nullable = false) /* No podemos recibir este valor como nulo "null" */
    private String direccion;

        /* Construtores de nuestro modelo de dato */

        /* Los constructores se utilizan al momento de instanciar nuesta clase y darle espacio en memoria,
         * los atributos de nuesta clase que no contengan instanciacion en el constructor quedaran con valor null
         * los constructores pueden ser overraideados y contener instanciaciones para varios atributos distStringos o
         * incluso el constructor vacio*/

        public Cancha(){} //Por ejemplo aca tenemos un constructor vacio
        public Cancha(String nombre, String direccion) { //Aca un constructor con solo dos parametros instanciados
            this.nombre = nombre;
            this.direccion = direccion;
        }

        public Cancha(Long id, String nombre, String direccion) { //Aca un constructor con tres parametros instanciados
            this.id = id;
            this.nombre = nombre;
            this.direccion = direccion;
        }

        /* Getters & Setters */

	/* Los getters y setters se utilizan para acceder a los atributos de nuestro objeto,
	como estos son PRIVADOS solo pueden ser accedidos desde metodos publicos, los cuales llamamos
	getters y setters, estos permiten modificar o obtener los atributos privados de la clase,
	si queremos que un atributo no sea accesible para cambio ni obtenerlo simplemente borramos el getter
	y el setter, de forma natural no habria forma de acceder a dicho atributo fuera de la instanciacion
	 */

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        /* Metodo para retornar nuestro objeto en un formato JSON */
        /*Este metodo es muy utilizado para poder transformar el objeto a JSON en caso de ser necesario para retorno*/
        public JSONObject toJSONObject() {
            JSONObject jo = new JSONObject();
            jo.put("id",getId());
            jo.put("nombre",getNombre());
            jo.put("direccion",getDireccion());
            return jo;
        }

}

