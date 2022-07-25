package de.jobst.recipe.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.jobst.recipe.model.PersistentRecipe;
import de.jobst.recipe.model.generated.Recipe;

/**
 * Mapper class using mapstruct for automatically mapping from {@link PersistentRecipe} to {@link Recipe} and vice versa.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecipePersistentRecipeMapper {
  
  RecipePersistentRecipeMapper INSTANCE = Mappers.getMapper(RecipePersistentRecipeMapper.class);
  
  @Mapping(target = "ingredients", source = "persistentIngredients")
  public Recipe persistentRecipeToRecipe(PersistentRecipe persistentRecipe);
  
  @InheritInverseConfiguration
  public PersistentRecipe recipeToPersistentRecipe(Recipe recipe);
}
