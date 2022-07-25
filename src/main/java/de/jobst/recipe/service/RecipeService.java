package de.jobst.recipe.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.jobst.recipe.model.PersistentRecipe;
import de.jobst.recipe.repository.RecipeQueryBuilder;
import de.jobst.recipe.repository.RecipeRepository;

/**
 * Service for managing the recipe database.
 */
@Service
@Transactional
public class RecipeService {
  
  private EntityManager em;
  private RecipeRepository recipeRepository;

  @Autowired
  public RecipeService(EntityManager em, RecipeRepository recipeRepository) {
    this.em = em;
    this.recipeRepository = recipeRepository;
  }
  
  public PersistentRecipe create(PersistentRecipe recipe) {
    recipe.getPersistentIngredients().forEach(ingredient -> ingredient.setPersistentRecipe(recipe));
    return recipeRepository.save(recipe);
  }
  
  public Optional<PersistentRecipe> read(UUID id) {
    return recipeRepository.findById(id);
  }
  
  public Optional<PersistentRecipe> update(UUID id, PersistentRecipe recipe) {
    return recipeRepository.findById(id)
      .map(existingRecipe -> {
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setServings(recipe.getServings());
        existingRecipe.setTitle(recipe.getTitle());
        // collection needs to be cleared and refilled, so that the orphans can be removed
        existingRecipe.getPersistentIngredients().clear();
        existingRecipe.getPersistentIngredients().addAll(recipe.getPersistentIngredients());
        existingRecipe.getPersistentIngredients().forEach(ingredient -> ingredient.setPersistentRecipe(existingRecipe));
        return recipeRepository.save(existingRecipe);
      });
  }
  
  public long delete(UUID id) {
    return recipeRepository.deleteByIdIs(id);
  }
  
  public Page<PersistentRecipe> search(Pageable pageable, Boolean vegetarian, Integer servings, String text, List<String> ingredientsIncluded, List<String> ingredientsExcluded) {
    RecipeQueryBuilder builder = new RecipeQueryBuilder()
        .vegetarian(vegetarian)
        .servings(servings)
        .text(text)
        .withIngredients(ingredientsIncluded)
        .withoutIngredients(ingredientsExcluded);
    
    List<PersistentRecipe> resultList = builder.buildRetrieve(em).setFirstResult(pageable.getPageNumber()*pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
    Long resultCount = builder.buildCount(em).getSingleResult();
    
    return new PageImpl<>(resultList, pageable, resultCount);
  }
}
