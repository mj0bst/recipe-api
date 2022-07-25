package de.jobst.recipe;

import de.jobst.recipe.model.PersistentIngredient;
import de.jobst.recipe.model.PersistentRecipe;
import de.jobst.recipe.model.generated.Ingredient;
import de.jobst.recipe.model.generated.Recipe;

public class TestHelper {
  
  public static PersistentRecipe createPersistentRecipe(String title, Integer servings, Boolean vegetarian, String instructions) {
    PersistentRecipe recipe = new PersistentRecipe();
    recipe.setTitle(title);
    recipe.setServings(servings);
    recipe.setVegetarian(vegetarian);
    recipe.setInstructions(instructions);
    return recipe;
  }
  
  public static PersistentIngredient createPersistentIngredient(String name, String unit, Double number) {
    PersistentIngredient ingredient = new PersistentIngredient();
    ingredient.setName(name);
    ingredient.setUnit(unit);
    ingredient.setNumber(number);
    return ingredient;
  }
  
  public static void backLinkPersistentIngredients(PersistentRecipe recipe) {
    recipe.getPersistentIngredients().forEach(ingredient -> ingredient.setPersistentRecipe(recipe));
  }
  
  public static Recipe createRecipe(String title, Integer servings, Boolean vegetarian, String instructions) {
    Recipe recipe = new Recipe();
    recipe.setTitle(title);
    recipe.setServings(servings);
    recipe.setVegetarian(vegetarian);
    recipe.setInstructions(instructions);
    return recipe;
  }
  
  public static Ingredient createIngredient(String name, String unit, Double number) {
    Ingredient ingredient = new Ingredient();
    ingredient.setName(name);
    ingredient.setUnit(unit);
    ingredient.setNumber(number);
    return ingredient;
  }
}
