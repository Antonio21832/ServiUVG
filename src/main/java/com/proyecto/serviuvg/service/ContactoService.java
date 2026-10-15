package com.proyecto.serviuvg.service;

import com.proyecto.serviuvg.dto.ContactoFormulario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

// Spring detecta esta clase como un servicio de la aplicación.
@Service
public class ContactoService {

    private static final Logger log =
            LoggerFactory.getLogger(ContactoService.class);

    private final JavaMailSender mailSender;
    private final String remitente;
    private final String destinatario;
    private final String password;

    // Spring proporciona el servicio de correo y lee la configuración.
    public ContactoService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String remitente,
            @Value("${contacto.destinatario:}") String destinatario,
            @Value("${spring.mail.password:}") String password) {

        this.mailSender = mailSender;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.password = password;
    }

    /*
     * Envía primero la consulta al equipo y después la copia al usuario.
     * Devuelve true si ambos envíos funcionan.
     * Devuelve false si la consulta se envió, pero falló la copia.
     * Si falla el envío al equipo, lanza una excepción.
     */
    public boolean enviar(ContactoFormulario formulario) {

        // Evita intentar el envío si falta configurar el correo.
        if (!StringUtils.hasText(remitente)
                || !StringUtils.hasText(destinatario)
                || !StringUtils.hasText(password)) {

            throw new MailSendException(
                    "Falta configurar el correo de contacto."
            );
        }

        // Información que aparecerá en los dos correos.
        String detalle =
                "Nombre: " + formulario.getNombre()
                + "\nCorreo: " + formulario.getCorreo()
                + "\nAsunto: " + formulario.getAsunto()
                + "\n\nMensaje:\n" + formulario.getMensaje();

        // Primer correo: consulta dirigida al equipo del proyecto.
        SimpleMailMessage consulta = new SimpleMailMessage();

        consulta.setFrom(remitente);
        consulta.setTo(destinatario);

        // Al responder este correo, se responderá al usuario.
        consulta.setReplyTo(formulario.getCorreo());

        consulta.setSubject(
                "[ServiUVG · Contacto] " + formulario.getAsunto()
        );

        consulta.setText(
                "Nueva consulta desde ServiUVG\n\n" + detalle
        );

        // Si este envío falla, no se intenta enviar la copia.
        mailSender.send(consulta);

        // Segundo correo: copia de respaldo para el usuario.
        SimpleMailMessage copia = new SimpleMailMessage();

        copia.setFrom(remitente);
        copia.setTo(formulario.getCorreo());
        copia.setReplyTo(destinatario);

        copia.setSubject(
                "[ServiUVG] Copia de tu consulta: "
                + formulario.getAsunto()
        );

        copia.setText(
                "Hola, " + formulario.getNombre() + ".\n\n"
                + "Tu consulta fue enviada al equipo de ServiUVG. "
                + "Esta es una copia para tu respaldo.\n\n"
                + detalle
                + "\n\nGracias por ayudarnos a mejorar nuestra comunidad."
                + "\nEquipo ServiUVG"
        );

        try {
            mailSender.send(copia);
            return true;

        } catch (MailException ex) {
            // La consulta ya se envió; únicamente falló la copia.
            log.warn(
                    "Consulta enviada; no se pudo enviar la copia ({})",
                    ex.getClass().getSimpleName()
            );

            return false;
        }
    }
}