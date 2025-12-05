# Deployment and Testing Guide

## Setup Steps

### 1. Install Dependencies
```bash
cd backend
pip install -r requirements.txt
```

### 2. Database Setup

#### Create PostgreSQL Database
```bash
# Connect to PostgreSQL as superuser
psql -U postgres

# Create database
CREATE DATABASE yemekdb;

# Exit
\q
```

#### Configure Environment
```bash
# Copy example env file
cp .env.example .env

# Edit .env with your database credentials
# Example: DATABASE_URL=postgresql://postgres:password@localhost:5432/yemekdb
```

### 3. Run Migrations
```bash
# Initialize database schema
cd backend
alembic upgrade head
```

### 4. Seed Database
```bash
# Load initial data from JSON
python scripts/seed_database.py
```

### 5. Build Embeddings (for Semantic Search)
```bash
# Generate embeddings and FAISS index
python scripts/build_embeddings.py
```

### 6. Run Application
```bash
# Start FastAPI server
python main.py
# Or with uvicorn
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

## Testing

### Test Fuzzy Search (Existing)
```bash
# Run existing fuzzy search tests
pytest test_fuzzy_search.py -v
```

### Test Database Connection
```bash
# Check ingredient count
psql -U postgres -d yemekdb -c "SELECT COUNT(*) FROM ingredients;"
# Expected: 467 (or number in ingredients.json)
```

### Test API Endpoints

#### Fuzzy Search (Existing)
```bash
# Test fuzzy search
curl "http://localhost:8000/api/ingredients?q=domates&limit=5"
```

#### Semantic Search (New)
```bash
# Test semantic search
curl "http://localhost:8000/api/semantic/search?q=domates&limit=5"

# Check semantic service status
curl "http://localhost:8000/api/semantic/status"
```

## Performance Metrics

### Expected Performance
- Fuzzy search p95 latency: < existing baseline ±10ms
- Semantic search average query time: < 600ms (first POC)
- Database query overhead: < 50ms for simple queries

## Rollback Instructions

### Full Rollback
```bash
# 1. Stop the application
# 2. Rollback database
alembic downgrade base

# 3. Drop database (optional)
psql -U postgres -c "DROP DATABASE yemekdb;"

# 4. Revert to JSON-only mode
git revert HEAD  # Or specific commit
```

### Partial Rollback (Keep DB, Disable Semantic)
```bash
# Simply don't run build_embeddings.py
# The semantic endpoints will return empty results
```

## Edge Cases Handled

1. **Empty Query**: Returns first N ingredients
2. **Database Empty**: Service falls back to JSON loading
3. **Index Missing**: Semantic search returns empty results with warning
4. **DB Connection Failed**: Falls back to JSON mode (backward compatible)

## Monitoring

Check logs for:
- ✅ Success messages (green)
- ⚠️ Warnings (yellow)
- ❌ Errors (red)

## Notes

- Database models use SQLAlchemy ORM
- Embeddings stored as JSON strings in DB (POC approach)
- FAISS index stored as binary file in data/embeddings/
- Turkish BERT model used for semantic search
- Backward compatibility maintained with JSON fallback