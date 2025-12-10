"""
SQLAlchemy Database Models
"""
from sqlalchemy import Column, Integer, String, Float, ForeignKey, Table, Text, DateTime
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from .base import Base

# Many-to-many association table for recipes and ingredients
recipe_ingredients = Table(
    'recipe_ingredients',
    Base.metadata,
    Column('recipe_id', Integer, ForeignKey('recipes.id'), primary_key=True),
    Column('ingredient_id', Integer, ForeignKey('ingredients.id'), primary_key=True),
    Column('amount', Float, nullable=True),
    Column('unit', String(50), nullable=True)
)

class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(100), unique=True, index=True, nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

class Ingredient(Base):
    __tablename__ = 'ingredients'

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), unique=True, index=True, nullable=False)
    portion_g = Column(Float, nullable=False)
    calories = Column(Float, nullable=False)
    fat_g = Column(Float, nullable=False)
    carbs_g = Column(Float, nullable=False)
    protein_g = Column(Float, nullable=False)
    sugar_g = Column(Float, nullable=False)
    fiber_g = Column(Float, nullable=False)
    embedding = Column(Text, nullable=True)  # Store as JSON string

    recipes = relationship("Recipe", secondary=recipe_ingredients, back_populates="ingredients")

class Recipe(Base):
    __tablename__ = 'recipes'

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False, unique=True, index=True)
    cooking_time = Column(Integer, nullable=False)  # minutes
    calories = Column(Integer, nullable=False)
    servings = Column(Integer, default=4)
    recommendation_reason = Column(Text, nullable=True)
    available_ingredients = Column(Text, nullable=True)
    image_url = Column(String(500), nullable=True)
    image_thumb_url = Column(String(500), nullable=True)
    instructions = Column(Text, nullable=True)  # JSON array as string

    # Popularity tracking
    popularity_score = Column(Float, default=0.5)
    view_count = Column(Integer, default=0)
    favorite_count = Column(Integer, default=0)

    ingredients = relationship("Ingredient", secondary=recipe_ingredients, back_populates="recipes")

class Embedding(Base):
    __tablename__ = 'embeddings'

    id = Column(Integer, primary_key=True, index=True)
    entity_type = Column(String(50), nullable=False, index=True)  # 'ingredient' or 'recipe'
    entity_id = Column(Integer, nullable=False, index=True)
    embedding_text = Column(Text, nullable=False)  # JSON array
    created_at = Column(DateTime, server_default=func.now())