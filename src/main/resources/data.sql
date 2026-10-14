-- Insert an admin user with a hashed password (password123)
insert into users (username, password, email) values
  ('admin', '$2a$10$9gVowZYyIn0LEP/pOeU2mu6869gQ8C7kfVmYUfpoVZEk8EIhqkLxG', 'admin@example.com');