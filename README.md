Pasos para ejecutar:
Clonar repositorio
Iniciar el servicio de base de datos, en este caso usamos MySQL por medio de XAMPP
Crear la base de datos con el nombre "base_serviuvg" o crear una base de datos con otro nombre y cambiar las credenciales en application.properties
Asegurarse de tener levantar el servicio de base de datos
Para correrlo en visual studio code asegurarse de tener las extensiones de spring boot
Abrir el proyecto en visual studio code y abrir la clase ServiUvgApplication.java y ejecutarla
Abrir localhost:8080

Disponibilidad semanal de servicios:
- Al publicar o editar un servicio, marca los días de atención y un horario de inicio y fin por día. El horario es opcional; los servicios anteriores se muestran como «Horario por confirmar».
- En «Servicios» se puede ver la disponibilidad antes de enviar una solicitud. La fecha y hora solicitadas deben estar dentro del horario publicado; la solicitud permanece pendiente hasta que el proveedor la acepte.
- Los horarios se repiten semanalmente y corresponden a la hora local de Guatemala. Para la base de datos local se usa `spring.jpa.hibernate.ddl-auto=update`, que crea la tabla de disponibilidad y agrega la hora a las contrataciones existentes (sin modificar sus fechas).
