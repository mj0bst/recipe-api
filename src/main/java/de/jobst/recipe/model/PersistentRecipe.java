package de.jobst.recipe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "recipe")
public class PersistentRecipe {
  
  @Id
  @GeneratedValue
  @Column(name = "id", columnDefinition = "binary(16)", nullable = false)
  private UUID id;

  @Column(name = "title", columnDefinition = "varchar(255)",  nullable = false)
  private String title;

  @Column(name = "servings", nullable = false)
  private Integer servings;

  @Column(name = "vegetarian", nullable = false)
  private Boolean vegetarian;

  @Column(name = "instructions", columnDefinition = "text",  nullable = false)
  private String instructions;
  
  @OneToMany(mappedBy = "persistentRecipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PersistentIngredient> persistentIngredients;
  
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getServings() {
    return servings;
  }

  public void setServings(Integer servings) {
    this.servings = servings;
  }

  public Boolean getVegetarian() {
    return vegetarian;
  }

  public void setVegetarian(Boolean vegetarian) {
    this.vegetarian = vegetarian;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public List<PersistentIngredient> getPersistentIngredients() {
    return persistentIngredients;
  }

  public void setPersistentIngredients(List<PersistentIngredient> persistentIngredients) {
    this.persistentIngredients = persistentIngredients;
  }
  
  public void addPersistentIngredient(PersistentIngredient persistentIngredient) {
    if(persistentIngredients == null) {
      persistentIngredients = new ArrayList<>();
    }
    persistentIngredients.add(persistentIngredient);
  }
}
