package de.jobst.recipe.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.jobst.recipe.model.PersistentIngredient;
import de.jobst.recipe.model.PersistentRecipe;
import de.jobst.recipe.model.generated.Ingredient;
import de.jobst.recipe.model.generated.Recipe;

class RecipePersistentRecipeMapperTest {
  
  @Test
  void mapsAllAttributesFromRecipeToPersistentRecipe() {
    
    UUID randomUUID = UUID.randomUUID();
    String title = "Title";
    String instructions = "Do this and that.";
    int servings = 5;
    boolean vegetarian = true;

    Recipe recipe = new Recipe();
    recipe.setId(randomUUID);
    recipe.setTitle(title);
    recipe.setInstructions(instructions);
    recipe.setServings(servings);
    recipe.setVegetarian(vegetarian);
    
    String name1 = "salt";
    double number1 = 3.5;
    String unit1 = "tsp";
    
    Ingredient ingredient1 = new Ingredient();
    ingredient1.setName(name1);
    ingredient1.setNumber(number1);
    ingredient1.setUnit(unit1);
    
    String name2 = "pepper";
    double number2 = 12;
    String unit2 = "tbsp";
    
    Ingredient ingredient2 = new Ingredient();
    ingredient2.setName(name2);
    ingredient2.setNumber(number2);
    ingredient2.setUnit(unit2);
    
    recipe.setIngredients(new ArrayList<>());
    recipe.getIngredients().add(ingredient1);
    recipe.getIngredients().add(ingredient2);
    
    PersistentRecipe persistentRecipe = RecipePersistentRecipeMapper.INSTANCE.recipeToPersistentRecipe(recipe);
    
    assertThat(persistentRecipe)
      .returns(randomUUID, PersistentRecipe::getId)
      .returns(title, PersistentRecipe::getTitle)
      .returns(instructions, PersistentRecipe::getInstructions)
      .returns(servings, PersistentRecipe::getServings)
      .returns(vegetarian, PersistentRecipe::getVegetarian);
    
    assertThat(persistentRecipe.getPersistentIngredients()).hasSize(2);
    
    assertThat(persistentRecipe.getPersistentIngredients().get(0))
      .returns(null, PersistentIngredient::getId)
      .returns(name1, PersistentIngredient::getName)
      .returns(number1, PersistentIngredient::getNumber)
      .returns(unit1, PersistentIngredient::getUnit);
    
    assertThat(persistentRecipe.getPersistentIngredients().get(1))
    .returns(null, PersistentIngredient::getId)
    .returns(name2, PersistentIngredient::getName)
    .returns(number2, PersistentIngredient::getNumber)
    .returns(unit2, PersistentIngredient::getUnit);
  }
  
  @Test
  void mapsAllAttributesFromPersistentRecipeToRecipe() {
    
    UUID randomUUID = UUID.randomUUID();
    String title = "Title";
    String instructions = "Do this and that.";
    int servings = 5;
    boolean vegetarian = true;

    PersistentRecipe persistentRecipe = new PersistentRecipe();
    persistentRecipe.setId(randomUUID);
    persistentRecipe.setTitle(title);
    persistentRecipe.setInstructions(instructions);
    persistentRecipe.setServings(servings);
    persistentRecipe.setVegetarian(vegetarian);
    
    String name1 = "salt";
    double number1 = 3.5;
    String unit1 = "tsp";
    
    PersistentIngredient persistentIngredient1 = new PersistentIngredient();
    persistentIngredient1.setName(name1);
    persistentIngredient1.setNumber(number1);
    persistentIngredient1.setUnit(unit1);
    
    String name2 = "pepper";
    double number2 = 12;
    String unit2 = "tbsp";
    
    PersistentIngredient persistentingredient2 = new PersistentIngredient();
    persistentingredient2.setName(name2);
    persistentingredient2.setNumber(number2);
    persistentingredient2.setUnit(unit2);
    
    persistentRecipe.setPersistentIngredients(new ArrayList<>());
    persistentRecipe.getPersistentIngredients().add(persistentIngredient1);
    persistentRecipe.getPersistentIngredients().add(persistentingredient2);
    
    Recipe recipe = RecipePersistentRecipeMapper.INSTANCE.persistentRecipeToRecipe(persistentRecipe);
    
    assertThat(recipe)
      .returns(randomUUID, Recipe::getId)
      .returns(title, Recipe::getTitle)
      .returns(instructions, Recipe::getInstructions)
      .returns(servings, Recipe::getServings)
      .returns(vegetarian, Recipe::getVegetarian);
    
    assertThat(recipe.getIngredients()).hasSize(2);
    
    assertThat(recipe.getIngredients().get(0))
      .returns(name1, Ingredient::getName)
      .returns(number1, Ingredient::getNumber)
      .returns(unit1, Ingredient::getUnit);
    
    assertThat(recipe.getIngredients().get(1))
    .returns(name2, Ingredient::getName)
    .returns(number2, Ingredient::getNumber)
    .returns(unit2, Ingredient::getUnit);
  }
}
