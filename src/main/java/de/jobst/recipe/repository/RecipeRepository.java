package de.jobst.recipe.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.jobst.recipe.model.PersistentRecipe;

public interface RecipeRepository extends JpaRepository<PersistentRecipe, UUID> {
  
  /**
   * Delete by id while returning the deleted row count.
   * @param id id of the recipe to delete
   * @return count of deleted rows (should be 1 or 0)
   */
  @Modifying(clearAutomatically = true)
  @Query("delete from PersistentRecipe where id = :id")
  public int deleteByIdIs(@Param("id") UUID id);
}
