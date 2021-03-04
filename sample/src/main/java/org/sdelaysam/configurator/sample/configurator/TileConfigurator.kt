package org.sdelaysam.configurator.sample.configurator

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.squareup.picasso.Picasso
import org.sdelaysam.configurator.GenerateViewHolder
import org.sdelaysam.configurator.ViewHolderType
import org.sdelaysam.configurator.adapter.AdapterBasicConfigurator
import org.sdelaysam.configurator.sample.VIEW_TYPE_TILE
import org.sdelaysam.configurator.sample.data.Tile
import org.sdelaysam.configurator.sample.databinding.ViewTileBinding
import org.sdelaysam.configurator.sample.util.dp
import org.sdelaysam.configurator.sample.util.setSize

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

@GenerateViewHolder(type = ViewHolderType.ALL)
class TileConfigurator(
    private val tile: Tile,
    private val isSmall: Boolean
) : AdapterBasicConfigurator<ViewTileBinding>() {

    override val viewType: Int = VIEW_TYPE_TILE
    override val contentId: Int = tile.id
    override val contentHash: Int = tile.hashCode()

    override fun onConfigure(target: ViewTileBinding) {
        if (isSmall) {
            target.root.setSize(dp(250), dp(200))
        } else {
            target.root.setSize(MATCH_PARENT, dp(350))
        }
        target.textView.text = tile.title
        Picasso.get().load(tile.imageUrl).into(target.imageView)
    }
}