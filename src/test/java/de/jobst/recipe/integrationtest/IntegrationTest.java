package de.jobst.recipe.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.jobst.recipe.RecipeApiApplication;
import de.jobst.recipe.TestHelper;
import de.jobst.recipe.mapper.RecipePersistentRecipeMapper;
import de.jobst.recipe.model.PersistentIngredient;
import de.jobst.recipe.model.PersistentRecipe;
import de.jobst.recipe.model.generated.Ingredient;
import de.jobst.recipe.model.generated.Recipe;
import de.jobst.recipe.model.generated.RecipeFilterResponse;
import de.jobst.recipe.repository.RecipeRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RecipeApiApplication.class })
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class IntegrationTest {
  
  @Autowired
  WebApplicationContext webApplicationContext;
  
  @Autowired
  RecipeRepository recipeRepository;
  
  @Autowired
  EntityManager em;
  
  MockMvc mockMvc;
  
  ObjectMapper mapper = new ObjectMapper();
  ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
  ObjectReader or = mapper.reader();
  
  @BeforeEach
  void setup() throws Exception {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }
  
  @Test
  void checkThatRecipeControllerExists() {
      ServletContext servletContext = webApplicationContext.getServletContext();
      
      assertThat(servletContext).isNotNull();
      assertThat(servletContext).isInstanceOf(MockServletContext.class);
      assertThat(webApplicationContext.getBean("recipeController")).isNotNull();
  }
  
  @Test
  void getShouldFailWith404WhenRecipeNotFound() throws Exception {
    this.mockMvc.perform(get("/recipe/" + UUID.randomUUID())).andDo(print())
      .andExpect(status().is(404));
  }
  
  @Test
  void getShouldFindExistingRecipe() throws Exception {
    PersistentRecipe recipe = TestHelper.createPersistentRecipe("Title", 3, false, "Instruction");
    PersistentIngredient ingredient = TestHelper.createPersistentIngredient("salt", "tsp", 1.1);
    recipe.addPersistentIngredient(ingredient);
    ingredient.setPersistentRecipe(recipe);
    recipeRepository.saveAndFlush(recipe);
    
    String recipeJson = ow.writeValueAsString(RecipePersistentRecipeMapper.INSTANCE.persistentRecipeToRecipe(recipe));
    
    this.mockMvc.perform(get("/recipe/" + recipe.getId())).andDo(print())
      .andExpect(status().is(200))
      .andExpect(content().json(recipeJson));
  }
  
  @Test
  void deleteShouldFailWith404WhenRecipeNotFound() throws Exception {
    this.mockMvc.perform(delete("/recipe/" + UUID.randomUUID())).andDo(print())
      .andExpect(status().is(404));
  }
  
  @Test
  void deleteShouldDeleteExistingRecipe() throws Exception {
    PersistentRecipe recipe = TestHelper.createPersistentRecipe("Title", 3, false, "Instruction");
    PersistentIngredient ingredient = TestHelper.createPersistentIngredient("salt", "tsp", 1.1);
    recipe.addPersistentIngredient(ingredient);
    ingredient.setPersistentRecipe(recipe);
    recipeRepository.saveAndFlush(recipe);
    
    assertThat(recipeRepository.count()).isEqualTo(1l);
    
    this.mockMvc.perform(delete("/recipe/" + recipe.getId())).andDo(print())
      .andExpect(status().is(200));
    
    assertThat(recipeRepository.count()).isEqualTo(0l);
  }
  
  @Test
  void updateShouldFailWith404WhenRecipeNotFound() throws Exception {
    Recipe recipe = TestHelper.createRecipe("Title", 3, false, "Instruction");
    this.mockMvc.perform(put("/recipe/" + UUID.randomUUID()).content(ow.writeValueAsString(recipe)).contentType(MediaType.APPLICATION_JSON)).andDo(print())
      .andExpect(status().is(404));
  }
  
  @Test
  void updateShouldCorreclyUpdateEverything() throws Exception {
    PersistentRecipe recipe = TestHelper.createPersistentRecipe("Title", 3, false, "Instruction");
    PersistentIngredient ingredient = TestHelper.createPersistentIngredient("salt", "tsp", 1.1);
    recipe.addPersistentIngredient(ingredient);
    ingredient.setPersistentRecipe(recipe);
    recipeRepository.saveAndFlush(recipe);
    
    // the updated recipe is created once as parameter...
    Recipe changedRecipe = TestHelper.createRecipe("Title", 3, false, "Instruction");
    Ingredient changedIngredient1 = TestHelper.createIngredient("salt", "tbsp", 2.1);
    Ingredient changedIngredient2 = TestHelper.createIngredient("pepper", "tsp", 3.);
    changedRecipe.setIngredients(List.of(changedIngredient1, changedIngredient2));
    
    // and once as expected response with the correct id
    Recipe changedRecipeWithId = TestHelper.createRecipe("Title", 3, false, "Instruction");
    changedRecipeWithId.setId(recipe.getId());
    changedRecipeWithId.setIngredients(List.of(changedIngredient1, changedIngredient2));
    
    String recipeJson = ow.writeValueAsString(changedRecipeWithId);
    
    this.mockMvc.perform(put("/recipe/" + recipe.getId()).content(ow.writeValueAsString(changedRecipe)).contentType(MediaType.APPLICATION_JSON)).andDo(print())
      .andExpect(status().is(200))
      .andExpect(content().json(recipeJson));
  }
  
  @Test
  void createShouldCorrectlyCreateTheRecipe() throws Exception {
    Recipe recipe = TestHelper.createRecipe("Title", 3, false, "Instruction");
    Ingredient ingredient1 = TestHelper.createIngredient("salt", "tbsp", 2.1);
    Ingredient ingredient2 = TestHelper.createIngredient("pepper", "tsp", 3.);
    recipe.setIngredients(List.of(ingredient1, ingredient2));
    
    MvcResult result = this.mockMvc.perform(post("/recipe").content(ow.writeValueAsString(recipe)).contentType(MediaType.APPLICATION_JSON)).andDo(print())
      .andExpect(status().is(201))
      .andReturn();
    Recipe resultRecipe = or.readValue(result.getResponse().getContentAsString(), Recipe.class);
    
    assertThat(resultRecipe.getId()).isNotNull();
    assertThat(resultRecipe)
      .returns("Title", Recipe::getTitle)
      .returns(3, Recipe::getServings)
      .returns(false, Recipe::getVegetarian)
      .returns("Instruction", Recipe::getInstructions);
    assertThat(resultRecipe.getIngredients()).hasSize(2);
    
    assertThat(recipeRepository.count()).isEqualTo(1l);
  }
  
  @Test
  void searchShouldWorkCorrectly() throws Exception {
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
    
    MvcResult result = this.mockMvc.perform(get("/recipe").param("page", "0").param("size", "5").param("vegetarian", "false").param("ingredientsIncluded", "salt", "onion")).andDo(print())
        .andExpect(status().is(200))
        .andReturn();
    
    RecipeFilterResponse resultList = or.readValue(result.getResponse().getContentAsString(), RecipeFilterResponse.class);
    assertThat(resultList.getContent()).hasSize(1);
    assertThat(resultList.getPageMetadata().getNumber()).isEqualTo(0);
    assertThat(resultList.getPageMetadata().getSize()).isEqualTo(5);
    assertThat(resultList.getPageMetadata().getTotalElements()).isEqualTo(1);
    assertThat(resultList.getPageMetadata().getTotalPages()).isEqualTo(1);
  }
  
  
}
