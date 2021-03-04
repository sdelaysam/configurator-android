package org.sdelaysam.configurator

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateViewHolder(val type: ViewHolderType = ViewHolderType.RECYCLER_VIEW)