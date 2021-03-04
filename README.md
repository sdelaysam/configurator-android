# Configurator

Ideas behind this library:
- breakdown UI logic into small reusable components
- let view model construct and control them
- allow `Activity`, `Fragment` or `RecyclerView` use them
- make them reactive and lifecycle-aware

### Building blocks

`Configurator` is simply an interface:
```kotlin
interface Configurator<T> {
    fun configure(target: T)
    fun reset(target: T)
}
```
Target `T` is not constrained: it can be a `View`, a `ViewBinding` or anything else depending on your needs.

Reactive `Configurator` implements `RequiresCoroutineScope` and defined as abstract  `CoroutineConfigurator<T>` (only `Flow` is supported atm).

### Working with RecyclerView

To use configurator in `RecyclerView`, first make it an `AdapterEntry` (or subclass either  `AdapterBasicConfigurator<T>` or `AdapterCoroutineConfigurator<T>`):

```kotlin
interface AdapterEntry {
    val viewType: Int
    val contentId: Int
    val contentHash: Int
}
```
where `viewType` is a unique constant identifiying the view (could be `R.layout.id` or any other final constant). `contentId` and `contentHash` are used by `DiffCallback`.

Basic configurator for `RecyclerView` can look like the following:
```kotlin
@GenerateViewHolder
class RowConfigurator(
    override val contentId: Int,
    private val text: String
) : AdapterBasicConfigurator<TextView>() {

    override val viewType: Int = VIEW_TYPE_TEXT
    override val contentHash: Int = text.hashCode()

    override fun onConfigure(target: TextView) {
        target.text = text
    }
}
```
As you might have noticed, configurator is annotated with `GenerateViewHolder` annotation.

#### Generate view holder

Although library provides basic implementation for a `RecyclerView.ViewHolder` - `ConfiguratorViewHolder` - it requires to write some boilerplate code to create a viewholder for each configurator. So you can use `GenerateViewHolder`to generate this boilerplate:
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

Then you register `ViewHolder.Factory` in adapter and its ready to go:
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

Please see the complete example in the sample app.

### Working with ViewGroup

Similarly, it is possible to use configurators to configure child views of some layout.
It can be done using `ViewGroupAdapter` and same `GenerateViewHolder` annotation, but with a special type:
```kotlin
@GenerateViewHolder(type = ViewHolderType.VIEW_GROUP)
class RowConfigurator
```

Then tie everything together:
```kotlin

val adapter = LinearLayoutAdapter()
adaper.register(RowViewHolder.Factory())
adapter.reload(viewModel.items)

adapter.attach(linearLayout)
```

### What else

There is built-in support for [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) library.


### Artifacts

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