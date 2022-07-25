package de.jobst.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import de.jobst.recipe.TestHelper;
import de.jobst.recipe.model.PersistentRecipe;
import de.jobst.recipe.repository.RecipeRepository;

@DataJpaTest
@Import(RecipeService.class)
class RecipeServiceTest {
  
  @Autowired
  RecipeRepository recipeRepository;
  
  @Autowired
  RecipeService recipeService;
  
  @Autowired
  EntityManager em;
  
  @Test
  void deleteShouldRemoveTheIngredientAndReturnCorrectCount() {
    PersistentRecipe recipe = TestHelper.createPersistentRecipe("Title", 3, false, "Do something");
    recipe.addPersistentIngredient(TestHelper.createPersistentIngredient("salt", "tbsp", 1.5));
    recipe.addPersistentIngredient(TestHelper.createPersistentIngredient("onion", null, 2.));
    
    TestHelper.backLinkPersistentIngredients(recipe);
    
    PersistentRecipe savedRecipe = recipeRepository.saveAndFlush(recipe);
    UUID uuid = savedRecipe.getId();
    
    assertThat(recipeRepository.findById(uuid)).isNotEmpty();
    
    long deleteCount = recipeService.delete(uuid);
    
    assertThat(deleteCount).isEqualTo(1l);
    assertThat(recipeRepository.count()).isEqualTo(0l);
    assertThat(recipeRepository.findById(uuid)).isEmpty();
    
  }

}
