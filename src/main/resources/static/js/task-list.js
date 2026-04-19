function deleteTask(id) {
    if (confirm('¿Seguro que quieres eliminar esta tarea? 🐝')) {
        document.getElementById('delete-task-' + id).submit();
    }
}