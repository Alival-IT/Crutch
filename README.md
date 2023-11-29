
[![](https://jitpack.io/v/Alival-IT/Crutch.svg)](https://jitpack.io/#Alival-IT/Crutch)


# Crutch

Android library with some common used features

# Logs
Currently, just a wrapper over [Timber](https://github.com/JakeWharton/timber), but in the future it might be swapped to support KMM.

###  Basic setup
```kotlin
Logs.init(enabledDefaultLogs = true, customLogs = null)
```

### Logging
```kotlin
Logs.dm(tag = "tag-dm") { "dmMessage" }  
Logs.em { "dmMessage" }  
Logs.wm(tag = "tag-wm") { "dmMessage" }  
Logs.dt { IllegalStateException("dtMessage") }  
Logs.et(tag = "tag-et") { IllegalStateException("etMessage") }  
Logs.wt(tag = "tag-wt") { IllegalStateException("wtMessage") }
```

### Advanced setup
There are some cases when you want to log the error or any other logs to some analytic tool so you can later analyze the issues.
```kotlin
Logs.init(  
	enabledDefaultLogs = false,  
	customLogs = object : CustomLogs {  // you optionally override any method from the interface
		override fun et(tag: String?, throwable: () -> Throwable) {  
			super.et(tag, throwable)  
			yourAppAnalyticsTool.logCustomCrash(throwable())  
		}  
  
		override fun em(tag: String?, message: () -> String) {  
			super.em(tag, message)  
			yourAppAnalyticsTool.logMessage(message())  
		}  
  
		override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {  
			super.log(priority, tag, message, t)  
			yourAppAnalyticsTool.log(priority, tag, message, t)  
		}  
	}  
)
```

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


# Roadmap

- Logger  :heavy_check_mark:
- StringResources  :heavy_check_mark:
- CacheableAPI
- Pager