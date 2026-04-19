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

function deleteCategory(id) {
    if (confirm('Are you sure to delete this Category? All related task will be reassigned to Main category')) {
        document.getElementById('delete-category-' + id).submit();
    }
}