"""
Seed script to populate database from JSON data
"""
import sys
import os
import json
from pathlib import Path

# Add parent directory to path
sys.path.append(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

from db.base import SessionLocal, engine, Base
from db.models import Ingredient, Recipe
from sqlalchemy.exc import IntegrityError

def load_json_data(filename):
    """Load data from JSON file"""
    json_path = Path(__file__).parent.parent / "data" / filename
    if not json_path.exists():
        print(f"Warning: {json_path} not found")
        return []

    with open(json_path, 'r', encoding='utf-8') as f:
        return json.load(f)

def seed_ingredients(session):
    """Seed ingredients from JSON"""
    ingredients_data = load_json_data("ingredients.json")

    if not ingredients_data:
        print("No ingredients data found")
        return 0

    count = 0
    for item in ingredients_data:
        try:
            # Check if ingredient already exists
            existing = session.query(Ingredient).filter_by(name=item['name']).first()
            if existing:
                print(f"Skipping existing: {item['name']}")
                continue

            ingredient = Ingredient(
                name=item['name'],
                portion_g=item['portion_g'],
                calories=item['calories'],
                fat_g=item['fat_g'],
                carbs_g=item['carbs_g'],
                protein_g=item['protein_g'],
                sugar_g=item['sugar_g'],
                fiber_g=item['fiber_g']
            )
            session.add(ingredient)
            session.flush()  # Flush to catch integrity errors early
            count += 1
        except IntegrityError:
            session.rollback()
            print(f"Skipping duplicate: {item['name']}")
        except Exception as e:
            print(f"Error adding {item['name']}: {e}")
            session.rollback()

    session.commit()
    return count

def seed_recipes(session):
    """Seed sample recipes"""
    # Sample recipes for POC
    recipes = [
        {
            "title": "Domates Salatası",
            "cooking_time": 10,
            "calories": 150,
            "servings": 2,
            "image_url": "https://example.com/domates-salatasi.jpg",
            "image_thumb_url": "https://example.com/domates-salatasi-thumb.jpg",
            "instructions": json.dumps(["Domatesleri dilimleyin", "Soğan ekleyin", "Zeytinyağı ve limon ekleyin"])
        },
        {
            "title": "Mercimek Çorbası",
            "cooking_time": 30,
            "calories": 250,
            "servings": 4,
            "image_url": "https://example.com/mercimek-corbasi.jpg",
            "image_thumb_url": "https://example.com/mercimek-corbasi-thumb.jpg",
            "instructions": json.dumps(["Mercimeği haşlayın", "Sebzeleri ekleyin", "Blenderdan geçirin"])
        },
        {
            "title": "Pilav",
            "cooking_time": 25,
            "calories": 200,
            "servings": 4,
            "image_url": "https://example.com/pilav.jpg",
            "image_thumb_url": "https://example.com/pilav-thumb.jpg",
            "instructions": json.dumps(["Pirinci yıkayın", "Tereyağında kavurun", "Su ekleyip pişirin"])
        }
    ]

    count = 0
    for recipe_data in recipes:
        try:
            # Check if recipe exists by title
            existing = session.query(Recipe).filter_by(title=recipe_data['title']).first()
            if existing:
                print(f"Skipping existing recipe: {recipe_data['title']}")
                continue

            recipe = Recipe(**recipe_data)
            session.add(recipe)
            session.flush()
            count += 1
        except IntegrityError:
            session.rollback()
            print(f"Skipping duplicate recipe: {recipe_data['title']}")
        except Exception as e:
            print(f"Error adding recipe: {e}")
            session.rollback()

    session.commit()
    return count

def main():
    """Main seed function"""
    print("Starting database seed...")

    # Create tables if not exists
    Base.metadata.create_all(bind=engine)

    session = SessionLocal()
    try:
        # Check if already seeded
        existing_ingredients = session.query(Ingredient).count()
        if existing_ingredients > 0:
            print(f"Database already contains {existing_ingredients} ingredients. Skipping seed.")
            return

        # Seed data
        ingredient_count = seed_ingredients(session)
        recipe_count = seed_recipes(session)

        print(f"✅ Seed complete:")
        print(f"   - {ingredient_count} ingredients added")
        print(f"   - {recipe_count} recipes added")

        # Verify counts
        total_ingredients = session.query(Ingredient).count()
        total_recipes = session.query(Recipe).count()
        print(f"✅ Database totals:")
        print(f"   - Total ingredients: {total_ingredients}")
        print(f"   - Total recipes: {total_recipes}")

    except Exception as e:
        print(f"❌ Seed failed: {e}")
        session.rollback()
    finally:
        session.close()

if __name__ == "__main__":
    main()