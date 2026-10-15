package com.proyecto.serviuvg.dto;

// Anotaciones para validar los datos del formulario.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Recibe los datos ingresados en contacto.html.
 * No representa una tabla de la base de datos.
 */
public class ContactoFormulario {

    // El nombre es obligatorio y admite hasta 80 caracteres.
    // No permite saltos de línea.
    @NotBlank(message = "Ingresá tu nombre.")
    @Size(max = 80, message = "Máximo 80 caracteres.")
    @Pattern(
        regexp = "[^\\r\\n]*",
        message = "El nombre debe estar en una sola línea."
    )
    private String nombre;

    // El correo es obligatorio y debe tener un formato válido.
    // Se permite una sola dirección, sin saltos de línea.
    @NotBlank(message = "Ingresá tu correo.")
    @Email(message = "Ingresá un correo válido.")
    @Size(max = 254)
    @Pattern(
        regexp = "[^\\r\\n,;]*",
        message = "Ingresá una sola dirección de correo."
    )
    private String correo;

    // El asunto es obligatorio y admite hasta 120 caracteres.
    // No permite saltos de línea porque se usará en el asunto del correo.
    @NotBlank(message = "Escribí el asunto.")
    @Size(max = 120, message = "Máximo 120 caracteres.")
    @Pattern(
        regexp = "[^\\r\\n]*",
        message = "El asunto debe estar en una sola línea."
    )
    private String asunto;

    // El mensaje es obligatorio y debe tener entre 10 y 5000 caracteres.
    @NotBlank(message = "Escribí tu mensaje.")
    @Size(
        min = 10,
        max = 5000,
        message = "El mensaje debe tener entre 10 y 5000 caracteres."
    )
    private String mensaje;

    // Los getters permiten consultar los valores de los campos.
    // Los setters permiten asignar los datos recibidos del formulario.
    // strip() elimina espacios al inicio y al final del texto.
    // La comprobación de null evita llamar a strip() sobre un valor nulo.

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.strip();
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo == null ? null : correo.strip();
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto == null ? null : asunto.strip();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje == null ? null : mensaje.strip();
    }
}