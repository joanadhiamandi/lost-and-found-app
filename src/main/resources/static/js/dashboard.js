// Dashboard functionality
console.log('Dashboard loaded successfully');

// Optional: Add any interactive features here
document.addEventListener('DOMContentLoaded', function() {
    console.log('Welcome to the dashboard!');

    // You can add animations or dynamic features here
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        setTimeout(() => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            setTimeout(() => {
                card.style.transition = 'all 0.5s ease';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, 50);
        }, index * 100);
    });
});
