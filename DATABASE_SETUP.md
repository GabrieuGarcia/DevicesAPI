# Database Setup Guide for DevicesAPI

This guide explains how to set up a PostgreSQL database for the DevicesAPI application using Docker Compose.

## Prerequisites

### Install Docker Desktop
1. Download Docker Desktop from: https://www.docker.com/products/docker-desktop
2. Install and start Docker Desktop
3. Verify installation by running: `docker --version`

## Quick Start with Docker Compose

### 1. Start the PostgreSQL Database
```bash
# Navigate to the project directory
cd /path/to/DevicesAPI

# Start PostgreSQL container in detached mode
docker compose up -d

# Check if the container is running
docker compose ps
```

### 2. Verify Database Connection
```bash
# Connect to PostgreSQL container
docker compose exec postgres psql -U postgres -d devicesdb

# List tables (should show 'devices' table)
\dt

# View sample data
SELECT * FROM devices;

# Exit PostgreSQL
\q
```

### 3. Run the Application
```bash
# The application will automatically connect to the PostgreSQL database
mvn spring-boot:run
```

## Database Configuration

The application is configured to connect to PostgreSQL with these settings:
- **Host**: localhost
- **Port**: 5432
- **Database**: devicesdb
- **Username**: postgres
- **Password**: postgres

## Docker Compose Configuration

The `compose.yaml` file includes:
- PostgreSQL 15 Alpine image for better performance
- Persistent data storage with named volume
- Health checks for container readiness
- Automatic database initialization with sample data

## Database Schema

The database includes:
- **devices** table with columns: id, name, brand, state, created_at, updated_at
- Indexes on brand and state for better query performance
- Sample data with various device types
- Automatic timestamp updates via triggers

## Useful Commands

### Docker Compose Commands
```bash
# Start services
docker compose up -d

# Stop services
docker compose down

# View logs
docker compose logs postgres

# Restart services
docker compose restart

# Remove everything (including data)
docker compose down -v
```

### Database Management
```bash
# Backup database
docker compose exec postgres pg_dump -U postgres devicesdb > backup.sql

# Restore database
docker compose exec -T postgres psql -U postgres devicesdb < backup.sql

# Access PostgreSQL shell
docker compose exec postgres psql -U postgres -d devicesdb
```

## Alternative Setup (Without Docker)

If you prefer not to use Docker, you can install PostgreSQL directly:

### macOS (using Homebrew)
```bash
# Install PostgreSQL
brew install postgresql

# Start PostgreSQL service
brew services start postgresql

# Create database and user
psql postgres
CREATE DATABASE devicesdb;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE devicesdb TO postgres;
\q

# Run the initialization script
psql -U postgres -d devicesdb -f init-db.sql
```

## Troubleshooting

### Common Issues

1. **Port 5432 already in use**
   ```bash
   # Check what's using port 5432
   lsof -i :5432
   
   # Stop local PostgreSQL if running
   brew services stop postgresql
   ```

2. **Container won't start**
   ```bash
   # Check container logs
   docker compose logs postgres
   
   # Remove and recreate
   docker compose down -v
   docker compose up -d
   ```

3. **Connection refused**
   - Ensure Docker Desktop is running
   - Wait for health check to pass: `docker compose ps`
   - Check application.yml configuration

4. **Permission denied**
   ```bash
   # Fix file permissions
   chmod 644 init-db.sql
   ```

## Testing the Setup

Once the database is running, you can test the API endpoints:

```bash
# Get all devices
curl http://localhost:8080/api/devices

# Get devices by brand
curl http://localhost:8080/api/devices/brand/Apple

# Get devices by state
curl http://localhost:8080/api/devices/state/ACTIVE
```

## Next Steps

1. Install Docker Desktop if not already installed
2. Run `docker compose up -d` to start PostgreSQL
3. Start the application with `mvn spring-boot:run`
4. Test the API endpoints to verify everything works

The database will persist data between container restarts thanks to the named volume configuration.