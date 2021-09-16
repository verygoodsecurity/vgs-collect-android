## Migration Guides

### Migrating from versions < v1.6.14

Field state will be valid if no validation rules defined.

### Migrating from versions < v1.6.5

#### VGSEditText
Remove `app:fieldType` attributes from your XML layouts.

Remove `setFieldType` method from code in case you use it.

Please use `VGSCardNumberEditText`, `ExpirationDateEditText`, `CardVerificationCodeEditText`, `PersonNameEditText`, `SSNEditText`
specific fields instead of `app:fieldType` attribute or `FieldType` enum class.

Field which doen't have validation rules will have invalid state, by default.
In case you don't need validation for VGSEditText, then set `app:enableValidation="false"` in XML.


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
