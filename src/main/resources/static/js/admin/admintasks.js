document.addEventListener('DOMContentLoaded', function () {
    const logoutLink = document.getElementById('logoutLink');
    const logoutForm = document.getElementById('logoutForm');

    if (logoutLink && logoutForm) {
        logoutLink.addEventListener('click', function (event) {
            event.preventDefault();
            logoutForm.submit();
        });
    }
});

function deleteTask(id) {
    if (confirm('Are you sure to delete this Task?')) {
        document.getElementById('delete-task-' + id).submit();
    }
}