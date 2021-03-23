## Migration Guides

### Migrating from versions < v1.6.2

### Additional data
Previously, a `Key` in static or dynamic additional data could be converted into other data structures. From now the `Key` can be only as a `String`. 

Let's see how to get of the following result:
```
{
  "data": {
    "content": "content_data"
  }
}
```

**Before:**

```
val staticData = mutableMapOf<String, Any>()
staticData["data.content"] = "content_data"
```

**Now:**

```
val staticData = mutableMapOf<String, Any>()

val content = mutableMapOf( "content" to "content_data" )
staticData["data"] = content
```
