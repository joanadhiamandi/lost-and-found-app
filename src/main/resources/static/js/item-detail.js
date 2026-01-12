document.addEventListener('DOMContentLoaded', function() {

    // ========== MESSAGE MODAL ==========
    const messageBtn = document.getElementById('messageBtn');
    const messageModal = document.getElementById('messageModal');
    const messageInput = document.getElementById('messageInput');
    const charCount = document.getElementById('charCount');
    const modalError = document.getElementById('modalError');

    // Open modal
    if (messageBtn) {
        messageBtn.addEventListener('click', function() {
            messageModal.classList.remove('hidden');
            messageInput.value = '';
            messageInput.focus();
            if (modalError) modalError.textContent = '';
        });
    }

    // Character counter
    if (messageInput && charCount) {
        messageInput.addEventListener('input', function() {
            charCount.textContent = `${messageInput.value.length}/1000`;
        });
    }

    // ========== COPY LINK BUTTON ==========
    const copyLinkBtn = document.getElementById('copyLinkBtn');
    if (copyLinkBtn) {
        copyLinkBtn.addEventListener('click', function() {
            const url = window.location.href;
            navigator.clipboard.writeText(url).then(() => {
                const originalText = copyLinkBtn.textContent;
                copyLinkBtn.textContent = '✅ Copied!';
                copyLinkBtn.style.background = '#28a745';
                setTimeout(() => {
                    copyLinkBtn.textContent = originalText;
                    copyLinkBtn.style.background = '';
                }, 2000);
            }).catch(err => {
                alert('Failed to copy link');
            });
        });
    }
});

// ========== CLOSE MODAL FUNCTION ==========
function closeMessageModal() {
    const messageModal = document.getElementById('messageModal');
    const modalError = document.getElementById('modalError');
    if (messageModal) {
        messageModal.classList.add('hidden');
    }
    if (modalError) {
        modalError.textContent = '';
    }
}

// ========== SEND MESSAGE FUNCTION ==========
function sendMessageFromModal() {
    const messageInput = document.getElementById('messageInput');
    const messageBtn = document.getElementById('messageBtn');
    const modalError = document.getElementById('modalError');

    const content = messageInput.value.trim();

    if (!content) {
        modalError.textContent = '❌ Please enter a message';
        modalError.style.color = '#dc3545';
        return;
    }

    if (content.length > 1000) {
        modalError.textContent = '❌ Message too long (max 1000 characters)';
        modalError.style.color = '#dc3545';
        return;
    }

    // Get itemId from data attribute or window variable
    const itemId = messageBtn.getAttribute('data-item-id') || window.itemId;

    if (!itemId) {
        modalError.textContent = '❌ Item ID not found';
        modalError.style.color = '#dc3545';
        return;
    }

    // Show loading state
    const sendBtn = document.querySelector('#messageModal .btn-primary');
    const originalText = sendBtn.textContent;
    sendBtn.textContent = 'Sending...';
    sendBtn.disabled = true;

    // Send AJAX request
    fetch('/messages/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${encodeURIComponent(itemId)}&content=${encodeURIComponent(content)}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('✅ Message sent successfully! You can view the conversation in your Messages page.');
                closeMessageModal();
            } else {
                modalError.textContent = data.error || '❌ Failed to send message';
                modalError.style.color = '#dc3545';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            modalError.textContent = '❌ Network error. Please try again.';
            modalError.style.color = '#dc3545';
        })
        .finally(() => {
            sendBtn.textContent = originalText;
            sendBtn.disabled = false;
        });
}
