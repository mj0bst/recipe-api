--liquibase formatted sql

--changeset mjobst:1-ingredient

create table ingredient (
    id binary(16) not null,
    name varchar(255) not null,
    number double not null,
    unit varchar(255),
    recipe_id binary(16) not null,
    primary key (id)
);

--changeset mjobst:1-recipe

create table recipe (
    id binary(16) not null,
    instructions text not null,
    servings integer not null,
    title varchar(255) not null,
    vegetarian boolean not null,
    primary key (id)
);

--changeset mjobst:1-ingredient_recipe

alter table ingredient
add constraint ingredient_recipe foreign key (recipe_id) references recipe (id) on delete cascade;

--changeset mjobst:1-index_recipe_vegetarian

create index recipe_vegetarian
on recipe (vegetarian)

--changeset mjobst:1-index_recipe_instructions

create index recipe_instructions
on recipe (instructions)

--changeset mjobst:1-index_recipe_servings

create index recipe_servings
on recipe (servings)

--changeset mjobst:1-index_ingredient_name

create index ingredient_name
on ingredient (name)