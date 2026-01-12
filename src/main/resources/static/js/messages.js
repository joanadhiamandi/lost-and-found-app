document.addEventListener('DOMContentLoaded', function() {
    loadConversations();
});

function loadConversations() {
    const conversationsList = document.getElementById('conversationsList');

    // Simulated conversations
    // For now, show a placeholder
    conversationsList.innerHTML = `
        <p style="text-align: center; color: #7f8c8d; padding: 2rem;">
            💬 No conversations yet. Start by messaging someone from an item detail page!
        </p>
    `;

}

function displayConversations(conversations) {
    const list = document.getElementById('conversationsList');
    list.innerHTML = '';

    if (!conversations || conversations.length === 0) {
        list.innerHTML = '<p class="empty-message">No conversations yet</p>';
        return;
    }

    conversations.forEach(conv => {
        const item = document.createElement('div');
        item.className = 'conversation-item';
        item.innerHTML = `
            <div class="conversation-info">
                <div class="conversation-user">${escapeHtml(conv.otherUserUsername)}</div>
                <div class="conversation-item-detail">
                    <strong>Item:</strong> ${escapeHtml(conv.itemName)}
                </div>
                <div class="conversation-preview">${escapeHtml(conv.lastMessage)}</div>
            </div>
            <div class="conversation-date">${formatDate(conv.lastMessageTime)}</div>
        `;
        item.onclick = () => openThread(conv.otherUserId, conv.itemId);
        list.appendChild(item);
    });
}

function openThread(otherUserId, itemId) {
    window.location.href = `/messages/thread?otherUserId=${otherUserId}&itemId=${itemId}`;
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;

    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return 'just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days < 7) return `${days}d ago`;

    return date.toLocaleDateString();
}
