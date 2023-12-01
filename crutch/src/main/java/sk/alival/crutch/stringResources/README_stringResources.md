# StringResource
Most of the time passing around strings, then refactoring methods to accept string resourceId with params can be annoying. For that case we came up with this wrapper class.

### Usage

**Create a StringResource**
```kotlin
StringResource.StringValueResource("Hello %s", "world")
StringResource.StringIdResource(R.string.hello, "world")
StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world))
StringResource.EmptyStringResource()
```

**Get string value**
```kotlin
StringResource.StringValueResource("Hello %s", "world").getString(null)
``` 

**Passing context**
Context is required to get the string from resources. Its required for StringIdResource. If you pass arguments as a type of StringIdResource, it is required also for StringValueResource.