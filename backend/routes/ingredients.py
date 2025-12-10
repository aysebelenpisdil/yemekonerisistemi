import logging

# Configure logging at module level
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Example function where print statements will be replaced with logging
def fetch_ingredients(user_id):
    try:
        logger.info(f"Fetching ingredients for user {user_id}")
        # ...code to fetch ingredients...
    except Exception as e:
        logger.error(f"Error occurred: {e}")

# Another example function
def debug_ingredient_data(data):
    logger.debug(f"Debug: {data}")
    # ...code that works with ingredient data...

# ...rest of the ingredients.py code...