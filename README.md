# livedata-ext
Android LiveData extension, including FreshLiveData and etc.

# Download

```groovy
repositories {
    jcenter()
}
dependencies {
    implementation 'com.github.kxfeng:livedata-ext:0.9.0'
}
```

## Usage

### FreshLiveData
Extension functions to observe freshly, which means the observer will only receive new value after now, previous value won't dispatch to it.

- Kotlin usage
```kotlin
val liveData = MutableLiveData<String>()

liveData.observeFreshly(lifecycleOwner, Observer {
    println("Fresh value: $it")
})

val observer = Observer<String> {
    println("Fresh value: $it")
}

liveData.observeForeverFreshly(observer)

liveData.removeObserverFreshly(observer)
```

- Simple test case
```kotlin
val liveData = MutableLiveData<String>()
liveData.value = "Hello"

val freshResult = mutableListOf<String>()
val normalResult = mutableListOf<String>()

liveData.observeForeverFreshly(Observer<String> {
    freshResult.add(it)
})

liveData.observeForever(Observer {
    normalResult.add(it)
})

liveData.value = "World"

assertEquals(listOf("World"), freshResult)
assertEquals(listOf("Hello", "World"), normalResult)
```

- Java usage
```Java
LiveData<String> liveData = new MutableLiveData<>();
FreshLiveDataKt.observeForeverFreshly(liveData, msg -> {
    System.out.println("Fresh value: " + msg);
});
```

## License

    Copyright 2019 kxfeng

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
