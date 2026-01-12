-- Users Table
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       phone VARCHAR(20),
                       role VARCHAR(20) DEFAULT 'MEMBER',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       is_active TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Categories Table
CREATE TABLE categories (
                            category_id INT AUTO_INCREMENT PRIMARY KEY,
                            category_name VARCHAR(50) NOT NULL,
                            description VARCHAR(255),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Items Table
CREATE TABLE items (
                       item_id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT NOT NULL,
                       category_id INT,
                       item_type VARCHAR(20) NOT NULL,
                       item_name VARCHAR(100) NOT NULL,
                       description TEXT,
                       location VARCHAR(200),
                       date_lost_found DATE,
                       contact_info VARCHAR(200),
                       status VARCHAR(20) DEFAULT 'ACTIVE',
                       viewcount INT DEFAULT 0,
                       latitude DECIMAL(10,8),
                       longitude DECIMAL(11,8),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                       FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Messages Table
CREATE TABLE messages (
                          message_id INT AUTO_INCREMENT PRIMARY KEY,
                          sender_id INT NOT NULL,
                          recipient_id INT NOT NULL,
                          message_text TEXT NOT NULL,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          is_read TINYINT(1) DEFAULT 0,
                          FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (recipient_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Comments Table
CREATE TABLE comments (
                          comment_id INT AUTO_INCREMENT PRIMARY KEY,
                          item_id INT NOT NULL,
                          user_id INT NOT NULL,
                          comment_text TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Alerts Table
CREATE TABLE alerts (
                        alert_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        item_id INT NOT NULL,
                        message VARCHAR(255),
                        is_read TINYINT(1) DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                        FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Images Table
CREATE TABLE images (
                        image_id INT AUTO_INCREMENT PRIMARY KEY,
                        item_id INT NOT NULL,
                        image_path VARCHAR(255) NOT NULL,
                        uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User Searches Table
CREATE TABLE user_searches (
                               search_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               item_type VARCHAR(20),
                               category_id INT,
                               keywords VARCHAR(255),
                               search_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Audit Log Table
CREATE TABLE audit_log (
                           log_id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT,
                           action VARCHAR(100) NOT NULL,
                           description TEXT,
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Insert default categories
INSERT INTO categories (category_name, description) VALUES
                                                        ('Electronics', 'Phones, laptops, tablets, etc.'),
                                                        ('Documents', 'IDs, passports, certificates'),
                                                        ('Accessories', 'Bags, wallets, jewelry'),
                                                        ('Keys', 'House keys, car keys'),
                                                        ('Clothing', 'Jackets, shoes, etc.'),
                                                        ('Other', 'Miscellaneous items');
