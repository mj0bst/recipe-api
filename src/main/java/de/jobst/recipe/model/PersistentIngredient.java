package de.jobst.recipe.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ingredient")
public class PersistentIngredient {
  
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id", columnDefinition = "binary(16)", nullable = false)
  private UUID id;
  
  @Column(name = "name", columnDefinition = "varchar(255)", nullable = false)
  private String name;

  @Column(name = "unit", columnDefinition = "varchar(255)")
  private String unit;

  @Column(name = "number", nullable = false)
  private Double number;
  
  @ManyToOne
  @JoinColumn(name = "recipe_id", nullable = false, updatable = false)
  private PersistentRecipe persistentRecipe;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Double getNumber() {
    return number;
  }

  public void setNumber(Double number) {
    this.number = number;
  }

  public PersistentRecipe getPersistentRecipe() {
    return persistentRecipe;
  }

  public void setPersistentRecipe(PersistentRecipe persistentRecipe) {
    this.persistentRecipe = persistentRecipe;
  }
  
}
