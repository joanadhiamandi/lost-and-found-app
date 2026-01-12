document.addEventListener('DOMContentLoaded', function () {
    const filterForm = document.querySelector('form[action="/items/browse"]');
    if (!filterForm) return;

    // Small UX: if user changes a dropdown, auto-submit
    const selects = filterForm.querySelectorAll('select');
    selects.forEach(s => s.addEventListener('change', () => filterForm.submit()));
});
