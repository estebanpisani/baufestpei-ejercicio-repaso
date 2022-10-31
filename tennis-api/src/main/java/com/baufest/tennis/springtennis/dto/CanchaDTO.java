package com.baufest.tennis.springtennis.dto;

import org.json.JSONObject;

/**
 * <p>Data transfer object de Jugador (Clase)</p>
 * clase utilizada como DTO de jugador
 */
public class CanchaDTO {

	/*Atributos privados de la clase*/

	private Long id;

	private String nombre;
	private String direccion;

	/* Los constructores se utilizan al momento de instanciar nuesta clase y darle espacio en memoria,
	 * los atributos de nuesta clase que no contengan instanciacion en el constructor quedaran con valor null
	 * los constructores pueden ser overraideados y contener instanciaciones para varios atributos distStringos o
	 * incluso el constructor vacio*/

	public CanchaDTO(){}

	public CanchaDTO(String nombre, String direccion) {
		this.nombre = nombre;
		this.direccion = direccion;
	}

	public CanchaDTO(Long id, String nombre, String direccion) {
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