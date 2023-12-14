# States

## Basic usage

### 1. Extend States

**Using ViewModel:**

```kotlin
sealed class ExampleEvents : StatesOneTimeEvents {
    data object SnackBar : ExampleEvents()
}

data class ExampleViewState(val state: String)

class ExampleViewModel : StatesViewModel<ExampleViewState>(ExampleViewState("example")) {
    
    init {
        // You should register your states and events in init{} block. Default state is automatically registered for StatesViewModel only. 
        registerCustomEvent<ExampleEvents>()
    }

    fun onStateClicked() = launchOnDefault {
        emitViewState {
            it.copy(state = it.state + "x")
        }
    }
}
```

**Using own screen models**

This can be used if you use for
example [Voyager](https://voyager.adriel.cafe/screenmodel/coroutines-integration).

```kotlin
sealed class ExampleEvents : StatesOneTimeEvents {
    data object SnackBar : ExampleEvents()
}

data class ExampleViewState(val state: String)

class ExampleScreenModel : States<ExampleViewState> {

    // you have to provide StatesStreamsContainer with a scope States operates on
    override val statesStreamsContainer: StatesStreamsContainer = StatesStreamsContainer(scope)
    
    init { 
        // you have to manually register states and events, also default viewState
        registerCustomViewState(ExampleViewState("Example"))
        registerCustomEvent<ExampleEvents>()
    }

    fun onStateClicked() = launchOnDefault {
        emitViewState {
            it.copy(state = it.state + "x")
        }
    }
}
```

### 2. Observe state in your composable screens

Provide your viewModel or other Model solution extending States to your screen. Then you
can call observe methods to observe viewStates and Events.

```kotlin
@Composable
fun ExampleScreen(exampleScreenModel: ExampleScreenModel) {
    val vs = exampleScreenModel.observeViewState()

    exampleScreenModel.observeEvents<ExampleEvents>(onEvent = {
        when (it) {
            ExampleEvents.NavigateToAnotherScreen -> {
                //NavigateToAnotherScreen   
            }
        }
    })

    Text(
        modifier = Modifier.clickable { exampleScreenModel.onStateClicked() },
        text = "Hello ${vs.state}"
    )
}
```

### 3. Emitting viewState and events

You can use extension methods of States interface to emit events or viewState.
Emitting should happen inside a coroutine. You can use one of the provided methods to launch a
coroutine
for each action: **launchOnIo, launchOnDefault, launchOnUnconfined, launchOnMain,
launchOnMainImmediate**.

```kotlin
    fun example() = launchOnDefault {
    emitViewState {
        it.copy(state = it.state + "x")
    }
    emitEvent(ExampleEvents.NavigateToAnotherScreen)
    emitViewStateWithType<String> { "new State" }
}
```

## Advanced usage and topics

### Registering events and states

States are based on a map of streams and types. Each type that is registered with
`registerCustomViewState` or `registerCustomEvent` is stored
as a pair of KClass to Stream and can be observed by its type.
You can register multiple states and events. For StatesViewModel, default viewState is automatically registered.
For States, you have to manually register every state and event, even the default one.

### Testing

With `StatesTestManager` you can switch the scope of all launchOn* methods to main if you set
isRunningInTests to true.

**Turbine**
Most convenient method for testing is using [Turbine](https://github.com/cashapp/turbine) library.

```kotlin
findViewStateStreamByType<SideMenuViewState>()?.stream?.test {
    // testing here
}
```

**EmitterCollector**
Another way to test is using `EmitterCollector` in  `StatesTestManager`. This collector will collect
every state and event
that is emitted by States. You can implement your own `EmitterCollector` or use a ready
made `TestingEmitterCollector` and its extension functions.

```kotlin
TestingEmitterCollector.expectStatesOrEvents
TestingEmitterCollector.expectLatestStateOrEvent
```

### Searching for streams

If you need an access to a certain stream, you can find it with methods of
States `findViewStateStreamByType` and `findEventByType`.

### Getting last state

If you need an access to the last state, you can use `getLastState`.

### One time events

One time events are supported if you extend the interface `StatesOneTimeEvents`.

### Debugging and logging

If you need to enable logging for debug purposes, enable `StatesLogger.isStatesDebugModeEnabled`.

### Observing states and events

For observing states and events you can use one of the following methods.

```kotlin
observeEvents
observeViewState // for default States viewState type
observeViewStateAsState // for default States viewState type
observeViewStateWithType // for custom registered viewState
observeViewStateAsStateWithType // for custom registered viewState
```

### Emitting states and events

For emitting states and events you can use one of the following methods.

```kotlin
emitViewState // for default States viewState type
emitViewStateWithType // for custom registered viewState
emitEvent // for custom registered event
```

### Coroutine helpers

There are several helpers for coroutines.

**Launchers as extensions for States**

```kotlin
launchOnIo
launchOnDefault
launchOnUnconfined
launchOnMain
launchOnMainImmediate
```

## SavedStateHandle

Supported types: [google official doc](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate#types)

#### StatesViewModel

Pass savedStateHandle to StatesViewModel, its an optional parameter. Its also a good practice to define a custom key, so
for StatesViewModel initial state, pass a custom key as a parameter `initialStateSavedStateHandleKey`.

For custom states you can pass a custom key during registration of your state.
`registerCustomViewState(YourState(), customKeyForMyStateForSavedStateHandle)`

#### States interface

Pass savedStateHandleManager during container creation:
```kotlin
override val statesStreamsContainer: StatesStreamsContainer = StatesStreamsContainer(scope, savedStateHandleManager)
```
You can use a ready made manager `SavedStateHandleManagerImpl` or create your own based on your project setup.

For States interface you can pass a custom key during registration of your state.
`registerCustomViewState(YourState(), customKeyForMyStateForSavedStateHandle)`

#### Lazy to pass a key?

No worries, we will generate a key based on the class using reflection, check `SavedStateHandleManager.createKey`.

#### Under the hood

On each emit it saves the state to savedStateHandle or SavedStateHandleManager. 
During registration it checks if there is a previous state under the defined key, or uses the initial state for the state type. 
