document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        const category = form.querySelector('select[name="categoryId"]');
        if (category && !category.value) {
            e.preventDefault();
            alert('Please select a category.');
            category.focus();
        }
    });
});
