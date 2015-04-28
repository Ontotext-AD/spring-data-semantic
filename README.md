spring-data-semantic
====================

Basic usage
-----------
**TODO:**

Filtering values
----------------
Two steps are needed to use the value filters:

1. Make your repository interfaces extend `FilteringSemanticRepository<T>` instead of `SemanticRepository<T>`
2. Add `factory-class="org.springframework.data.semantic.repository.FilteringSemanticRepositoryFactoryBean"` attribute to `<semantic:repositories>` in your
application context. E.g.```
<semantic:repositories base-package="com.example.repositories"
                       factory-class="org.springframework.data.semantic.repository.FilteringSemanticRepositoryFactoryBean" />
```
3. Use `findOne(URI, ValueFilter)` like this:
```
obj = findOne(id, hasLang("en")); // the entity's string properties will be filled with only "en" literals
```
(`hasLang` is a method of the `ValueFilters` class)
