package com.pragmagenia.exception;


/**
 * Exception Personalizada para manejo interno de la aplicacion
 * @category Exception
 * @author Rafael Cadenas
 * @version 1.0
 */
public class AppEntityException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7549528238991153828L;

	public AppEntityException(){}
	
	public AppEntityException(String message){
		super(message);
	}
	
}
