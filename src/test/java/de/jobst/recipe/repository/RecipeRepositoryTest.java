package de.jobst.recipe.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.jobst.recipe.TestHelper;
import de.jobst.recipe.model.PersistentIngredient;
import de.jobst.recipe.model.PersistentRecipe;

@DataJpaTest
class RecipeRepositoryTest {
  
  @Autowired
  RecipeRepository recipeRepository;
  
  @Test
  void createdRecipeAndIngredientsShouldReceiveId() {
    String title = "Title";
    String instructions = "Do this and that.";
    int servings = 5;
    boolean vegetarian = true;

    PersistentRecipe recipe = TestHelper.createPersistentRecipe(title, servings, vegetarian, instructions);
    
    String name1 = "salt";
    double number1 = 3.5;
    String unit1 = "tsp";
    
    PersistentIngredient ingredient1 = TestHelper.createPersistentIngredient(name1, unit1, number1);
    
    String name2 = "pepper";
    double number2 = 12;
    String unit2 = "tbsp";
    
    PersistentIngredient ingredient2 = TestHelper.createPersistentIngredient(name2, unit2, number2);
    
    recipe.addPersistentIngredient(ingredient1);
    recipe.addPersistentIngredient(ingredient2);
    
    TestHelper.backLinkPersistentIngredients(recipe);
    
    PersistentRecipe savedRecipe = recipeRepository.save(recipe);
    System.out.println(recipeRepository.findAll());
    
    assertThat(savedRecipe).isNotNull();
    assertThat(savedRecipe.getId()).isNotNull();
    assertThat(savedRecipe.getPersistentIngredients()).hasSize(2);
    assertThat(savedRecipe.getPersistentIngredients().get(0).getId()).isNotNull();
    assertThat(savedRecipe.getPersistentIngredients().get(1).getId()).isNotNull();
  }
  

}
