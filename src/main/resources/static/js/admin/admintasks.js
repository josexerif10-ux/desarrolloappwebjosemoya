document.addEventListener("DOMContentLoaded", () => {
    const deleteForms = document.querySelectorAll(".js-admin-delete-form");

    deleteForms.forEach(form => {
        form.addEventListener("submit", (event) => {
            const taskTitle = form.dataset.taskTitle || "esta tarea";

            const confirmed = window.confirm(
                `¿Seguro que quieres eliminar "${taskTitle}"? Esta acción no se puede deshacer.`
            );

            if (!confirmed) {
                event.preventDefault();
            }
        });
    });
});