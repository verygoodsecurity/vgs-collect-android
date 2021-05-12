## Migration Guides

### Migrating from versions < v1.6.5

#### VGSEditText
Remove `app:fieldType` attributes from your XML layouts.

Remove `setFieldType` method from code in case you use it.

Please use `VGSCardNumberEditText`, `ExpirationDateEditText`, `CardVerificationCodeEditText`, `PersonNameEditText`, `SSNEditText`
specific fields instead of `app:fieldType` attribute or `FieldType` enum class.


### Migrating from versions < v1.6.2

#### Additional data
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
