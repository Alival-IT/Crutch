# Logs
Currently, just a wrapper over [Timber](https://github.com/JakeWharton/timber).

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