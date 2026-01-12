document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Admin panel loaded');

    // Setup all delete buttons
    setupDeleteButtons();

    // Add confirmation styling
    addConfirmationStyles();
});


// Delete Items

function setupDeleteButtons() {
    // Delete Item buttons
    document.querySelectorAll('.delete-item-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const itemId = this.getAttribute('data-item-id');
            const itemName = this.getAttribute('data-item-name') || 'this item';

            if (confirm(`⚠️ Are you sure you want to delete "${itemName}"?\n\nThis action cannot be undone!`)) {
                deleteItem(itemId, this);
            }
        });
    });

    // Delete User buttons
    document.querySelectorAll('.delete-user-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const userId = this.getAttribute('data-user-id');
            const username = this.getAttribute('data-username') || 'this user';

            if (confirm(`⚠️ Are you sure you want to delete user "${username}"?\n\nThis will delete all their items and messages!\n\nThis action cannot be undone!`)) {
                deleteUser(userId, this);
            }
        });
    });
}


// Delete Item Function

function deleteItem(itemId, button) {
    // Disable button during request
    button.disabled = true;
    button.textContent = 'Deleting...';

    fetch('/admin/delete-item', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${itemId}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Show success message
                showNotification(data.message, 'success');

                // Remove row from table with animation
                const row = button.closest('tr');
                row.style.transition = 'all 0.3s ease';
                row.style.opacity = '0';
                row.style.transform = 'translateX(-20px)';

                setTimeout(() => {
                    row.remove();
                    updateStats();
                }, 300);
            } else {
                // Show error message
                showNotification(data.message, 'error');
                button.disabled = false;
                button.textContent = 'Delete';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('❌ Network error. Please try again.', 'error');
            button.disabled = false;
            button.textContent = 'Delete';
        });
}


// Delete User Function
function deleteUser(userId, button) {
    // Disable button during request
    button.disabled = true;
    button.textContent = 'Deleting...';

    fetch('/admin/delete-user', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `userId=${userId}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Show success message
                showNotification(data.message, 'success');

                // Remove row from table with animation
                const row = button.closest('tr');
                row.style.transition = 'all 0.3s ease';
                row.style.opacity = '0';
                row.style.transform = 'translateX(-20px)';

                setTimeout(() => {
                    row.remove();
                    updateStats();
                }, 300);
            } else {
                // Show error message
                showNotification(data.message, 'error');
                button.disabled = false;
                button.textContent = 'Delete';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('❌ Network error. Please try again.', 'error');
            button.disabled = false;
            button.textContent = 'Delete';
        });
}


// Update Stats After Deletion

function updateStats() {
    // Reload page to refresh stats
    location.reload();
}


// Notification System

function showNotification(message, type) {
    // Remove existing notifications
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;

    // Add to page
    document.body.appendChild(notification);

    // Show with animation
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

    // Auto-hide after 3 seconds
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}


// Add Notification Styles

function addConfirmationStyles() {
    if (!document.getElementById('notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            .notification {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 16px 24px;
                border-radius: 10px;
                color: white;
                font-weight: 600;
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
                transform: translateY(-100px);
                opacity: 0;
                transition: all 0.3s ease;
                z-index: 10000;
                max-width: 400px;
            }
            
            .notification.show {
                transform: translateY(0);
                opacity: 1;
            }
            
            .notification-success {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            }
            
            .notification-error {
                background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            }
            
            /* Smooth row deletion animation */
            tr {
                transition: all 0.3s ease;
            }
        `;
        document.head.appendChild(style);
    }
}

console.log('✅ Admin panel JavaScript loaded successfully');
