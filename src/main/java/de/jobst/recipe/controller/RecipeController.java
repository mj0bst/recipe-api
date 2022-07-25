package de.jobst.recipe.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.jobst.recipe.mapper.RecipePersistentRecipeMapper;
import de.jobst.recipe.model.generated.PageMetadata;
import de.jobst.recipe.model.generated.Recipe;
import de.jobst.recipe.model.generated.RecipeFilterResponse;
import de.jobst.recipe.service.RecipeService;

/**
 * Controller for managing {@link Recipe Recipes}.
 */
@RestController
@RequestMapping(path = "/recipe", produces = "application/json")
public class RecipeController {
  
  private RecipeService recipeService;

  @Autowired
  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
    
  }
  
  /**
   * Search method with pagination.
   * @param pageable Page information
   * @param vegetarian whether to return only (non-)vegetarian recipes
   * @param servings restrict servings count
   * @param text full text search in ingredients
   * @param ingredientsIncluded list of must-have ingredients
   * @param ingredientsExcluded list of forbidden ingredients
   * @return matching results with pagination information
   */
  @GetMapping
  public HttpEntity<RecipeFilterResponse> search(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) Boolean vegetarian,
      @RequestParam(required = false) Integer servings,
      @RequestParam(required = false) String text,
      @RequestParam(required = false) List<String> ingredientsIncluded,
      @RequestParam(required = false) List<String> ingredientsExcluded) {
        Page<Recipe> recipes = recipeService.search(pageable, vegetarian, servings, text, ingredientsIncluded, ingredientsExcluded).map(RecipePersistentRecipeMapper.INSTANCE::persistentRecipeToRecipe);
        RecipeFilterResponse response = createRecipeFilterResponse(recipes);
        return ResponseEntity.ok(response);
  }

  private RecipeFilterResponse createRecipeFilterResponse(Page<Recipe> recipes) {
    RecipeFilterResponse response = new RecipeFilterResponse();
    response.setContent(recipes.getContent());
    PageMetadata pageMetadata = new PageMetadata();
    pageMetadata.setNumber(recipes.getNumber());
    pageMetadata.setSize(recipes.getSize());
    pageMetadata.setTotalElements(recipes.getTotalElements());
    pageMetadata.setTotalPages(recipes.getTotalPages());
    response.setPageMetadata(pageMetadata);
    return response;
  }
  
  /**
   * Create new recipe.
   * @param recipe new recipe
   * @return created recipe
   */
  @PostMapping
  public HttpEntity<Recipe> create(@RequestBody @Valid Recipe recipe) {
    return new ResponseEntity<>(
        RecipePersistentRecipeMapper.INSTANCE.persistentRecipeToRecipe(
            recipeService.create(RecipePersistentRecipeMapper.INSTANCE.recipeToPersistentRecipe(recipe))),
        HttpStatus.CREATED);
  }
  
  /**
   * Get a recipe by id. Returns a 404 if not found.
   * @param id id of the recipe
   * @return found recipe
   */
  @GetMapping("/{id}")
  public HttpEntity<Recipe> read(@PathVariable UUID id) {
    return ResponseEntity.of(recipeService.read(id)
        .map(RecipePersistentRecipeMapper.INSTANCE::persistentRecipeToRecipe));
  }
  
  /**
   * Delete a recipe by id. Returns a 404 if not found.
   * @param id id of the recipe
   * @return nothing
   */
  @DeleteMapping("/{id}")
  public HttpEntity<Void> delete(@PathVariable UUID id) {
    long deleteCount = recipeService.delete(id);
    if(deleteCount == 1l) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
  
  /**
   * Update a recipe by id. Returns a 404 if not found.
   * @param id id of the recipe
   * @param recipe changed recipe
   * @return updated recipe
   */
  @PutMapping("/{id}")
  public HttpEntity<Recipe> update(@PathVariable UUID id, @RequestBody Recipe recipe) {
    return ResponseEntity.of(recipeService.update(id, RecipePersistentRecipeMapper.INSTANCE.recipeToPersistentRecipe(recipe))
        .map(RecipePersistentRecipeMapper.INSTANCE::persistentRecipeToRecipe));
  }
  
}
