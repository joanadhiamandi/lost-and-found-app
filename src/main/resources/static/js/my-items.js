document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form[action="/items/delete"] button[type="submit"]').forEach(btn => {
        btn.addEventListener('click', function (e) {
            if (!confirm('Delete this item?')) e.preventDefault();
        });
    });

    document.querySelectorAll('form[action="/items/resolve"] button[type="submit"]').forEach(btn => {
        btn.addEventListener('click', function (e) {
            if (!confirm('Mark this item as RESOLVED?')) e.preventDefault();
        });
    });
});
