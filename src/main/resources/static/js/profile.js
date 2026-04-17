document.addEventListener("DOMContentLoaded", function () {
    const avatarSelect = document.getElementById("avatarSelect");
    const previewAvatar = document.getElementById("previewAvatar");

    if (avatarSelect && previewAvatar) {
        avatarSelect.addEventListener("change", function () {
            previewAvatar.src = this.value;
        });
    }
});