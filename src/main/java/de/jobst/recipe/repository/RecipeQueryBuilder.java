package de.jobst.recipe.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.jobst.recipe.model.PersistentRecipe;

/**
 * Builder class for building a {@link TypedQuery} for both retrieving search results as well as getting their whole count.
 */
public class RecipeQueryBuilder {
  
  private List<String> withIngredients;
  private List<String> withoutIngredients;
  private Integer servings;
  private Boolean vegetarian;
  private String text;

  public RecipeQueryBuilder withIngredients(List<String> ingredients) {
    this.withIngredients = ingredients;
    return this;
  }
  
  public RecipeQueryBuilder withoutIngredients(List<String> ingredients) {
    this.withoutIngredients = ingredients;
    return this;
  }
  
  public RecipeQueryBuilder servings(Integer servings) {
    this.servings = servings;
    return this;
  }

  public RecipeQueryBuilder vegetarian(Boolean vegetarian) {
    this.vegetarian = vegetarian;
    return this;
  }
  
  public RecipeQueryBuilder text(String text) {
    this.text = text;
    return this;
  }
  
  /**
   * Prepares a query for recipes matching the given data with safe query parameters.
   * All parameters are checked for their existence and when present added to the query.
   * @param em currently active EntityManager which should execute the query
   * @return fully prepared query
   */
  public TypedQuery<PersistentRecipe> buildRetrieve(EntityManager em) {
    return buildInternal(em, false, PersistentRecipe.class);
  }
  
  /**
   * Prepares a query for counting the entries matching the given data with safe query parameters.
   * @param em currently active EntityManager which should execute the query
   * @return fully prepared query
   */
  public TypedQuery<Long> buildCount(EntityManager em) {
    return buildInternal(em, true, Long.class);
  }

  private <T> TypedQuery<T> buildInternal(EntityManager em, boolean count, Class<T> clazz) {
    boolean hasWhere = false;
    List<Object> parameters = new ArrayList<>();
    
    StringBuilder query = new StringBuilder("select ");
    if(count) {
      query.append("count(r) ");
    } else {
      query.append("r ");
    }
    query.append("from PersistentRecipe r ");
    
    if(withIngredients != null) {
      for(String withIngredient : withIngredients) {
        hasWhere = addWhereOrAnd(hasWhere, query);
        query.append("""
          exists(
            select i from PersistentIngredient i where
              i.persistentRecipe = r and i.name = ?
          ) 
        """.replace("?", "?" + parameters.size()));
        parameters.add(withIngredient);
      }
    }
    
    if(withoutIngredients != null) {
      for(String withoutIngredient : withoutIngredients) {
        hasWhere = addWhereOrAnd(hasWhere, query);
        query.append("""
          not exists(
            select i from PersistentIngredient i where
              i.persistentRecipe = r and i.name = ?
          ) 
        """.replace("?", "?" + parameters.size()));
        parameters.add(withoutIngredient);
      }
    }
    
    if(servings != null) {
      hasWhere = addWhereOrAnd(hasWhere, query);
      query.append("r.servings = ?" + parameters.size());
      parameters.add(servings);
    }
    
    if(vegetarian != null) {
      hasWhere = addWhereOrAnd(hasWhere, query);
      query.append("r.vegetarian = ?" + parameters.size());
      parameters.add(vegetarian);
    }
    
    if(text != null) {
      hasWhere = addWhereOrAnd(hasWhere, query);
      query.append("r.instructions like concat('%',? ,'%')".replace("?", "?" + parameters.size()));
      parameters.add(text);
    }
    TypedQuery<T> createdQuery = em.createQuery(query.toString(), clazz);
    
    for(int i = 0; i < parameters.size(); i++) {
      createdQuery.setParameter(i, parameters.get(i));
    }
    
    return createdQuery;
  }

  private boolean addWhereOrAnd(boolean hasWhere, StringBuilder query) {
    if(!hasWhere) {
      query.append("where ");
    } else {
      query.append("and ");
    }
    return true;
  }
}
