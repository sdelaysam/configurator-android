# Configurator

Ideas behind this library:
- breakdown UI logic into small reusable components
- let view model construct and control them
- allow `Activity`, `Fragment` or `RecyclerView` to use them
- make them reactive and lifecycle-aware

### Building blocks

`Configurator` is self-explanatory interface with two methods - `configure` and `reset`.
```kotlin
interface Configurator<T> {
    fun configure(target: T)
    fun reset(target: T)
}
```
Target `T` is not constrained - it can be a `View`, a `ViewBinding` or anything else depending on your needs.

To make `Configurator` reactive, one should implement `RequiresCoroutineScope` interface (only `Flow` is supported atm) or subclass abstract `CoroutineConfigurator`.

`Configurator` can also be stateful: save and restore its target state as `Parcelable`. Most of the time its not needed - configurator itself is a data holder and configurator's target (let's say a `View`) is saving its state automatically being in the view hierarchy.
But sometimes, being in a `RecyclerView.ViewHolder`, configurator may want to save its target state. To let this happen, configurator be a `StatefulConfigurator<T>`.

### Working with RecyclerView

`Configurator` is meant to be a data holder. View model can expose a list of `Configurator` to feed it to `RecyclerViewAdapter` - built-in adapter for the `RecyclerView`.
To make it work, one should implement `AdapterEntry` interface on a configurator.
`AdapterEntry` will let adapter identify an item and build proper `ViewHolder`
```kotlin
interface AdapterEntry {
    val viewType: Int
    val contentId: Int
    val contentHash: Int
}
```
`viewType` - identifier to find proper match against `ViewHolder.Factory` (will be shown later).
`contentId` and `contentHash` are used by `DiffCallback`.

Basic configurator for `RecyclerView` can look like
```kotlin
@GenerateViewHolder
class RowConfigurator(
    override val contentId: Int,
    private val text: String
) : AdapterBasicConfigurator<TextView>() {

    override val viewType: Int = VIEW_TYPE_TEXT // arbitraty constant
    override val contentHash: Int = text.hashCode()

    override fun onConfigure(target: TextView) {
        target.text = text
    }
}
```
First, configurator subclasses `AdapterBasicConfigurator` - basically its just a `Configurator` plus  `AdapterEntry`. And second, configurator is annotated with `GenerateViewHolder` annotation.

#### Generate view holder

Although library provides basic implementation for a `RecyclerView.ViewHolder` which works with configurators (see `ConfiguratorViewHolder`), it requires some boilerplate code to create a viewholder for each configurator. This is why `GenerateViewHolder` is here.
It tells annotation process to generate the following code:
```kotlin
public class RowViewHolder(
  view: TextView
) : ConfiguratorViewHolder<TextView, RowConfigurator>(view, view) {
  public class Factory : BasicViewHolder.Factory {
    public override val viewType: Int = 0 // value of VIEW_TYPE_TEXT

    public override fun create(parent: ViewGroup): BasicViewHolder {
      val view = TextView(parent.context)
      return RowViewHolder(view)
    }
  }
}
```

All you have to do now, is register this view holder. Now, adapter is ready to render your configurator. This is how it may look like
```kotlin
// view model
val items = listOf(
    RowConfigurator(0, "Row 0"),
    RowConfigurator(1, "Row 1"),
    RowConfigurator(2, "Row 2")
)

// screen
val adapter = RecyclerViewAdapter()
adapter.register(RowViewHolder.Factory())
adapter.reload(viewModel.items)

recyclerView.adapter = adapter
```

For more complex and detailed example please refer to the sample app.

### Working with ViewGroup

Similarly, it is possible to use configurators to configure child views of some layout.
It can be done using `ViewGroupAdapter` and same `GenerateViewHolder` annotation, but with a special type
```kotlin
@GenerateViewHolder(type = ViewHolderType.VIEW_GROUP)
class RowConfigurator
```

Then, tie everything together
```kotlin

val adapter = LinearLayoutAdapter()
adaper.register(RowViewHolder.Factory())
adapter.reload(viewModel.items)

adapter.attach(linearLayout)
```

### What else

There is built-in support for [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) library.


### Artefacts

```gradle
// core
implementation "com.github.sdelaysam.configurator-android:core:$latest_version"

// adapter for RecyclerView
implementation "com.github.sdelaysam.configurator-android:adapter-recyclerview:$latest_version"

// adapter for ViewGroup
implementation "com.github.sdelaysam.configurator-android:adapter-viewgroup:$latest_version"

// adapter for Paging3
implementation "com.github.sdelaysam.configurator-android:adapter-paging:$latest_version"

// annotation processor (requires 'kotlin-kapt' plugin)
implementation "com.github.sdelaysam.configurator-android:processor:$latest_version"
kapt "com.github.sdelaysam.configurator-android:processor:$latest_version"

```

### License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```