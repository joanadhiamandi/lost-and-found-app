document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form[action="/items/update"]');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        const name = form.querySelector('input[name="itemName"]');
        const location = form.querySelector('input[name="location"]');
        if (name && !name.value.trim()) {
            e.preventDefault(); alert('Item name is required.'); name.focus(); return;
        }
        if (location && !location.value.trim()) {
            e.preventDefault(); alert('Location is required.'); location.focus(); return;
        }
    });
});
