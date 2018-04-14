package forpdateam.ru.forpda.presentation.favorites

import forpdateam.ru.forpda.common.mvp.IBaseView
import forpdateam.ru.forpda.entity.remote.favorites.FavData
import forpdateam.ru.forpda.entity.remote.favorites.FavItem
import forpdateam.ru.forpda.model.data.remote.api.favorites.Sorting

/**
 * Created by radiationx on 01.01.18.
 */

interface FavoritesView : IBaseView {
    fun initSorting(sorting: Sorting)
    fun onLoadFavorites(data: FavData)
    fun onShowFavorite(items: List<FavItem>)
    fun onHandleEvent(count: Int)
    fun showItemDialogMenu(item: FavItem)
    fun onChangeFav(result: Boolean)
    fun showSubscribeDialog(item: FavItem)
    fun onMarkAllRead()
}
