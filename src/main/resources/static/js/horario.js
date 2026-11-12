document.querySelectorAll("[data-horario-semanal] .fila-horario").forEach(fila => {
    const casilla = fila.querySelector('input[type="checkbox"]');
    const horas = fila.querySelectorAll('input[type="time"]');
    function actualizar() {
        horas.forEach(hora => {
            hora.disabled = !casilla.checked;
            hora.required = casilla.checked;
        });
    }
    casilla.addEventListener("change", actualizar);
    actualizar();
});
