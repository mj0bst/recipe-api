package de.jobst.recipe.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.jobst.recipe.TestHelper;
import de.jobst.recipe.model.PersistentRecipe;

@DataJpaTest
class RecipeQueryBuilderTest {
  
  @Autowired
  RecipeRepository recipeRepository;
  
  @Autowired
  EntityManager em;
  
  @BeforeEach
  void setUp() {
    PersistentRecipe recipe1 = TestHelper.createPersistentRecipe("Title", 3, false, "Do something");
    recipe1.addPersistentIngredient(TestHelper.createPersistentIngredient("salt", "tbsp", 1.5));
    recipe1.addPersistentIngredient(TestHelper.createPersistentIngredient("onion", null, 2.));
    TestHelper.backLinkPersistentIngredients(recipe1);
    
    recipeRepository.save(recipe1);
    
    PersistentRecipe recipe2 = TestHelper.createPersistentRecipe("Title", 2, true, "Do another thing");
    recipe2.addPersistentIngredient(TestHelper.createPersistentIngredient("salt", "tbsp", 1.5));
    recipe2.addPersistentIngredient(TestHelper.createPersistentIngredient("pepper", null, 2.));
    recipe2.addPersistentIngredient(TestHelper.createPersistentIngredient("chili", null, 2.));
    TestHelper.backLinkPersistentIngredients(recipe2);
    
    recipeRepository.save(recipe2);
    
    PersistentRecipe recipe3 = TestHelper.createPersistentRecipe("Title", 1, false, "Do something");
    
    recipeRepository.save(recipe3);
    
    em.flush();
  }
  
  @Test
  void shouldFindAllIngredientsWithoutQuery() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder();
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(3);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(3l);
  }
  
  @Test
  void shouldOnlyFindVegetarianRecipeWhenQueried() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder().vegetarian(true);
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(1);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(1l);
  }
  
  @Test
  void shouldOnlyFindRecipeWithMatchingServings() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder().servings(2);
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(1);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(1l);
  }
  
  @Test
  void shouldOnlyFindRecipeWithMatchingText() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder().text("something");
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(2);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(2l);
  }
  
  @Test
  void shouldOnlyFindRecipeWithWantedIngredients() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder().withIngredients(List.of("salt", "chili"));
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(1);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(1l);
  }
  
  @Test
  void shouldNotFindRecipeWithUnwantedIngredients() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder().withoutIngredients(List.of("chili", "rice"));
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(2);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(2l);
  }
  
  @Test
  void completeSearchFilter() {
    RecipeQueryBuilder builder = new RecipeQueryBuilder().servings(3).vegetarian(false).text("something").withIngredients(List.of("salt", "onion")).withoutIngredients(List.of("chili", "rice", "curry", "water"));
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).getResultList();
    assertThat(resultList).hasSize(1);
    
    Long resultCount = builder.buildCount(em).getSingleResult();
    assertThat(resultCount).isEqualTo(1l);
  }
}
